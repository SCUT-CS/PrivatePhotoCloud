package com.hao.baselib.eventbus;

public class MessageBean {

    private int code;//消息标记，用于区分我们不同页面接收的消息
    private Object message;//消息实体，用于放我们消息的实体

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
