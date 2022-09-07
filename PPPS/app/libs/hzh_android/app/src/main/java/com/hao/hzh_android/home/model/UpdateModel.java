package com.hao.hzh_android.home.model;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.UpdateCallback;

public class UpdateModel extends MvcBaseModel<UpdateCallback> {
    public UpdateModel(Context context, UpdateCallback callback) {
        super(context, callback);
    }
}
