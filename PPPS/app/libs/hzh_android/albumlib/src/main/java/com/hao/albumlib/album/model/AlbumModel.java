package com.hao.albumlib.album.model;

import android.content.Context;
import com.hao.albumlib.album.callback.AlbumCallback;
import com.hao.baselib.base.MvcBaseModel;

public class AlbumModel extends MvcBaseModel<AlbumCallback> {

    public AlbumModel(Context context, AlbumCallback callback) {
        super(context, callback);
    }
}
