package com.dbc.framework.exception;

/**
 * @Auther dbc
 * @Date 2020/10/20 16:14
 * @Description
 */
public class ComponentException extends RuntimeException {
    public ComponentException(String message) {
        super(message);
    }

    public ComponentException(String message, Throwable cause) {
        super(message, cause);
    }
}
