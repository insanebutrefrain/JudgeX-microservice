package insane.utils;

import cn.hutool.core.util.StrUtil;
import insane.enums.ExecuteStatus;
import insane.model.ExecuteMessage;
import org.springframework.util.StopWatch;

import java.io.*;

/**
 * 进程工具类
 */
public class ProcessUtils {

    private ProcessUtils() {
    }

    /**
     * 运行进程并获取执行信息
     *
     * @param process
     * @return
     * @throws Exception
     */
    public static ExecuteMessage runProcessAndGetMessage(Process process) throws Exception {
        ExecuteMessage executeMessage = new ExecuteMessage();
        int exitCode = process.waitFor();
        executeMessage.setProcessExitCode(exitCode);
        if (exitCode == 0) {
            String output = readStream(process.getInputStream());
            executeMessage.setOutput(output);
            executeMessage.setExecuteStatus(ExecuteStatus.SUCCESS);
        } else {
            String errorMessage = readStream(process.getErrorStream());
            executeMessage.setErrorMessage((errorMessage));
            executeMessage.setExecuteStatus(ExecuteStatus.EXITED);
        }
        return executeMessage;
    }

    /**
     * 以交互方式运行进程并获取信息
     *
     * @param process 进程
     * @param opName  操作名称
     * @param args    输入用例
     * @return 执行信息
     */
    public static ExecuteMessage runInterProcessAndGetMessage(Process process, String opName, String args) throws Exception {
        ExecuteMessage executeMessage = new ExecuteMessage();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 向控制台输入程序
        OutputStream outputStream = process.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        // 记得要加空格和回车
        String[] s = args.split(" ");
        String join = StrUtil.join("\n", s) + "\n";
        outputStreamWriter.write(join);
        // 相当于按了回车，执行输入的发送
        outputStreamWriter.flush();
        // 获取进程的输出
        executeMessage.setOutput(readStream(process.getInputStream()));
        // 获取进程的异常输出
        executeMessage.setErrorMessage(readStream(process.getErrorStream()));
        // 退出，记得资源的释放，否则会卡死
        outputStreamWriter.close();
        process.destroy();
        // 停止计时
        stopWatch.stop();
        executeMessage.setTime(stopWatch.getTotalTimeMillis());
        int exitCode = process.waitFor();
        executeMessage.setProcessExitCode(exitCode);
        return executeMessage;
    }

    /**
     * 执行进程并获取信息
     *
     * @param process
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process process, String opName) throws Exception {
        ExecuteMessage executeMessage = new ExecuteMessage();
        // 开始计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 等待程序执行，获取错误码
        int exitValue = process.waitFor();
        executeMessage.setProcessExitCode(exitValue);
        // 正常退出
        if (exitValue == 0) {
            // 获取进程的输出
            executeMessage.setOutput(readStream(process.getInputStream()));
        } else {
            // 获取进程的输出
            executeMessage.setOutput(readStream(process.getInputStream()));

            // 获取进程的异常输出
            executeMessage.setErrorMessage(readStream(process.getErrorStream()));
        }
        // 停止计时
        stopWatch.stop();
        executeMessage.setTime(stopWatch.getTotalTimeMillis());
        return executeMessage;
    }


    /**
     * 获取输出
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    static String readStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder ans = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            ans.append(line).append("\n");
        }
        bufferedReader.close();
        return ans.toString();
    }
}
