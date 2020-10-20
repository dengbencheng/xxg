package com.dbc.framework.constants;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther dbc
 * @Date 2020/10/9 17:02
 * @Description
 */
public enum ResultConstants {

    SUCCESS(0, "登录成功"),
    VERIFY_CODE_ERROR(1, " 验证码错误");

    private int code;
    private String msg;

    ResultConstants(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
