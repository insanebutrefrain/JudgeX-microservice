package insane.constant;

import lombok.Data;

@Data
public class DockerConstant {
    public static String SecurityPolicy = "security.policy";
    public static String SecurityJson = "seccomp.json";
    public static String[] cmdArr = new String[]{
            "java",
            "-Djava.security.manager",
            "-Djava.security.policy==/app/security.policy",
            "-Djdk.serialFilter=!*", // 禁用所有反序列化
            "-cp", "/app", "Main"
    };
}
