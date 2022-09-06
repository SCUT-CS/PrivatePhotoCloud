package com.hao.okhttplib.http;

import android.content.Context;
import android.util.Log;
import com.hao.baselib.utils.NullUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求网络的封装类
 * @author WaterWood
 */
public class RequestCenter {

    /**
     * 本类实例
     */
    private static RequestCenter mIntance;
    /**
     * 一次请求的参数集合
     */
    private RequestParams requestParams;
    /**
     * 根据传入url调用的不同方法，区分请求方式
     */
    private int requestType;
    private final int GET = 0x01;
    private final int POST = 0x02;
    private final int FILE = 0x03;
    private final int UP_JSON = 0x04;
    private final int DOWNLOAD = 0x05;
    private final int DUAN_DOWNLOAD = 0x06;
    /**
     * 请求地址
     */
    private String mUrl;
    /**
     * 返回json的解析类型
     */
    private Class<?> clazz;
    /**
     * 传入回调
     */
    private DisposeDataListener disposeDataListener;
    /**
     * 文件名称集合
     */
    private List<String> strs;
    /**
     * 文件下载的目录路径
     */
    private String pathDir;
    /**
     * 下载文件名
     */
    private String downloadFileName;
    /**
     * 文件下载的回调
     */
    private DownloadListener downListener;
    private DuanDownloadListener duanDownloadListener;

    private String tokenName;

    private String tokenValue;

    private int totalTime;
    private long totalSum;

    /**
     * 获取对象实例
     *
     * @return
     */
    public static RequestCenter getInstance(Context context) {
        mIntance = new RequestCenter(context);
        return mIntance;
    }

    /**
     * 私有化构造方法
     */
    private RequestCenter(Context context) {
        requestParams = new RequestParams();
        strs = new ArrayList<>();
        //设置token，如果半中间要改也能改了
        tokenName = "";
        tokenValue = "";
    }

    //=======================传入参数======================================
    //=======================GET，POST，UP_JSON用第一种====================
    //=======================FILE用第二种==================================
    //=======================DOWNLOAD不用==================================

    /**
     * 设置普通参数
     *
     * @param key
     * @param value
     */
    public RequestCenter setParam(String key, String value) {
        Log.i("RequestParam","key:"+key+"       value:"+value);
        requestParams.put(key, value);
        return this;
    }

    public RequestCenter setIntParam(String key, int value) throws FileNotFoundException {
        requestParams.put(key, value);
        return this;
    }

