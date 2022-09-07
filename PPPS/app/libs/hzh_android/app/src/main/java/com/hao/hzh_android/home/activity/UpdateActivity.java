package com.hao.hzh_android.home.activity;

import android.view.View;
import android.widget.Button;
import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.UpdateCallback;
import com.hao.hzh_android.home.model.UpdateModel;
import com.hao.updatelib.core.UpdateUtil;

/**
 * 版本更新
 *
 * @author WaterWood
 */
public class UpdateActivity extends WaterPermissionActivity<UpdateModel> implements UpdateCallback, View.OnClickListener {

    private Button bt_update;

    @Override
    protected UpdateModel getModelImp() {
        return new UpdateModel(this, this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_update;
    }

    @Override
    protected void initWidget() {
        bt_update = findViewById(R.id.bt_update);
        bt_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_update:
                requestPermission(WRITE_EXTERNAL_STORAGE);
                break;
        }
    }

    @Override
    protected void doSDWrite() {
        requestPermission(READ_EXTERNAL_STORAGE);
    }

    @Override
    protected void doSDRead() {
        install();
    }

    private void install() {
        UpdateUtil.getInstance().startUpdate(this
                , 2
                , "这是一个更新测试"
                , true
                , "https://ee26e8ea948d60b3b938f84747a99bfc.dd.cdntips.com/imtt.dd.qq.com/16891/apk/92A30BB0D60E28FC7F7EB3A45540E1AA.apk?mkey=5f59b9a4ab741c7b&f=850d&fsname=com.icoolme.android.weather_6.05.004.20200902_2052000035.apk&csr=1bbd&cip=171.116.58.142&proto=https"
                , "xxx.apk");
    }
}
