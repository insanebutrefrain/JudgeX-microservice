package insane.security;

import java.net.SocketPermission;
import java.security.Permission;

/**
 * 安全管理器
 */
public class MySecurityManager extends SecurityManager {

    /**
     * 检查所有权限
     *
     * @param perm the requested permission.
     */
    @Override
    public void checkPermission(Permission perm) {
        // 禁止设置安全管理器
        if (perm.getName().contains("setSecurityManager")) {
            throw new SecurityException("权限不足");
        }

        // 禁止网络连接
        if (perm instanceof SocketPermission) {
            throw new SecurityException("禁止网络访问");
        }

        // 允许其他基本权限
    }

    // 检查是否可以执行
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("checkExec权限异常" + cmd);
    }

    // 检查是否可以读取
    @Override
    public void checkRead(String file, Object context) {
        throw new SecurityException("checkRead权限异常" + file);
    }


    // 检查是否可以写入
    @Override
    public void checkWrite(String file) {
        throw new SecurityException("checkWrite权限异常" + file);
    }

    // 检查是否可以删除
    @Override
    public void checkDelete(String file) {
        throw new SecurityException("checkDelete权限异常" + file);
    }


    // 检查是否可以连接网络
    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("checkConnect权限异常" + host + ":" + port);
    }


}
