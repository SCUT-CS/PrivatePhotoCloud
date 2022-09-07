package com.hao.hzh_android.home.activity;

import android.view.View;
import android.widget.Button;

import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.baselib.utils.ToastUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.DtqxCallback;
import com.hao.hzh_android.home.model.DtqxModel;

/**
 * 动态权限获取实例
 * @author WaterWood
 */
public class DtqxActivity extends WaterPermissionActivity<DtqxModel> implements DtqxCallback {

    private Button bt;

    @Override
    protected DtqxModel getModelImp() {
        return new DtqxModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_dtqx;
    }

    @Override
    protected void initWidget() {
        bt = findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重点是3个，
                //1.继承的类，改为WaterPermissionActivity，
                //2.下面这个方法
                //3.对应的清单文件也要配置对应权限
                requestPermission(WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    @Override
    protected void doSDWrite() {
        requestPermission(READ_EXTERNAL_STORAGE);
    }

    @Override
    protected void doSDRead() {
        ToastUtil.toastWord(this,"权限请求成功");
    }
}
