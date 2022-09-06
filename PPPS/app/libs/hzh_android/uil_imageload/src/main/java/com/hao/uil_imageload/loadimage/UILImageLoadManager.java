package com.hao.uil_imageload.loadimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.hao.uil_imageload.R;
import com.hao.uil_imageload.inner.UILImageLoadInfo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * UIL图片加载组件
 * @author WaterWood
 */
public class UILImageLoadManager {

    /**
     * 标明我们的UIL最多可以有多少条线程
     */
    private static final int THREAD_COUNT = 4;
    /**
     * 标明我们图片加载的一个优先级
     */
    private static final int PROPRITY = 2;
    /**
     * 标明UIL可以最多缓存多少图片
     */
    private static final int DISK_CACHE_SIZE = 50*1024;
    /**
     * 链接的超时时间
     */
    private static final int CONNECTION_TIME_OUT = 5*1000;
    /**
     * 读取的超时时间
     */
    private static final int READ_TIME_OUT = 30*1000;
    /**
     * UniversalImageLoader对象
     */
    private static ImageLoader mImageLoader;
    /**
     * 本类单例对象
     */
    private static UILImageLoadManager mInstance;

    /**
     * 单例获取的方法
     * @param context
     * @return
     */
    public static UILImageLoadManager getInstance(Context context){
        if (mInstance == null){
            synchronized (UILImageLoadManager.class){
                if (mInstance == null){
                    mInstance = new UILImageLoadManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 私有构造方法
     * @param context
     */
    private UILImageLoadManager(Context context) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(context)
                .threadPoolSize(THREAD_COUNT)//配置图片下载线程的最大数量
                .threadPriority(Thread.NORM_PRIORITY - PROPRITY)//配置图片下载的优先级
                .denyCacheImageMultipleSizesInMemory()//防止缓存多套尺寸的图片到我们的内存中
                .memoryCache(new WeakMemoryCache())//使用弱引用内存缓存，内存不足会回收我们的图片
                .diskCacheSize(DISK_CACHE_SIZE)//分配硬盘缓存大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//使用MD5命名文件
                .tasksProcessingOrder(QueueProcessingType.LIFO)//图片下载顺序
                .defaultDisplayImageOptions(getDefaultOptions())//默认的图片加载器
                .imageDownloader(new BaseImageDownloader(context,CONNECTION_TIME_OUT,READ_TIME_OUT))//设置图片下载器
                .writeDebugLogs()//debug环境下会输出日志
                .build();
        //将configuration加载到ImageLoader
        ImageLoader.getInstance().init(configuration);
        //获取UIL对象
        mImageLoader = ImageLoader.getInstance();
    }


    /**
     * 图片加载器
     * @return
     */
    private DisplayImageOptions getDefaultOptions(){
        DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .showImageForEmptyUri(R.mipmap.ic_seize)//在我们图片地址为空的时候加载指定的图片
                .showImageOnFail(R.mipmap.ic_error)//图片下载失败的时候显示的图片
                .cacheInMemory(true)//设置图片可以缓存在内存
                .cacheOnDisk(true)//设置图片可以缓存在硬盘
                .bitmapConfig(Bitmap.Config.RGB_565)//使用的图片解码类型
                .decodingOptions(new BitmapFactory.Options())//图片解码配置
                .build();
        return options;
    }

    /**
     * 显示图片调用
     * @param imageView
     * @param url
     * @param options
     * @param listener
     */
    public void displayImage(ImageView imageView
            , String url
            , DisplayImageOptions options
            , UILImageLoadInfo listener){
        if (mImageLoader!=null){
            mImageLoader.displayImage(url,imageView,options,listener);
        }
    }

    /**
     * 显示图片调用
     * @param imageView
     * @param url
     * @param listener
     */
    public void displayImage(ImageView imageView
            , String url
            , UILImageLoadInfo listener){
        displayImage(imageView,url,null,listener);
    }

    /**
     * 显示图片调用
     * @param imageView
     * @param url
     */
    public void displayImage(ImageView imageView,String url){
        displayImage(imageView,url,null);
    }
}
