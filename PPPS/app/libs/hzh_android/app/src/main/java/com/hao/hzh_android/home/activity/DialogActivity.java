package com.hao.hzh_android.home.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.dialog.CommonDialog;
import com.hao.baselib.dialog.DownLoadDialog;
import com.hao.baselib.utils.SpUtils;
import com.hao.baselib.utils.ToastUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.DialogCallback;
import com.hao.hzh_android.home.model.DialogModel;

/**
 * 对话框页面
 * @author WaterWood
 */
public class DialogActivity extends BaseActivity<DialogModel> implements DialogCallback, View.OnClickListener{

    private Button bt_common;
    private Button bt_progress;
    private DownLoadDialog downloadDialog;
    //定时器相关
    private Handler handler;
    private Runnable runnable;
    private int progress;

    @Override
    protected DialogModel getModelImp() {
        return new DialogModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_dialog;
    }

    @Override
    protected void initWidget() {
        bt_common = findViewById(R.id.bt_common);
        bt_progress = findViewById(R.id.bt_progress);
        bt_common.setOnClickListener(this);
        bt_progress.setOnClickListener(this);
        //设置一个定时器
        handler = new Handler();
        //每两秒执行一次
        runnable = new Runnable() {
            @Override
            public void run() {
                //要执行的方法
                downloadDialog.setProgress(progress);
                progress++;
                //每秒执行一次方法
                if (progress!=101) {
                    handler.postDelayed(runnable, 100);
                }else{
                    downloadDialog.dismiss();
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_common:
                //普通对话框
                final CommonDialog commonDialog1 = new CommonDialog(this);
                commonDialog1.setTitleText("对话框样式")
                        .setContentText("这是一个对话框样式")
                        .setOneColor(0xFF00A684)
                        .setTwoColor(0xFFFFFFFF)
                        .setTitleSize(20)
                        .setContentSize(14)
                        .setOneSize(14)
                        .setTwoSize(14)
                        .setOneDrawable(0xFFE8EFEE)
                        .setTwoDrawable(0xFF19BD9B)
                        .setTwoClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtil.toastWord(DialogActivity.this,"确定了");
                                commonDialog1.dismiss();
                            }
                        })
                        .setOneClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtil.toastWord(DialogActivity.this,"取消了");
                                commonDialog1.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.bt_progress:
                //进度条对话框
                downloadDialog = new DownLoadDialog(this);
                downloadDialog.setContent("进度对话框样式")
                        .show();
                progress = 0;
                handler.postDelayed(runnable, 100);
                break;
        }
    }
}
