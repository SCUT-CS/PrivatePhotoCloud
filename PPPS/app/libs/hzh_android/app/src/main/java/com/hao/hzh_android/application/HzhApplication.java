package com.hao.hzh_android.application;

import com.hao.baselib.baseapp.BaseApplication;
import com.hao.hzh_android.R;
import com.hao.initlib.Latte;

/**
 * 入口Application
 * @author WaterWood
 */
//这个类一定要在清单文件中配置name，否则无法生效
public class HzhApplication extends BaseApplication {
    @Override
    protected void init() {
        Latte.getInstance()
                .init(this)
                .withApiHost("http://127.0.0.1/")
                .withApi()
                .withDownloadImage()
                .withJpush()
                .initARouter()
                .configure();
    }
}
