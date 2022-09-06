package com.hao.okhttplib.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 接口信息回调处理类
 * Created by WaterWood on 2018/5/29.
 */
public class CommonJsonCallback implements Callback {

    //与服务器返回的字段的一个对应状态
    protected final String EMPTY_MSG = "";
    //自定义异常类型
    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;

    private Handler mDeliveryHandler;//进行消息的转发
    private DisposeDataListener mListener;
    private Class<?> mClass;//这两个是刚刚封装的那两个类

    public CommonJsonCallback(DisposeDataHandle handle) {
        this.mListener = handle.mListener;
        this.mClass = handle.mClass;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(final Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    /**
     * 处理返回json的方法
     *
     * @param responseObj
     */
    private void handleResponse(Object responseObj) {
        //为了保证代码的健壮性
        if (responseObj == null || responseObj.toString().trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }
        //接下来正经开始，这里我把所有的解析判断全部用GSON处理了
        Log.i("longway_json", responseObj.toString());
        CommonBean commonBean;
        try {
            commonBean = new Gson().fromJson(responseObj.toString(), CommonBean.class);
            if (commonBean.isSuccess()) {
                //数据请求成功
                if (mClass == null) {
                    //没有传入要解析实体的情况下，把json返回，这里的返回是一个Object类型，需要responseObj.toString()处理
                    mListener.onSuccess(responseObj);
                } else {
                    //传入了实体对象，需要我们进行解析。这里的实体对象还包括了CommonBean中有的字段，否则解析的太麻烦了，需要用到TypeBuilder
                    //这里我用try...catch包裹一下，省的再出现json格式不对，崩溃的问题
                    try {
                        Object object = new Gson().fromJson(responseObj.toString(), mClass);
                        if (object != null) {
                            mListener.onSuccess(object);
                        } else {
                            //服务器返回的不是一个合法的json
                            mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        //服务器返回的不是一个合法的json
                        mListener.onFailure(new OkHttpException(JSON_ERROR, e.toString()));
                    }
                }
            } else {
                //数据请求失败
                try {
                    mListener.onFailure(new OkHttpException(Integer.parseInt(commonBean.getCode()), commonBean.getMessage()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (JsonSyntaxException e) {
            //服务器返回的不是一个合法的json
            mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
        }
    }
}
