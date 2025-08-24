package insane.controller;

import insane.model.ExecuteCodeRequest;
import insane.model.ExecuteCodeResponse;
import insane.service.JavaNativeCodeSandbox;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class MainController {
    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secret";

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandBox;

//    @Resource
//    private JavaDockerCodeSandbox javaDockerCodeSandbox;

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 基本的认证
//        String authHeader = httpServletRequest.getHeader(AUTH_REQUEST_HEADER);
//        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
//            // 权限不足
//            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return null;
//        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空！");
        }
        return javaNativeCodeSandBox.executeCode(executeCodeRequest);
//        return javaDockerCodeSandbox.executeCode(executeCodeRequest);
    }


    @GetMapping("/test")
    public String hello() {
        return "ok";
    }
}