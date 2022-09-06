package com.hao.initlib;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hao.baselib.utils.PathGetUtil;
import com.hao.imageloadbydown.imageload.DownloadImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Configurator {

    /**
     * 存储数据的主要集合
     */
    private static final HashMap<String,Object> LATTE_CONFIGS = new HashMap<>();

    /**
     * 私有构造方法
     */
    private Configurator() {
        //初始化操作刚刚开始，还未结束
        LATTE_CONFIGS.put(ConfigType.CONFIG_READY.name(),false);
    }

    /**
     * 创建本类实例的静态内部类
     */
    private static class Holder{
        private static final Configurator INSTANCE = new Configurator();
    }

    /**
     * 获取单例对象
     * @return
     */
    public static Configurator getInstance(){
        return Holder.INSTANCE;
    }

    /**
     * 配置结束
     */
    public final void configure(){
        LATTE_CONFIGS.put(ConfigType.CONFIG_READY.name(),true);
    }

    /**
     * 获取LATTE_CONFIGS集合,只提供给Latte使用
     * @return
     */
    protected final HashMap<String,Object> getLatteConfigs(){
        return LATTE_CONFIGS;
    }

    /**
     * 设置根请求地址
     * @param host
     * @return
     */
    public final Configurator withApiHost(String host){
        LATTE_CONFIGS.put(ConfigType.API_HOST.name(),host);
        return this;
    }

    /**
     * 设置接口地址
     * @return
     */
    public final Configurator withApi(){
        Student student = new Student();
        student.setName("小明");
        student.setAge(22);
        student.setSex("男");
        LATTE_CONFIGS.put(ConfigType.LOGIN.name(),student);
        return this;
    }

    /**
     * 初始化图片带下载框架
     * @return
     */
    public final Configurator withDownloadImage(){
        List<String> listHaha = new ArrayList<>();
        listHaha.add("aaaa");
        listHaha.add("bbbb");
        listHaha.add("cccc");
        DownloadImageLoader.getInstance().setImgPathDir(PathGetUtil.getLongwayPath((Context)LATTE_CONFIGS.get(ConfigType.APPLICATION_CONTEXT.name()),listHaha));
        return this;
    }

    /**
     * 初始化极光推送
     * @return
     */
    public final Configurator withJpush(){
        JpushUtil.getInstance().initJpush((Context)LATTE_CONFIGS.get(ConfigType.APPLICATION_CONTEXT.name()));
        return this;
    }

    /**
     * 初始化ARouter
     * @return
     */
    public final Configurator initARouter(){
        if (BuildConfig.DEBUG){
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init((Application) LATTE_CONFIGS.get(ConfigType.APPLICATION_CONTEXT.name()));
        return this;
    }

    /**
     * 检查是否已经初始化完成，在获取配置时调用
     */
    private void checkConfiguration(){
        final boolean isReady = (boolean) LATTE_CONFIGS.get(ConfigType.CONFIG_READY.name());
        if (!isReady){
            throw new RuntimeException("Configuration is not ready,call configure");
        }
    }

    /**
     * 获取对应的值
     * @param key
     * @return
     */
    <T> T getValueByKey(Enum<ConfigType> key){
        checkConfiguration();
        return (T) LATTE_CONFIGS.get(key.name());
    }
}
