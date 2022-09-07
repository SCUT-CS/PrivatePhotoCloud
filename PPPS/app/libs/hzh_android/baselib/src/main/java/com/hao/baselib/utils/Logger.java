package com.hao.baselib.utils;

import android.util.Log;

/**
 * 日志打印类
 */
public class Logger {

    public static final String TAG = "com.longway.sdxlxj";
    public static final boolean DEBUG = true;//是否打印日志的开关，上线时改为false

    public static String getMessage(Object o){
        return o==null?"null":o.toString();
    }

    public static void i(Object msg){
        if (DEBUG)
            Log.i(TAG,getMessage(msg));
    }

    public static void i(String tag,Object msg){
        if (DEBUG)
            Log.i(tag,getMessage(msg));
    }

    public static void e(Object msg){
        if (DEBUG)
            Log.e(TAG,getMessage(msg));
    }

    public static void e(String tag,Object msg){
        if (DEBUG)
            Log.e(tag,getMessage(msg));
    }
}
