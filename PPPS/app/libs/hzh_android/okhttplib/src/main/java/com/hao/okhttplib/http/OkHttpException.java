package com.hao.okhttplib.http;

/**
 * 异常封装类
 * @author WaterWood
 */
public class OkHttpException extends Exception{

    private int ecode;
    private Object emsg;

    public OkHttpException(int ecode,Object emsg){
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }

    public void setEcode(int ecode) {
        this.ecode = ecode;
    }

    public Object getEmsg() {
        return emsg;
    }

    public void setEmsg(Object emsg) {
        this.emsg = emsg;
    }
}
