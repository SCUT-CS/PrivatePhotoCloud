package com.hao.hzh_android.home.model;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.LineChartCallback;

public class LineChartModel extends MvcBaseModel<LineChartCallback> {

    public LineChartModel(Context context, LineChartCallback callback) {
        super(context, callback);
    }
}
