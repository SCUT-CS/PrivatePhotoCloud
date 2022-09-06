package com.hao.okhttplib.utils;

import com.google.gson.Gson;

/**
 * 使用Gson的实体包装，省的再引用依赖
 * @author WaterWood
 */
public class JsonUtil {

    /**
     * json转指定实体
     * @param json
     * @param clazz
     * @return
     */
    public static Object JsonToBean(String json,Class clazz){
        return new Gson().fromJson(json,clazz);
    }

    /**
     * 实体或者列表转json
     * @param object
     * @return
     */
    public static String BeanToJson(Object object){
        return new Gson().toJson(object);
    }
}
