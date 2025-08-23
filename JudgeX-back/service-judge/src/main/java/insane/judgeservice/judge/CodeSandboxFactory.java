package insane.judgeservice.judge;


import insane.judgeservice.judge.impl.ExampleCodeSandbox;
import insane.judgeservice.judge.impl.RemoteCodeSandbox;
import insane.judgeservice.judge.impl.ThirdPartyCodeSandbox;

/**
 代码沙箱工厂
 */
public class CodeSandboxFactory {
    /**
     根据 沙箱类别type 返回 沙箱实例
     @return 无匹配项时默认返回ExampleCodeSandbox
     */
    public static CodeSandbox newInstance(String type) {
        if ("example".equals(type)) {
            return new ExampleCodeSandbox();
        } else if ("remote".equals(type)) {
            return new RemoteCodeSandbox();
        } else if ("thirdParty".equals(type)) {
            return new ThirdPartyCodeSandbox();
        } else {
            return new ExampleCodeSandbox();
        }
    }
}
