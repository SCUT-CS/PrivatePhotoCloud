package com.hao.okhttplib.http;

import com.google.gson.Gson;
import com.hao.baselib.utils.NullUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Request创建类
 * @author WaterWood
 */
public class CommonRequest {

    /**
     * 创建post请求的Request对象
     * @param url
     * @param params
     * @return
     */
    public static Request createPostRequest(String tokenName,String tokenValue,String url,RequestParams params){
        FormBody.Builder mFormBodyBuild = new FormBody.Builder();
        if (params!=null){
            for (Map.Entry<String,String> entry:params.urlParams.entrySet()) {
                mFormBodyBuild.add(entry.getKey(),entry.getValue());
            }
        }
        FormBody mFormBody = mFormBodyBuild.build();
        if (NullUtil.isStringEmpty(tokenName)){
            return new Request.Builder()
                    .url(url)
                    .post(mFormBody)
                    .build();
        }else{
            return new Request.Builder()
                    .url(url)
                    .addHeader(tokenName, tokenValue)
                    .post(mFormBody)
                    .build();
        }
    }

    /**
     * 创建get请求的Request对象
     * @param url
     * @param params
     * @return
     */
    public static Request createGetRequest(String tokenName,String tokenValue,String url,RequestParams params){
        StringBuilder urlBuilder = new StringBuilder(url)
                .append("?");
        if (params!=null){
            for (Map.Entry<String,String> entry:params.urlParams.entrySet()){
                urlBuilder
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        if (NullUtil.isStringEmpty(tokenName)){
            return new Request.Builder()
                    .url(urlBuilder.substring(0,urlBuilder.length()-1))
                    .get()
                    .build();
        }else{
            return new Request.Builder()
                    .url(urlBuilder.substring(0,urlBuilder.length()-1))
                    .addHeader(tokenName, tokenValue)
                    .get()
                    .build();
        }
    }

    /**
     * 文件上传请求
     * @return
     */
    public static Request createMultiPostRequest(String tokenName, String tokenValue, String url, RequestParams params, List<String> strs) {
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (params != null) {
            int num = 0;
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof File) {
                    File file = (File) entry.getValue();
                    requestBody.addFormDataPart(entry.getKey(), strs.get(num), RequestBody.create(MediaType.parse("multipart/form-data"), file));
                    num++;
                } else if (entry.getValue() instanceof String) {
                    requestBody.addFormDataPart(entry.getKey(), (String) entry.getValue());
                }
            }
        }
        if (NullUtil.isStringEmpty(tokenName)){
            return new Request.Builder()
                    .url(url)
                    .post(requestBody.build())
                    .build();
        }else{
            return new Request.Builder()
                    .url(url)
                    .addHeader(tokenName, tokenValue)
                    .post(requestBody.build())
                    .build();
        }
    }

    /**
     * application/json格式请求接口
     *
     * @param tokenName
     * @param tokenValue
     * @param url
     * @param params
     * @return
     */
    public static Request createUpjsonRequest(String tokenName, String tokenValue, String url, RequestParams params) {
        Map map = new HashMap();
        for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        String json = new Gson().toJson(map);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        if (NullUtil.isStringEmpty(tokenName) || NullUtil.isStringEmpty(tokenValue)) {
            return new Request.Builder().url(url).post(requestBody).build();
        } else {
            return new Request.Builder().url(url).addHeader(tokenName, tokenValue).addHeader("city","shanxi").post(requestBody).build();
        }
    }

    /**
     * 文件下载的Request创建
     *
     * @param url
     * @return
     */
    public static Request createDownloadRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        return request;
    }
}
