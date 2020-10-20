package com.dbc.framework.exception;

/**
 * @Auther dbc
 * @Date 2020/10/20 14:27
 * @Description 处理autowired注入时的异常
 */
public class AutoWiredException extends RuntimeException {
    public AutoWiredException(String message) {
        super(message);
    }

    public AutoWiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
