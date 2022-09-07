package com.hao.okhttplib.http;

/**
 * 把回调接口和字节码进行封装
 * @author WaterWood
 */
public class DisposeDataHandle {

    public DisposeDataListener mListener = null;
    public Class<?> mClass = null;

    public DisposeDataHandle(DisposeDataListener listener){
        this.mListener = listener;
    }

    public DisposeDataHandle(DisposeDataListener listener,Class<?> clazz){
        this.mListener = listener;
        this.mClass = clazz;
    }
}
