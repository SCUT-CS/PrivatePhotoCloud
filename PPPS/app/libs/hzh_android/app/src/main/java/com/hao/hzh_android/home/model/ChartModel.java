package com.hao.hzh_android.home.model;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.ChartCallback;

public class ChartModel extends MvcBaseModel<ChartCallback> {

    public ChartModel(Context context, ChartCallback callback) {
        super(context, callback);
    }
}
