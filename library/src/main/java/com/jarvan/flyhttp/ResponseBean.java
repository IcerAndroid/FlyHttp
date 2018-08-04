package com.jarvan.flyhttp;

/**
 * 创建日期：2018/8/4 on 下午1:52
 * 描述:
 * 作者:张冰
 */
public class ResponseBean<T> {
    private int code;
    private String msg;
    private T data;
    private String errlog;

    public String getErrlog() {
        return errlog;
    }

    public void setErrlog(String errlog) {
        this.errlog = errlog;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
