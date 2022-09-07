package com.hao.hzh_android.home.activity;

import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.CjsCallback;
import com.hao.hzh_android.home.model.CjsModel;

/**
 * 沉浸式状态栏示例
 * @author WaterWood
 */
public class CjsActivity extends BaseActivity<CjsModel> implements CjsCallback {
    @Override
    protected CjsModel getModelImp() {
        return new CjsModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_cjs;
    }

    @Override
    protected void initWidget() {
        //这一句是沉浸式状态栏 ，状态栏变色方案
        statusBarColor(R.color.red,true);
    }
}
