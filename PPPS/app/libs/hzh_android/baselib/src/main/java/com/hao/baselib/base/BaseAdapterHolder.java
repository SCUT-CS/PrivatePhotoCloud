package com.hao.baselib.base;

import android.view.View;
import butterknife.ButterKnife;

public class BaseAdapterHolder {

    public BaseAdapterHolder(View rootView) {
        ButterKnife.bind(this,rootView);
    }
}
