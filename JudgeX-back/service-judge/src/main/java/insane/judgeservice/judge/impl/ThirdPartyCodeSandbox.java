package insane.judgeservice.judge.impl;


import insane.judgeservice.judge.CodeSandbox;
import insane.model.codeSandBox.ExecuteCodeRequest;
import insane.model.codeSandBox.ExecuteCodeResponse;

/**
 第三方代码沙箱(非自己编写的代码沙箱)
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return null;
    }
}
