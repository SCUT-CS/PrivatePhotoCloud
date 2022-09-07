package com.hao.uil_imageload.inner;

import android.graphics.Bitmap;
import android.view.View;

/**
 * 图片加载监听接口
 * @author WateWood
 */
public interface UILImageLoadListener{
    void onLoadingStarted(String imageUri, View view);
    void onLoadingFailed(String imageUri, View view, FailReasonBean failReasonBean);
    void onLoadingComplete(String imageUri, View view, Bitmap loadedImage);
    void onLoadingCancelled(String imageUri, View view);
}