    /**
     * 设置文件上传时的普通参数，以及单张图片的上传方式
     *
     * @param key
     * @param value
     */
    public RequestCenter setFileParam(String key, Object value) {
        try {
            requestParams.put(key, value);
            if (value instanceof File) {
                File file = (File) value;
                strs.add(file.getName());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 多张图片的上传方式
     *
     * @param listFile
     * @return
     */
    public RequestCenter setFileParams(String key, List<File> listFile) {
        if (!NullUtil.isListEmpty(listFile)) {
            //这时就可以处理图片集合了
            for (int i = 0; i < listFile.size(); i++) {
                try {
                    requestParams.put(key + "[" + i + "]", listFile.get(i));
                    strs.add(listFile.get(i).getName());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    /**
     * 单张张图片的上传方式
     *
     * @param listKey
     * @param listFile
     * @return
     */
    public RequestCenter setOneFileParams(List<String> listKey, List<File> listFile) {
        if (!NullUtil.isListEmpty(listFile)) {
            //这时就可以处理图片集合了
            for (int i = 0; i < listFile.size(); i++) {
                try {
                    if (listFile.get(i).length()!=0){
                        requestParams.put(listKey.get(i), listFile.get(i));
                        strs.add(listFile.get(i).getName());
                        Log.i("fbweufas",listKey.get(i));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }


    //=======================传入地址====================
    //=======================GET用get====================
    //=======================POST用post==================
    //=======================UP_JSON用upjson=============
    //=======================FILE用file==================
    //=======================DOWNLOAD用download==========

    /**
     * get方法传入url
     *
     * @param url
     * @return
     */
    public RequestCenter get(String url) {
        Log.i("RequestUrl",url);
        this.mUrl = url;
        requestType = GET;
        return this;
    }

    /**
     * post方法传入url
     *
     * @param url
     * @return
     */
    public RequestCenter post(String url) {
        Log.i("RequestUrl",url);
        this.mUrl = url;
        requestType = POST;
        return this;
    }

    /**
     * 文件上传传入url
     *
     * @param url
     * @return
     */
    public RequestCenter file(String url) {
        Log.i("RequestUrl",url);
        this.mUrl = url;
        requestType = FILE;
        return this;
    }

    /**
     * application/json方式请求传入url
     *
     * @param url
     * @return
     */
    public RequestCenter upJson(String url) {
        Log.i("RequestUrl",url);
        this.mUrl = url;
        requestType = UP_JSON;
        return this;
    }

    /**
     * 下载方式传入url
     *
     * @param url
     * @return
     */
    public RequestCenter download(String url) {
        Log.i("RequestUrl",url);
        this.mUrl = url;
        requestType = DOWNLOAD;
        return this;
    }

    /**
     * 断点下载方式传入url
     * @param url
     * @return
     */
    public RequestCenter duanDownload(String url) {
        Log.i("RequestUrl",url);
        this.mUrl = url;
        requestType = DUAN_DOWNLOAD;
        return this;
    }

    //=======================传入解析实体==================================
    //=======================GET,POST,UP_JSON，FILE使用====================
    //=======================DOWNLOAD不用==================================

    /**
     * 设置返回json的解析实体
     *
     * @param clazz
     * @return
     */
    public RequestCenter setGsonClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    //=======================传入请求回调==================================
    //=======================GET,POST,UP_JSON，FILE用setListener====================
    //=======================DOWNLOAD用剩下的后面的==================================

    /**
     * 设置传入回调
     *
     * @param disposeDataListener
     */
    public RequestCenter setListener(DisposeDataListener disposeDataListener) {
        this.disposeDataListener = disposeDataListener;
        return this;
    }

    /**
     * 传入下载的文件夹路径
     *
     * @param pathDir
     * @return
     */
    public RequestCenter setPathDir(String pathDir) {
        this.pathDir = pathDir;
        return this;
    }

    public RequestCenter setTotalTime(int totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    public RequestCenter setTotalSum(long totalSum) {
        this.totalSum = totalSum;
        return this;
    }

    /**
     * 设置文件名称
     * @param downloadFileName
     */
    public RequestCenter setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
        return this;
    }

    /**
     * 设置文件下载传入回调
     *
     * @param downListener
     */
    public RequestCenter setDownListener(DownloadListener downListener) {
        this.downListener = downListener;
        return this;
    }

    /**
     * 设置文件断点下载传入回调
     * @param duanDownloadListener
     */
    public RequestCenter setDuanDownListener(DuanDownloadListener duanDownloadListener) {
        this.duanDownloadListener = duanDownloadListener;
        return this;
    }

    public RequestCenter setHead(String tokenName, String tokenValue) {
        this.tokenName = tokenName;
        this.tokenValue = tokenValue;
        return this;
    }

    /**
     * 都设置好后最后的请求
     */
    public void go() {
        if (requestType == GET) {
            //get请求方式
            CommonOkHttpClient.sendRequest(CommonRequest
                            .createGetRequest(tokenName, tokenValue, mUrl, requestParams),
                    new CommonJsonCallback(new DisposeDataHandle(disposeDataListener, clazz)));
        } else if (requestType == POST) {
            //post请求方式
            CommonOkHttpClient.sendRequest(CommonRequest
                            .createPostRequest(tokenName, tokenValue, mUrl, requestParams),
                    new CommonJsonCallback(new DisposeDataHandle(disposeDataListener, clazz)));
        } else if (requestType == FILE) {
            //文件上传
            CommonOkHttpClient.sendRequest(CommonRequest.createMultiPostRequest(tokenName, tokenValue
                    , mUrl, requestParams, strs),
                    new CommonJsonCallback(new DisposeDataHandle(disposeDataListener, clazz)));
        } else if (requestType == UP_JSON) {
            //application/json上传方式
            CommonOkHttpClient.sendRequest(CommonRequest.createUpjsonRequest(tokenName, tokenValue
                    , mUrl, requestParams),
                    new CommonJsonCallback(new DisposeDataHandle(disposeDataListener, clazz)));
        } else if (requestType == DOWNLOAD) {
            //文件下载
            CommonOkHttpClient.sendDownloadRequest(CommonRequest.createDownloadRequest(mUrl)
                    , new CommonDownloadCallback(pathDir, downloadFileName, downListener));
        }else if (requestType == DUAN_DOWNLOAD) {
            //文件下载
            CommonOkHttpClient.sendDuanDownloadRequest(CommonRequest.createDownloadRequest(mUrl)
                    , new CommonDuanDownloadCallback(pathDir, downloadFileName, duanDownloadListener,totalTime,totalSum));
        }
    }
}
