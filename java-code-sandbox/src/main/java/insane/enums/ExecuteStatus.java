package insane.enums;

public enum ExecuteStatus {
    SUCCESS,       // 执行成功
    TIMEOUT,       // 执行超时
    EXITED,        // 程序退出(可能有错误)
    INTERRUPTED,   // 执行被中断
    RUNTIME_ERROR   // Docker执行错误
}
