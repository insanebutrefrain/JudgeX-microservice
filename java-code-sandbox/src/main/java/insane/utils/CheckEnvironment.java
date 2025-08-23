package insane.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CheckEnvironment {
    private CheckEnvironment() {}

    /**
     检查环境是否可用
     */
    public static boolean isValid(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            log.warn("Docker环境检查失败: {}", e.getMessage());
            return false;
        }
    }

    public static boolean isLinuxEnvironment() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux");
    }

    public static boolean isWindowsEnvironment() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows");
    }

    /**
     检查java环境是否可用
     */
    public static boolean isJavaValid() {
        return isValid("java -version");
    }

    /**
     检查docker环境是否可用
     */
    public static boolean isDockerValid() {
        return isValid("docker --version");
    }

    /**
     检查docker环境是否可用,且只能是linux环境
     */
    public static boolean isDockerValidAndLinux() {
        return isDockerValid() && isLinuxEnvironment();
    }


}
