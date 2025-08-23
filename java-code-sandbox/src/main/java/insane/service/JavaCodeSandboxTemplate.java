package insane.service;

import cn.hutool.core.io.FileUtil;
import insane.enums.ExecuteErrorMessageEnum;
import insane.enums.ExecuteStatus;
import insane.model.ExecuteCaseInfo;
import insane.model.ExecuteCodeRequest;
import insane.model.ExecuteCodeResponse;
import insane.model.ExecuteMessage;
import insane.utils.JavaCodeValidator;
import insane.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Java代码沙箱模板
 */
@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {
    /**
     * 时间限制
     */
    protected final long TIME_OUT = 5000L;

    /**
     * 用户代码所在根目录
     */
    protected final String USER_DIR = JavaCodeSandboxTemplate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    /**
     * 临时代码存放文件夹
     */
    protected final String CODE_FOLDER = "tempCode";

    /**
     * 代码文件名
     */
    protected final String CODE_FILE_NAME = "Main.java";

    /**
     * 安全管理类名
     */
    protected final String MANAGER_NAME = "MySecurityManager";
    /**
     * 安全策略文件路径
     */
    protected final String POLICY_PATH = "file" + USER_DIR + File.separator + "security.policy";

    /**
     * 完整流程
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        String code = executeCodeRequest.getCode();
        // 1. 校验
        log.info("1.校验");
        boolean isValid = JavaCodeValidator.validateCode(code);
        if (!isValid) {
            executeCodeResponse.setExecuteErrorMessageEnum(ExecuteErrorMessageEnum.DANGEROUS_OPERATION.getValue());
            executeCodeResponse.setMessage("代码校验不通过");
            return executeCodeResponse;
        }
        // 2. 保存用户代码
        log.info("2.保存用户代码");
        File userCodeFile = saveCodeToFile(code);
        // 3. 编译
        log.info("3.编译");
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);
        if (!ExecuteStatus.SUCCESS.equals(compileFileExecuteMessage.getExecuteStatus())) {
            executeCodeResponse.setExecuteErrorMessageEnum(ExecuteErrorMessageEnum.COMPILE_ERROR.getValue());
            executeCodeResponse.setMessage("编译失败: " + compileFileExecuteMessage.getErrorMessage());
            return executeCodeResponse;
        }
        // 4. 运行
        log.info("4.运行");
        List<String> inputList = executeCodeRequest.getInputList();
        List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);
        // 5. 收集结果
        log.info("5.收集结果");
        List<ExecuteCaseInfo> executeCaseInfoList = organizeInformation(executeMessageList);
        executeCodeResponse.setExecuteCaseInfoList(executeCaseInfoList);
        // 6. 文件清理
        log.info("6.文件清理");
        boolean del = deleteFile(userCodeFile);
        if (!del) System.out.println("文件清理失败");
        log.info("7.响应:{}", executeCodeResponse);
        return executeCodeResponse;
    }

    /**
     * 1. 把用户的代码保存为文件
     *
     * @param code
     * @return File
     */
    private File saveCodeToFile(String code) {
        String codeParentPath = USER_DIR + File.separator + CODE_FOLDER;
        // 判断全局代码目录是否存在，不存在则新建
        if (!FileUtil.exist(codeParentPath)) {
            FileUtil.mkdir(codeParentPath);
        }
        // 把用户代码隔离存放
        String userCodePath = codeParentPath + File.separator + System.currentTimeMillis();
        String codePath = userCodePath + File.separator + CODE_FILE_NAME;
        File userCodeFile = FileUtil.writeString(code, codePath, StandardCharsets.UTF_8);
        log.info("保存用户代码：" + userCodeFile.getAbsolutePath());
        return userCodeFile;
    }

    /**
     * 2. 编译代码文件
     *
     * @param userCodeFile
     * @return ExecuteMessage
     */
    private ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsoluteFile());
        try {
            Process complieProcess = Runtime.getRuntime().exec(compileCmd);
            return ProcessUtils.runProcessAndGetMessage(complieProcess);
        } catch (Exception e) {
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExecuteStatus(ExecuteStatus.EXITED);
            executeMessage.setProcessExitCode(-1);
            executeMessage.addErrorMessage(e.getMessage());
            return executeMessage;
        }
    }

    /**
     * 3. 执行文件，获得输出结果
     *
     * @param userCodeFile
     * @param inputList
     * @return List<ExecuteMessage>
     */
    protected abstract List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList);

    /**
     * 4. 整理信息
     *
     * @param executeMessageList 每个测试用例的执行情况
     * @return
     */
    private List<ExecuteCaseInfo> organizeInformation(List<ExecuteMessage> executeMessageList) {
        List<ExecuteCaseInfo> executeCaseInfoList = new ArrayList<>();
        for (ExecuteMessage executeMessage : executeMessageList) {
            ExecuteCaseInfo executeCaseInfo = new ExecuteCaseInfo();
            String output = executeMessage.getOutput();
            if (output != null) {
                output = output.replaceAll("\\s+$", "");//去除末尾换行
            }
            executeCaseInfo.setOutput(output);
            executeCaseInfo.setTime(executeMessage.getTime());
            executeCaseInfo.setMemory(executeMessage.getMemory());
            executeCaseInfo.setErrorMessage(executeMessage.getErrorMessage());
            executeCaseInfoList.add(executeCaseInfo);
        }
        return executeCaseInfoList;
    }

    /**
     * 5. 删除文件
     *
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile().exists()) {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            return FileUtil.del(userCodeParentPath);
        }
        return true;
    }
}
