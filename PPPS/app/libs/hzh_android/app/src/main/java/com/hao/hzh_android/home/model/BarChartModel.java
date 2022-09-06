package com.hao.hzh_android.home.model;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.BarChartCallback;

public class BarChartModel extends MvcBaseModel<BarChartCallback> {

    public BarChartModel(Context context, BarChartCallback callback) {
        super(context, callback);
    }
}
