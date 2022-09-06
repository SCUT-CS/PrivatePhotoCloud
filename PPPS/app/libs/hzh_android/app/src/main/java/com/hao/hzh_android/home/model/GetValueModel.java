package com.hao.hzh_android.home.model;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.GetValueCallback;

public class GetValueModel extends MvcBaseModel<GetValueCallback> {

    public GetValueModel(Context context, GetValueCallback callback) {
        super(context, callback);
    }
}
