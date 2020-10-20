package com.dbc.framework.utils;

/**
 * @Auther dbc
 * @Date 2020/9/11 19:35
 */
public class R<T> {
    private final static int SUCCESS_CODE = 0;
    private final static int FAILED_CODE = 1;
    private int code;
    private String msg;
    private T data;

    public static <T> R ok(String msg){
        return result(SUCCESS_CODE,msg,null);
    }
    public static <T> R ok(String msg, T data){
        return result(SUCCESS_CODE,msg,data);
    }
    public static <T> R failed(String msg, T data){
        return result(FAILED_CODE,msg,data);
    }
    public static <T> R failed(String msg){
        return result(FAILED_CODE,msg,null);
    }
    private static <T> R result(int code, String msg, T data){
        R<T> r=new R<T>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public int getCode() {
        return code;
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

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
