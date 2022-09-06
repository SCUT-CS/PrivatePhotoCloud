package com.hao.okhttplib.http;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数放置类
 * @author WaterWood
 */
public class RequestParams {

    public ConcurrentHashMap<String,String> urlParams = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String,Object> fileParams = new ConcurrentHashMap<>();

    /**
     * 设置普通字符串参数
     * @param key
     * @param value
     */
    public void put(String key,String value){
        if (key!=null && value!=null){
            urlParams.put(key,value);
        }
    }

    /**
     * 设置文件参数
     * @param key
     * @param object
     * @throws FileNotFoundException
     */
    public void put(String key,Object object) throws FileNotFoundException {
        if (key!=null){
            fileParams.put(key,object);
        }
    }
}
