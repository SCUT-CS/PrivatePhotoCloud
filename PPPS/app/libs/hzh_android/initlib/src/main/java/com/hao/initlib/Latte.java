package com.hao.initlib;

import android.content.Context;

import java.util.HashMap;

public final class Latte {

    /**
     * 创建本类实例的静态内部类
     */
    private static class Holder{
        private static final Latte INSTANCE = new Latte();
    }

    /**
     * 获取单例对象
     * @return
     */
    public static Latte getInstance(){
        return Holder.INSTANCE;
    }

    /**
     * 初始化方法
     * @param context
     * @return
     */
    public Configurator init(Context context) {
        getConfigurations().put(ConfigType.APPLICATION_CONTEXT.name(), context.getApplicationContext());
        return Configurator.getInstance();
    }

    private HashMap<String, Object> getConfigurations() {
        return Configurator.getInstance().getLatteConfigs();
    }

    /**
     * 获取全局上下文
     * @return
     */
    public Context getApplication(){
        return (Context) getConfigurations().get(ConfigType.APPLICATION_CONTEXT.name());
    }

    /**
     * 获取值
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getValueByKey(Enum<ConfigType> key){
        return Configurator.getInstance().getValueByKey(key);
    }
}
