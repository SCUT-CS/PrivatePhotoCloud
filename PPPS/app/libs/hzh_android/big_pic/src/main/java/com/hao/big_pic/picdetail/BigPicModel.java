package com.hao.big_pic.picdetail;

import android.content.Context;

import com.hao.baselib.base.MvcBaseModel;

/**
 * 大图预览接口访问
 * @author WaterWood
 */
public class BigPicModel extends MvcBaseModel<BigPicCallback>{
    public BigPicModel(Context context, BigPicCallback callback) {
        super(context, callback);
    }
}
