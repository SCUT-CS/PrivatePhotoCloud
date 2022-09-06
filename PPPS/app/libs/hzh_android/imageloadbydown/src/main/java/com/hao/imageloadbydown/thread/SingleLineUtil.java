package com.hao.imageloadbydown.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单线程线程池获取
 * @author WaterWood
 */
public class SingleLineUtil {
    private static SingleLineUtil mInstance;
    private ExecutorService singleThreadExecutor;

    public static SingleLineUtil getInstance(){
        if (mInstance == null){
            synchronized (SingleLineUtil.class){
                if (mInstance == null){
                    mInstance = new SingleLineUtil();
                }
            }
        }
        return mInstance;
    }

    private SingleLineUtil() {}

    public ExecutorService getSingle(){
        if (singleThreadExecutor == null){
            synchronized (SingleLineUtil.class){
                if (singleThreadExecutor == null){
                    singleThreadExecutor =  Executors.newSingleThreadExecutor();
                }
            }
        }
        return singleThreadExecutor;
    }
}
