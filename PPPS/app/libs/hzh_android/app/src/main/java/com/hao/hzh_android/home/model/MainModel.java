package com.hao.hzh_android.home.model;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.MainCallback;

public class MainModel extends MvcBaseModel<MainCallback> {

    public MainModel(Context context, MainCallback callback) {
        super(context, callback);
    }
}
