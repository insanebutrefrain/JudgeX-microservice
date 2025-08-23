package insane.judgeservice.judge;


import insane.model.codeSandBox.ExecuteCodeRequest;
import insane.model.codeSandBox.ExecuteCodeResponse;

/**
 代码沙箱接口定义
 */
public interface CodeSandbox {
    /**
     执行代码
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
