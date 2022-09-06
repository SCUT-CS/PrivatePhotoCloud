package com.hao.okhttplib.http;

import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 网络请求Client核心类,进行请求的发送，请求参数的配置和https的支持
 * @author WaterWood
 */
public class CommonOkHttpClient {

    private static final int TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;

    static {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        //设置超时时间
        okHttpBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(TIME_OUT,TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(TIME_OUT,TimeUnit.SECONDS);
        //支持重定向
        okHttpBuilder.followRedirects(true);
        //无论什么类型的主机都返回true，不论自定义还是从第三方购买
        okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        okHttpBuilder.sslSocketFactory(HttpUtils.initSSLSocketFactory());
        mOkHttpClient = okHttpBuilder.build();
    }

    /**
     * 发送请求
     * @param request
     * @param commonJsonCallback
     * @return
     */
    public static Call sendRequest(Request request, CommonJsonCallback commonJsonCallback){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(commonJsonCallback);
        return call;
    }

    /**
     * 文件下载请求
     * @param request
     * @param callback
     * @return
     */
    public static Call sendDownloadRequest(Request request,CommonDownloadCallback callback){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    /**
     * 文件断点下载请求
     * @param request
     * @param callback
     * @return
     */
    public static Call sendDuanDownloadRequest(Request request,CommonDuanDownloadCallback callback){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
