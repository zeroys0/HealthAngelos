package com.pattonsoft.pattonutil1_0.net;

/**
 * 返回实体类
 * Created by zhao on 2016/12/16.
 */

public class HttpResult<T> {
    private int flag;
    private String msg;
    private T data;

    public HttpResult() {

    }

    public int getFlag() {
        return this.flag;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "flag=" + this.flag +
                ", msg='" + this.msg + '\'' +
                ", data=" + this.data +
                '}';
    }
}
