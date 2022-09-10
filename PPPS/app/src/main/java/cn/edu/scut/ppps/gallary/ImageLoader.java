package cn.edu.scut.ppps.gallary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载类
 * @author WaterWood
 */
public class ImageLoader {

    /**
     * 单例对象
     */
    private static ImageLoader mInstance;
    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 默认线程个数
     */
    private static final int DEAFAULT_THREAD_COUNT = 1;
    /**
     * 队列方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    /**
     * 与线程绑定的Handler，对线程中的Queue发送消息
     */
    private Handler mPoolThreadHandler;
    /**
     * UI线程的一个Handler
     */
    private Handler mUIHandler;
    /**
     * addTask使用mPoolThreadHandler的信号量，防止mPoolThreadHandler为空
     */
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    /**
     * 是内部队列执行完，再从TaskQueue中取下一个
     */
    private Semaphore mSemaphoreThreadPool;

    /**
     * 私有构造方法
     */
    private ImageLoader(int threadCount,Type type){
        init(threadCount,type);
    }

    /**
     * 单例获取
     * @return
     */
    public static ImageLoader getInstance(){
        if (mInstance == null){
            synchronized (ImageLoader.class){
                if (mInstance == null) {
                    mInstance = new ImageLoader(DEAFAULT_THREAD_COUNT,Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    /**
     * 单例获取
     * @return
     */
    public static ImageLoader getInstance(int threadCount,Type type){
        if (mInstance == null){
            synchronized (ImageLoader.class){
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount,type);
                }
            }
        }
        return mInstance;
    }

    /**
     * 队列类型枚举
     */
    public enum Type{
        FIFO,LIFO
    }

    /**
     * 一系列的初始化操作
     * @param threadCount
     * @param type
     */
    private void init(int threadCount,Type type){
        mPoolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //不断从线程池中取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory/8;
        mLruCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;
        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 根据path加载图片
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView){
        imageView.setTag(path);
        if (mUIHandler == null){
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //获得图片，为imageView回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageview = holder.imageView;
                    String path = holder.path;
                    //将path与getTag存储路径进行比较
                    if (imageview.getTag().toString().equals(path)){
                        imageview.setImageBitmap(bm);
                    }
                }
            };
        }
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm!=null){
            //内存中有的情况
            refreshBitmap(path,imageView,bm);
        }else{
            //内存中没有的情况
            addTask(new Runnable(){
                @Override
                public void run() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        ImageSize imageSize = getImageViewSize(imageView);
                        Bitmap bm = decodeSampledBitmapFromPath(path,imageSize.width,imageSize.height);
                        addBitmapToLruCache(path,bm);
                        refreshBitmap(path,imageView,bm);
                        mSemaphoreThreadPool.release();
                    }
                }
            });
        }
    }

    private Bitmap getBitmapFromLruCache(String key){
        return mLruCache.get(key);
    }

    private class ImgBeanHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    private synchronized void addTask(Runnable runnable){
        mTaskQueue.add(runnable);
        try {
            if (mPoolThreadHandler==null) {
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Runnable getTask(){
        if (mType == Type.FIFO){
            return mTaskQueue.removeFirst();
        }else if (mType == Type.LIFO){
            return mTaskQueue.removeLast();
        }
        return null;
    }

    /**
     * 计算压缩尺寸
     * @param imageView
     * @return
     */
    private ImageSize getImageViewSize(ImageView imageView){
        ImageSize imageSize = new ImageSize();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        int width = imageView.getWidth();
        if (width == 0){
            width = lp.width;//获取imageview在layout中声明的宽度
        }
        if (width<=0){
            width = getImageViewFieldValue(imageView,"mMaxWidth");
        }
        if (width<=0){
            width = displayMetrics.widthPixels;
        }
        int height = imageView.getHeight();
        if (height == 0){
            height = lp.height;//获取imageview在layout中声明的高度
        }
        if (height<=0){
            height = getImageViewFieldValue(imageView,"mMaxHeight");
        }
        if (height<=0){
            height = displayMetrics.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    private class ImageSize{
        int width;
        int height;
    }

    /**
     * 将path转换为bimap带压缩
     * @param path
     * @param width
     * @param height
     * @return
     */
    protected Bitmap decodeSampledBitmapFromPath(String path,int width,int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = caculateInSampleSize(options,width,height);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    /**
     * 计算压缩比
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int caculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width>reqWidth || height > reqHeight){
            int widthRadio = Math.round(width*1.0f/reqWidth);
            int heightRadio = Math.round(height*1.0f/reqHeight);
            inSampleSize = Math.max(widthRadio,heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 将bitmap加入内存缓存中
     * @param path
     * @param bm
     */
    protected void addBitmapToLruCache(String path,Bitmap bm){
        if (getBitmapFromLruCache(path) == null){
            if (bm!=null){
                mLruCache.put(path,bm);
            }
        }
    }

    /**
     * 刷新图片
     * @param path
     * @param imageView
     * @param bitmap
     */
    private void refreshBitmap(String path,ImageView imageView,Bitmap bitmap){
        Message message = Message.obtain();
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bitmap;
        holder.path = path;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    /**
     * 通过反射获取任何对象的任何属性值
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object,String fieldName){
        try {
            int value = 0;
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE){
                value = fieldValue;
            }
            return value;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
