package insane.service;


import insane.model.ExecuteCodeRequest;
import insane.model.ExecuteCodeResponse;

/**
 代码沙箱接口定义
 */
public interface CodeSandbox {
    /**
     执行代码
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
