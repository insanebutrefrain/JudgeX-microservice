package insane.service;

import insane.enums.ExecuteStatus;
import insane.model.ExecuteMessage;
import insane.utils.ProcessUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Java原生实现
 */
@Service
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {

    @Override
    protected List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            // 使用-Xmx256m，限制最大内存为256M
            // jvm 参数的限制，堆内存限制，不等同于系统实际占用内存
            /**
             *  运行命令行参数
             */
//            String runCmd = String.format("java -Xmx512m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
//            String runCmd = String.format("java -Xmx512m -Dfile.encoding=UTF-8 -cp \"%s;%s\" -Djava.security.manager=%s Main %s",
//                    userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, inputArgs);
            /**
             * 交互执行命令
             */
//             设置安全管理器
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp \"%s;%s\" -Djava.security.manager=%s Main",
//                    userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            String runCmd = String.format(
                    "java -Xmx256m -Dfile.encoding=UTF-8 -Djava.security.manager=%s -Djava.security.policy=%s -cp \"%s;%s\" Main",
                    MANAGER_NAME, POLICY_PATH, userCodeParentPath, USER_DIR);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                ExecuteMessage executeMessage = ProcessUtils.runInterProcessAndGetMessage(runProcess, "交互参数运行", inputArgs);
                // 运行时间限制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        if (runProcess.isAlive()) {
                            runProcess.destroy();
                            executeMessage.setExecuteStatus(ExecuteStatus.TIMEOUT);
                            executeMessage.setErrorMessage("运行超时");
                        }
                    } catch (InterruptedException ignored) {
                    }
                }).start();
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                executeMessageList.add(ExecuteMessage.builder().errorMessage("运行错误").executeStatus(ExecuteStatus.RUNTIME_ERROR).build());
            }
        }
        return executeMessageList;
    }
}
