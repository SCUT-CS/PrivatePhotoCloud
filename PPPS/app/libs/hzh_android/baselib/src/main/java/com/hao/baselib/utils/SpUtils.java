package com.hao.baselib.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * sp保存类
 */
public class SpUtils {

    public static final int STRING_TYPE = 0x01;
    public static final int INT_TYPE = 0x02;
    public static final int BOOLEAN_TYPE = 0x03;
    public static final int FLOAT_TYPE = 0x04;
    public static final int LONG_TYPE = 0x05;


    /**
     * 线程安全的懒汉单例模式
     * @return
     */
    public static SpUtils getInstance(){
        return Holder.INSTANCE;
    }

    private static class Holder{
        private static final SpUtils INSTANCE = new SpUtils();
    }

    /**
     * 保存数据
     * @param context
     * @param name
     * @param value
     */
    public void write(Context context,String name,Object value){
        SharedPreferences sps = context.getSharedPreferences("patrol", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sps.edit();
        if (value instanceof String) {
            editor.putString(name, (String) value);
        }else if (value instanceof Integer) {
            editor.putInt(name, (int) value);
        }else if (value instanceof Boolean) {
            editor.putBoolean(name, (boolean) value);
        }else if (value instanceof Float) {
            editor.putFloat(name, (float) value);
        }else if (value instanceof Long){
            editor.putLong(name, (Long) value);
        }
        editor.commit();
    }

    /**
     * 读取数据
     * @param context
     * @param name
     * @param type
     * @return
     */
    public Object read(Context context,String name,int type){
        SharedPreferences sps = context.getSharedPreferences("patrol", Context.MODE_PRIVATE);
        if (type == STRING_TYPE) {
            return sps.getString(name, "");
        }else if (type == INT_TYPE) {
            return sps.getInt(name, 0);
        }else if (type == BOOLEAN_TYPE) {
            return sps.getBoolean(name, false);
        }else if (type == FLOAT_TYPE) {
            return sps.getFloat(name, 0F);
        }else if (type == LONG_TYPE) {
            return sps.getLong(name, 0L);
        }else {
            return null;
        }
    }
}
