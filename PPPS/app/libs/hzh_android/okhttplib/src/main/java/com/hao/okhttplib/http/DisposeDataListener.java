package com.hao.okhttplib.http;

/**
 * 事件监听接口
 * @author WaterWood
 */
public interface DisposeDataListener {
    //请求成功回调事件处理
    void onSuccess(Object responseObj);
    //请求失败回调事件处理
    void onFailure(Object reasonobj);
}
