package com.hao.hzh_android.home.activity;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.utils.PathGetUtil;
import com.hao.baselib.utils.ToastUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.DdxcCallback;
import com.hao.hzh_android.home.model.DdxcModel;
import com.hao.okhttplib.http.DownloadListener;
import com.hao.okhttplib.http.DuanDownloadListener;
import com.hao.okhttplib.http.RequestCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DdxcActivity extends BaseActivity<DdxcModel> implements DdxcCallback {

    private long totalSum;
    private int totalTime;
    @BindView(R.id.pro)
    ProgressBar pro;

    @Override
    protected DdxcModel getModelImp() {
        return new DdxcModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_ddxc;
    }

    @Override
    protected void initWidget() {

    }

    @OnClick({R.id.bt_start})
    void click(View v){
        switch (v.getId()){
            case R.id.bt_start:
                List<String> list = new ArrayList<>();
                list.add("aaaa");
                RequestCenter.getInstance(this)
                        .duanDownload("https://ee26e8ea948d60b3b938f84747a99bfc.dd.cdntips.com/imtt.dd.qq.com/16891/apk/92A30BB0D60E28FC7F7EB3A45540E1AA.apk?mkey=5f59b9a4ab741c7b&f=850d&fsname=com.icoolme.android.weather_6.05.004.20200902_2052000035.apk&csr=1bbd&cip=171.116.58.142&proto=https")
                        .setPathDir(PathGetUtil.getLongwayPath(this,list)+ File.separator)
                        .setDownloadFileName("ccc.apk")
                        .setTotalTime(totalTime)
                        .setTotalSum(totalSum)
                        .setDuanDownListener(new DuanDownloadListener(){

                            @Override
                            public void onDownloadSuccess(String path) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.toastWord(DdxcActivity.this,"下载完成");
                                    }
                                });
                            }

                            @Override
                            public void onDownloading(int progress) {
                                pro.setProgress(progress);
                            }

                            @Override
                            public void onDownloadFailed(int time, long sum) {
                                totalTime = time;
                                totalSum = sum;
                            }
                        }).go();
                break;
        }
    }
}
