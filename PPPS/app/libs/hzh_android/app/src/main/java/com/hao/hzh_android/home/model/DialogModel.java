package com.hao.hzh_android.home.model;

import android.content.Context;

import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.DialogCallback;

public class DialogModel extends MvcBaseModel<DialogCallback> {

    public DialogModel(Context context, DialogCallback callback) {
        super(context, callback);
    }
}
