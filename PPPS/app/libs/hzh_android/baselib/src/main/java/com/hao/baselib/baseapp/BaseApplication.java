package com.hao.baselib.baseapp;

import android.app.Application;

public abstract class BaseApplication extends Application {
    //1.获取全局上下文第一步
    private static Application mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //2.获取全局上下文第二步
        mApplication = this;
        init();
    }

    /**
     * 获取全局上下文的方法
     * @return
     */
    //3.获取全局上下文第三步
    public static Application getInstance(){
        return mApplication;
    }

    /**
     * 其他初始化内容
     */
    protected abstract void init();
}
