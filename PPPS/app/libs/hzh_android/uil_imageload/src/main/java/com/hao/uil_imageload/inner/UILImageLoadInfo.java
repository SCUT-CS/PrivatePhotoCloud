package com.hao.uil_imageload.inner;

import android.graphics.Bitmap;
import android.view.View;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UILImageLoadInfo implements ImageLoadingListener {

    private UILImageLoadListener uilImageLoadListener;

    public UILImageLoadInfo(UILImageLoadListener uilImageLoadListener) {
        this.uilImageLoadListener = uilImageLoadListener;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        uilImageLoadListener.onLoadingStarted(imageUri,view);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        FailReasonBean failReasonBean = new FailReasonBean();
        failReasonBean.setFailReason(failReason);
        uilImageLoadListener.onLoadingFailed(imageUri,view,failReasonBean);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        uilImageLoadListener.onLoadingComplete(imageUri,view,loadedImage);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        uilImageLoadListener.onLoadingCancelled(imageUri,view);
    }
}
