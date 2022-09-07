package com.hao.hzh_android.home.model;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.PieChartCallback;

public class PieChartModel extends MvcBaseModel<PieChartCallback> {

    public PieChartModel(Context context, PieChartCallback callback) {
        super(context, callback);
    }
}
