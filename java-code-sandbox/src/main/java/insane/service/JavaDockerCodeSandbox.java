package insane.service;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.resource.ResourceUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import insane.constant.DockerConstant;
import insane.enums.ExecuteStatus;
import insane.model.ExecuteCodeRequest;
import insane.model.ExecuteCodeResponse;
import insane.model.ExecuteMessage;
import insane.utils.CheckEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 基于Docker的Java代码沙箱实现
 * 通过预创建容器池和安全配置来运行用户代码
 */
@Slf4j
//@Service
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate implements CodeSandbox {

    // Docker镜像名称，默认使用openjdk:8-jre-alpine
    @Value("${docker.imageName:openjdk:8-jre-alpine}")
    private String imageName;

    // 是否首次初始化镜像
    @Value("${docker.firstInitImage:false}")
    private boolean firstInitImage;

    // 预创建容器数量，默认50个
    @Value("${docker.PreContainers:50}")
    private int PreContainers;

    // 执行超时时间，10秒
    private static final int Time_Out = 10 * 1000;

    // 安全策略文件路径
    private final String SecurityPolicyPath = ResourceUtil.getResource(DockerConstant.SecurityPolicy).getPath();

    // Docker客户端实例
    private DockerClient dockerClient;

    // 容器池，用于复用已创建的容器
    private final LinkedBlockingQueue<String> containerPool = new LinkedBlockingQueue<>();

    // Docker主机配置，包含资源限制和安全设置
    private static final HostConfig hostConfig = new HostConfig();

    // 静态初始化块，配置容器的安全和资源限制
    static {
        hostConfig.withMemory(256 * 1024 * 1024L); // 最大内存256MB
        hostConfig.withMemorySwap(0L); // 禁止使用swap
        hostConfig.withReadonlyRootfs(false); // 根目录可写
        hostConfig.withNetworkMode("none"); // 禁用网络
        hostConfig.withPidsLimit(100L);  // 限制进程/线程数为100
        // 安全配置，使用seccomp限制系统调用
        String seccomp = ResourceUtil.readStr(DockerConstant.SecurityJson, StandardCharsets.UTF_8);
        hostConfig.withSecurityOpts(Arrays.asList("seccomp=" + seccomp));
    }

    /**
     * 容器池初始化方法
     * 在Spring Bean创建后自动执行
     */
    @PostConstruct
    private void initContainerPool() {
        PreContainers = Math.max(PreContainers, 1); // 至少预创建1个容器
        // 检查Docker环境是否可用
        boolean dockerAvailable = CheckEnvironment.isDockerValidAndLinux();
        if (!dockerAvailable) {
            log.error("Docker环境不可用, 跳过容器池初始化");
            return;
        }

        // 初始化Docker客户端
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);

        // 拉取所需的Docker镜像
        pullImage(dockerClient, imageName);
        log.info("开始预创建容器,个数={}", PreContainers);

        // 预创建指定数量的容器并放入容器池
        for (int i = 0; i < PreContainers; i++) {
            String containerId = createAndStartContainer();
            if (containerId != null) {
                containerPool.offer(containerId);
            } else {
                log.error("第{}号容器创建失败", i);
            }
        }
        log.info("预创建容器完成");
    }

    /**
     * 创建并启动一个新的容器
     *
     * @return 容器ID，创建失败返回null
     */
    private String createAndStartContainer() {
        try {
            // 创建容器，执行命令创建/app目录并保持容器运行
            CreateContainerResponse response = dockerClient.createContainerCmd(imageName)
                    .withHostConfig(hostConfig)
                    .withCmd("sh", "-c", "mkdir -p /app && tail -f /dev/null")
                    .exec();
            String id = response.getId();
            // 启动容器
            dockerClient.startContainerCmd(id).exec();
            return id;
        } catch (Exception e) {
            log.error("容器创建失败", e);
            return null;
        }
    }

    /**
     * 将文件复制到指定容器的/app目录下
     *
     * @param containerId 容器ID
     * @param filePath    本地文件路径
     */
    private void copyFilesToContainer(String containerId, String filePath) {
        try {
            dockerClient.copyArchiveToContainerCmd(containerId)
                    .withHostResource(filePath)
                    .withRemotePath("/app")
                    .exec();
        } catch (Exception e) {
            log.error("复制文件到容器失败", e);
            throw new RuntimeException("复制文件到容器失败", e);
        }
    }

    /**
     * 执行代码的主要方法
     *
     * @param executeCodeRequest 代码执行请求
     * @return 代码执行响应
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }

    /**
     * 运行编译后的代码文件
     *
     * @param userCodeFile 用户代码文件
     * @param inputList    输入测试用例列表
     * @return 执行消息列表
     */
    @Override
    protected List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String containerId = null;
        try {
            // 从容器池中获取一个可用容器，超时时间为100毫秒
            containerId = containerPool.poll(100, TimeUnit.MILLISECONDS);
            if (containerId == null) {
                containerId = createAndStartContainer(); // 紧急创建新容器
            }

            // 将编译后的class文件和安全策略文件复制到容器中
            copyFilesToContainer(containerId, userCodeFile.getAbsolutePath().replace(".java", ".class"));
            copyFilesToContainer(containerId, SecurityPolicyPath);

            // 并行执行所有测试用例
            return parallelExecute(inputList, containerId);
        } catch (InterruptedException e) {
            throw new RuntimeException("获取容器被中断", e);
        } finally {
            // 将容器归还到容器池中供下次使用
            if (containerId != null) {
                boolean offer = containerPool.offer(containerId);
                if (!offer) {
                    log.error("容器归还失败");
                }
            }
        }
    }

    /**
     * 并行执行多个测试用例
     *
     * @param inputList   输入测试用例列表
     * @param containerId 容器ID
     * @return 执行结果列表
     */
    private List<ExecuteMessage> parallelExecute(List<String> inputList, String containerId) {
        log.info("并行执行测试用例");
        // 创建线程池，最大线程数为测试用例数和8中的较小值
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(inputList.size(), 8));

        try {
            // 提交所有测试用例执行任务
            List<Future<ExecuteMessage>> futures = new ArrayList<>();
            for (String input : inputList) {
                futures.add(executor.submit(() -> getExecuteMessage(input + "\n", dockerClient, containerId)));
            }

            // 收集执行结果
            List<ExecuteMessage> results = new ArrayList<>();
            for (Future<ExecuteMessage> future : futures) {
                ExecuteMessage executeMessage;
                try {
                    // 获取执行结果，超时时间为Time_Out
                    executeMessage = future.get(Time_Out, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("超时", e);
                    // 超时处理
                    ExecuteMessage errorMessage = new ExecuteMessage();
                    errorMessage.setExecuteStatus(ExecuteStatus.TIMEOUT);
                    errorMessage.setErrorMessage("测试用例执行超时");
                    results.add(errorMessage);
                    continue;
                }
                results.add(executeMessage);
            }
            log.info("测试用例执行完成");
            return results;
        } finally {
            executor.shutdown(); // 关闭线程池
        }
    }

    /**
     * 在容器中执行单个测试用例
     *
     * @param input        输入数据
     * @param dockerClient Docker客户端
     * @param containerId  容器ID
     * @return 执行消息
     */
    private static ExecuteMessage getExecuteMessage(String input, DockerClient dockerClient, String containerId) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        StopWatch stopWatch = new StopWatch();

        // 内存监控设置
        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        AtomicLong maxMemory = new AtomicLong(0);
        ResultCallback<Statistics> statsCallback = new ResultCallback.Adapter<Statistics>() {
            @Override
            public void onNext(Statistics stats) {
                // 记录最大内存使用量
                Long usage = stats.getMemoryStats().getUsage();
                if (usage != null) {
                    maxMemory.set(Math.max(maxMemory.get(), usage));
                }
            }
        };

        // 创建执行命令
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(DockerConstant.cmdArr) // 执行命令数组
                .withAttachStdin(true)  // 连接标准输入
                .withAttachStdout(true) // 连接标准输出
                .withAttachStderr(true) // 连接标准错误
                .exec();
        String execId = execCreateCmdResponse.getId();

        // 输出收集器
        AtomicBoolean hasError = new AtomicBoolean(false);
        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();

        // 执行结果回调处理
        ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
            @Override
            public void onNext(Frame frame) {
                StreamType streamType = frame.getStreamType();
                String payload = new String(frame.getPayload());
                if (StreamType.STDOUT.equals(streamType)) {
                    outputBuilder.append(payload); // 标准输出
                } else {
                    hasError.set(true);
                    errorBuilder.append(payload); // 错误输出
                }
                super.onNext(frame);
            }
        };

        try {
            stopWatch.start(); // 开始计时
            statsCmd.exec(statsCallback); // 开始内存监控

            // 执行命令并等待完成
            boolean isCompleted = dockerClient.execStartCmd(execId)
                    .withStdIn(new ByteArrayInputStream(input.getBytes())) // 输入数据
                    .exec(execStartResultCallback)
                    .awaitCompletion(Time_Out, TimeUnit.MILLISECONDS); // 等待执行完成或超时

            // 设置执行结果
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis()); // 执行时间
            executeMessage.setMemory(maxMemory.get()); // 内存使用量
            executeMessage.setOutput(outputBuilder.toString()); // 标准输出

            // 如果有错误输出则设置错误信息
            if (hasError.get()) {
                executeMessage.setErrorMessage(errorBuilder.toString());
            }

            // 检查执行状态
            if (!isCompleted) {
                // 执行超时
                executeMessage.setExecuteStatus(ExecuteStatus.TIMEOUT);
                executeMessage.setErrorMessage("运行超时");
                return executeMessage;
            }

            // 检查执行是否仍在运行
            InspectExecResponse inspectResponse = dockerClient.inspectExecCmd(execId).exec();
            if (inspectResponse.isRunning()) {
                executeMessage.setExecuteStatus(ExecuteStatus.RUNTIME_ERROR);
                executeMessage.setErrorMessage("进程仍在运行");
            } else {
                int exitCode = inspectResponse.getExitCode();
                executeMessage.setProcessExitCode(exitCode);
                // 根据退出码和错误标志设置执行状态
                if (exitCode != 0 || hasError.get()) {
                    executeMessage.setExecuteStatus(ExecuteStatus.EXITED);
                    if (executeMessage.getErrorMessage() == null) {
                        executeMessage.setErrorMessage("程序退出码:" + exitCode);
                    }
                } else {
                    executeMessage.setExecuteStatus(ExecuteStatus.SUCCESS);
                }
            }
        } catch (InterruptedException e) {
            // 执行被中断
            executeMessage.setExecuteStatus(ExecuteStatus.INTERRUPTED);
            executeMessage.setErrorMessage("执行被中断");
        } catch (Exception e) {
            // Docker相关错误
            executeMessage.setExecuteStatus(ExecuteStatus.RUNTIME_ERROR);
            executeMessage.setErrorMessage("Docker发生错误: " + e.getMessage());
        } finally {
            statsCmd.close(); // 关闭内存监控
        }
        return executeMessage;
    }

    /**
     * 拉取Docker镜像
     *
     * @param dockerClient Docker客户端
     * @param image        镜像名称
     */
    private void pullImage(DockerClient dockerClient, String image) {
        if (!firstInitImage) return; // 如果不是首次初始化则跳过
        firstInitImage = false;
        log.info("拉取镜像: {}", image);

        // 创建拉取镜像命令
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                log.info("拉取镜像进度: {}", item.getStatus());
                super.onNext(item);
            }
        };

        try {
            // 执行拉取并等待完成
            pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            log.info("镜像拉取完成: {}", image);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("镜像拉取被中断", e);
        }
    }

    /**
     * 容器清理方法
     * 在Spring Bean销毁前自动执行
     */
    @PreDestroy
    private void destroy() {
        log.info("开始清理Docker容器...");
        try {
            // 清理容器池中的容器
            while (!containerPool.isEmpty()) {
                String containerId = containerPool.poll();
                if (containerId != null) {
                    stopAndRemoveContainer(containerId);
                }
            }

            // 清理其他可能遗留的容器
            List<Container> containers = dockerClient.listContainersCmd()
                    .withShowAll(true)  // 包括已停止的容器
                    .withStatusFilter(Arrays.asList("created", "running", "exited", "paused"))
                    .exec();

            for (Container container : containers) {
                String imageId = container.getImageId();
                // 删除与当前镜像相关的容器
                if (imageId != null && imageId.contains(imageName.split(":")[0])) {
                    stopAndRemoveContainer(container.getId());
                }
            }

            log.info("Docker容器清理完成");
        } catch (Exception e) {
            log.error("容器清理过程中发生异常", e);
        }
    }

    /**
     * 停止并删除指定容器
     *
     * @param containerId 容器ID
     */
    private void stopAndRemoveContainer(String containerId) {
        try {
            // 停止容器，超时时间为2秒
            dockerClient.stopContainerCmd(containerId)
                    .withTimeout(2)
                    .exec();
            log.debug("已停止容器: {}", containerId);
        } catch (Exception e) {
            log.error("停止容器失败: {} - {}", containerId, e.getMessage());
        }

        try {
            // 强制删除容器
            dockerClient.removeContainerCmd(containerId)
                    .withForce(true)
                    .exec();
            log.debug("已删除容器: {}", containerId);
        } catch (Exception e) {
            log.error("删除容器失败: {} - {}", containerId, e.getMessage());
        }
    }
}
