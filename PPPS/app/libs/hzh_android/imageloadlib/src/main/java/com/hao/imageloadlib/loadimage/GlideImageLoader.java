package com.hao.imageloadlib.loadimage;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

/**
 * glide实现的图片加载工具类
 * @author WaterWood
 */
public class GlideImageLoader {

    private static GlideImageLoader imageLoader;

    /**
     * 获取对象实例
     * @return
     */
    public static GlideImageLoader getInstance() {
        if (imageLoader == null){
            synchronized (GlideImageLoader.class){
                if (imageLoader == null){
                    imageLoader = new GlideImageLoader();
                }
            }
        }
        return imageLoader;
    }

    /**
     * 私有构造方法
     */
    private GlideImageLoader() {
    }

    /**
     * 加载普通图片
     * @param context 上下文
     * @param imgUrl 图片地址
     * @param defaultPic 占位图
     * @param errorPic 错误加载图
     */
    public void loadImage(Context context, String imgUrl, int defaultPic,int errorPic,ImageView imageView){
        final RequestOptions options = new RequestOptions();
        options.skipMemoryCache(false);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.priority(Priority.HIGH);
        options.error(errorPic);
        options.placeholder(defaultPic);
        Glide.with(context).load(imgUrl).apply(options).into(imageView);
    }

    /**
     * 加载圆角图片
     * @param context
     * @param imgUrl
     * @param round
     * @param imageView
     */
    public void loadCornerImage(Context context,String imgUrl,int round,ImageView imageView){
        Glide.with(context)
                .load(imgUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(round)))//圆角半径
                .into(imageView);
    }

    /**
     * 加载圆形图片
     * @param context
     * @param imgUrl
     * @param imageView
     */
    public void loadRoundImage(Context context,String imgUrl,ImageView imageView){
        Glide.with(context)
                .load(imgUrl)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(imageView);
    }

    /**
     * 加载资源图片
     * @param context 上下文
     * @param imgResource 资源图片
     * @param defaultPic 占位图
     * @param errorPic 错误加载图
     */
    public void loadImage(Context context, int imgResource, int defaultPic, int errorPic, ImageView imageView){
        final RequestOptions options = new RequestOptions();
        options.skipMemoryCache(false);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.priority(Priority.HIGH);
        options.error(errorPic);
        options.placeholder(defaultPic);
        Glide.with(context).load(imgResource).apply(options).into(imageView);
    }
}
