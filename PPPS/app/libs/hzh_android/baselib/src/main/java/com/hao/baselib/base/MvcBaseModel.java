package com.hao.baselib.base;

import android.content.Context;

public class MvcBaseModel<T extends MvcBaseCallBack> {

    protected T callback;
    protected Context context;

    public MvcBaseModel(Context context, T callback) {
        this.context = context;
        this.callback = callback;
    }
}
