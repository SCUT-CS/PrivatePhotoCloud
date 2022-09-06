package com.hao.hzh_android.home.model;

import android.content.Context;
import com.hao.baselib.base.MvcBaseModel;
import com.hao.hzh_android.home.callback.VideoCallback;

public class VideoModel extends MvcBaseModel<VideoCallback> {

    public VideoModel(Context context, VideoCallback callback) {
        super(context, callback);
    }
}
