package insane.judgeservice.judge.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import insane.common.common.ErrorCode;
import insane.common.exception.BusinessException;
import insane.judgeservice.judge.CodeSandbox;
import insane.model.codeSandBox.ExecuteCodeRequest;
import insane.model.codeSandBox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 远程代码沙箱
 */
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://192.168.146.128:8082/execute";
        String json = JSONUtil.toJsonStr(executeCodeRequest);

        HttpResponse execute = HttpUtil.createPost(url).body(json).execute();
        String response = execute.body();
        if (response == null || response.isEmpty()) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "代码沙箱服务执行错误:" + response);
        }
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(response, ExecuteCodeResponse.class);
        log.info("[远程代码沙箱]响应:{}", executeCodeResponse);
        return executeCodeResponse;
    }
}
