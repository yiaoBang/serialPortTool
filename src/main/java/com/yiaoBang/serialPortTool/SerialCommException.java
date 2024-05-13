package com.yiaoBang.serialPortTool;

import java.io.Serial;

public class SerialCommException extends Exception {
    @Serial
    private static final long serialVersionUID = -2066373571857175811L;

    public SerialCommException() {
        super();
    }

    /**
     * 串行通信异常
     *
     * @param message 消息
     */
    public SerialCommException(String message) {
        super(message);
    }


    /**
     * 串行通信异常
     *
     * @param message 消息
     * @param cause
     */
    public SerialCommException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 串行通信异常
     *
     * @param cause 原因
     */
    public SerialCommException(Throwable cause) {
        super(cause);
    }

    /**
     * 串行通信异常
     *
     * @param message            消息
     * @param cause              原因
     * @param enableSuppression  启用抑制
     * @param writableStackTrace 可写堆栈跟踪
     */
    public SerialCommException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}