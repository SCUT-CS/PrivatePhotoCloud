package com.hao.initlib;

import android.content.Context;

import cn.jpush.android.api.JPushInterface;

public class JpushUtil {

    /**
     * 本类单例对象
     */
    private static JpushUtil mInstance;

    /**
     * 单例获取的方法
     *
     * @return
     */
    public static JpushUtil getInstance() {
        if (mInstance == null) {
            synchronized (JpushUtil.class) {
                if (mInstance == null) {
                    mInstance = new JpushUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 私有构造方法
     */
    private JpushUtil() {
    }

    /**
     * 初始化极光推送
     * @param context
     */
    public void initJpush(Context context){
        JPushInterface.init(context);
    }
}
