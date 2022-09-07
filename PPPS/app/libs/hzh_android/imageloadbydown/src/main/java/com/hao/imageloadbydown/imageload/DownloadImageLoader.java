package com.hao.imageloadbydown.imageload;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import com.hao.baselib.utils.NullUtil;
import com.hao.baselib.utils.PathGetUtil;
import com.hao.imageloadbydown.db.ImageLoadDbHelper;
import com.hao.imageloadbydown.entity.ImageBean;
import com.hao.imageloadbydown.inner.DownloadPicInterface;
import com.hao.imageloadbydown.thread.SingleLineUtil;
import com.hao.imageloadbydown.utils.UrlUtil;
import com.hao.imageloadlib.loadimage.GlideImageLoader;
import com.hao.okhttplib.http.DownloadListener;
import com.hao.okhttplib.http.RequestCenter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * 指定路径下载图片加载工具类
 * @author WaterWood
 */
public class DownloadImageLoader {

    private static DownloadImageLoader mInstance;
    private ExecutorService singleThreadExecutor;
    private Map<String, ArrayList<ImageBean>> listPicsMap;
    private String imgPathDir;//图片地址

    /**
     * 获取对象实例
     *
     * @return
     */
    public static DownloadImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (DownloadImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new DownloadImageLoader();
                }
            }
        }
        return mInstance;
    }

    /**
     * 私有构造方法
     */
    private DownloadImageLoader() {
        //初始化线程池
        singleThreadExecutor = SingleLineUtil.getInstance().getSingle();
        //初始化Map
        listPicsMap = new HashMap<>();
    }

    /**
     * 指定图片加载地址
     *
     * @param imgPathDir
     */
    public void setImgPathDir(String imgPathDir) {
        this.imgPathDir = imgPathDir;
    }

    /**
     * 加载普通图片
     *
     * @param activity   上下文
     * @param imgUrl     图片地址
     * @param defaultPic 占位图
     * @param errorPic   错误加载图
     */
    public void loadImage(final Activity activity, final String imgUrl, final int defaultPic, final int errorPic, final ImageView imageView) {
        if (imgUrl.startsWith("http")){
            queryImgByUrl(activity, imgUrl, defaultPic, errorPic, 0, imageView, 0);
        }else{
            loadImage(activity, imgUrl, defaultPic, errorPic, 0, imageView, 0);
        }
    }

    /**
     * 加载圆角图片
     *
     * @param activity
     * @param imgUrl
     * @param round
     * @param imageView
     */
    public void loadCornerImage(Activity activity, String imgUrl, int round, ImageView imageView) {
        if (imgUrl.startsWith("http")) {
            queryImgByUrl(activity, imgUrl, 0, 0, round, imageView, 1);
        }else{
            loadImage(activity, imgUrl, 0, 0, round, imageView, 1);
        }
    }

    /**
     * 加载圆形图片
     *
     * @param activity
     * @param imgUrl
     * @param imageView
     */
    public void loadRoundImage(Activity activity, String imgUrl, ImageView imageView) {
        if (imgUrl.startsWith("http")) {
            queryImgByUrl(activity, imgUrl, 0, 0, 0, imageView, 2);
        }else{
            loadImage(activity, imgUrl, 0, 0, 0, imageView, 2);
        }
    }

    /**
     * 加载资源图片
     *
     * @param context     上下文
     * @param imgResource 资源图片
     * @param defaultPic  占位图
     * @param errorPic    错误加载图
     */
    public void loadImage(Context context, int imgResource, int defaultPic, int errorPic, ImageView imageView) {
        GlideImageLoader.getInstance().loadImage(context, imgResource, defaultPic, errorPic, imageView);
    }

    /**
     * 根据url查询对应的图片
     *
     * @param activity
     * @param imgUrl
     * @param defaultPic
     * @param errorPic
     * @param round
     * @param imageView
     * @param flag       0:普通图片   1：圆角图片  2：圆形图片
     */
    private void queryImgByUrl(final Activity activity, final String imgUrl, final int defaultPic, final int errorPic
            , final int round, final ImageView imageView, final int flag) {
        //查询数据库里有没有这个图片
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //查询指定imgUrl有没有
                ImageLoadDbHelper imageLoadDbHelper = ImageLoadDbHelper.getInstance(activity, 0);
                imageLoadDbHelper.openReadLink();
                ArrayList<ImageBean> imageBeans = imageLoadDbHelper.queryImageFileDb("isdelete=0 and imgurl='" + imgUrl + "'");
                imageLoadDbHelper.closeLink();
                listPicsMap.put(imgUrl, imageBeans);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //转入主线程
                        if (NullUtil.isListEmpty(listPicsMap.get(imgUrl))) {
                            //没有下载过该图片，开始去下载
                            downloadImg(activity, imgUrl, defaultPic, errorPic, round, imageView, flag);
                            listPicsMap.remove(imgUrl);
                        } else {
                            //虽然数据库是有数据的，但还是要判断本地有没有
                            File file = new File(listPicsMap.get(imgUrl).get(0).getImgpath());
                            if (file.exists()) {
                                //下载过该图片了，直接进行图片的加载
                                loadImage(activity, listPicsMap.get(imgUrl).get(0).getImgpath(), defaultPic, errorPic, round, imageView, flag);
                                listPicsMap.remove(imgUrl);
                            } else {
                                //本地是没有这张图的
                                downloadImg(activity, imgUrl, defaultPic, errorPic, round, imageView, flag);
                                listPicsMap.remove(imgUrl);
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 下载图片
     *
     * @param url
     * @param flag 0:普通图片   1：圆角图片  2：圆形图片
     */
    private void downloadImg(final Activity activity, final String url, final int defaultPic, final int errorPic
            , final int round, final ImageView imageView, final int flag) {
        List<String> list = new ArrayList<>();
        list.add("hzh");
        list.add("pics");
        if (NullUtil.isStringEmpty(imgPathDir)) {
            //没有路径，用自己的
            imgPathDir = PathGetUtil.getPath(list);
        }
        //这个图片没有下载中，正常下载即可
        RequestCenter.getInstance(activity)
                .download(url)
                .setPathDir(imgPathDir)
                .setDownloadFileName(UrlUtil.getNameFromUrl(url))
                .setDownListener(new DownloadListener() {
                    @Override
                    public void onDownloadSuccess(final String path) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //加载图片
                                loadImage(activity, path, defaultPic, errorPic, round, imageView, flag);
                                //写入数据库
                                singleThreadExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        //组装数据实体
                                        ImageBean imageBean = new ImageBean();
                                        imageBean.setUuid(UUID.randomUUID().toString());
                                        imageBean.setImgurl(url);
                                        imageBean.setImgpath(path);
                                        imageBean.setIsdelete(0);
                                        List<ImageBean> listImageBean = new ArrayList<>();
                                        listImageBean.add(imageBean);
                                        //操作数据库
                                        ImageLoadDbHelper imageLoadDbHelper = ImageLoadDbHelper.getInstance(activity, 0);
                                        imageLoadDbHelper.openReadLink();
                                        imageLoadDbHelper.insertImageFileDb(listImageBean);
                                        imageLoadDbHelper.closeLink();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {
                        //如果下载失败直接用图片框架加载
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadImage(activity, url, defaultPic, errorPic, round, imageView, flag);
                            }
                        });
                    }
                })
                .go();
    }

    /**
     * 真正的加载图片
     *
     * @param activity
     * @param imgUrl
     * @param defaultPic
     * @param errorPic
     * @param round
     * @param imageView
     * @param flag
     */
    private void loadImage(final Activity activity, final String imgUrl, final int defaultPic, final int errorPic
            , final int round, final ImageView imageView, final int flag) {
        if (flag == 0) {
            loadImageReal(activity, imgUrl, defaultPic, errorPic, imageView);
        } else if (flag == 1) {
            loadCornerImageReal(activity, imgUrl, round, imageView);
        } else if (flag == 2) {
            loadRoundImageReal(activity, imgUrl, imageView);
        }
    }

    /**
     * 加载普通图片
     *
     * @param activity
     * @param imgUrl
     * @param defaultPic
     * @param errorPic
     * @param imageView
     */
    private void loadImageReal(final Activity activity, final String imgUrl, int defaultPic, int errorPic, ImageView imageView) {
        GlideImageLoader.getInstance().loadImage(activity, imgUrl, defaultPic, errorPic, imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param activity
     * @param imgUrl
     * @param round
     * @param imageView
     */
    private void loadCornerImageReal(Activity activity, String imgUrl, int round, ImageView imageView) {
        GlideImageLoader.getInstance().loadCornerImage(activity, imgUrl, round, imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param activity
     * @param imgUrl
     * @param imageView
     */
    private void loadRoundImageReal(Activity activity, String imgUrl, ImageView imageView) {
        GlideImageLoader.getInstance().loadRoundImage(activity, imgUrl, imageView);
    }

    /**
     * 一键清除所有缓存图片
     */
    public void deleteAllPic(final Activity activity) {
        File file = new File(imgPathDir);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ImageLoadDbHelper imageLoadDbHelper = ImageLoadDbHelper.getInstance(activity, 0);
                imageLoadDbHelper.openReadLink();
                imageLoadDbHelper.deleteImageFileDb();
                imageLoadDbHelper.closeLink();
            }
        });
    }

    /**
     * 开放的图片下载功能
     */
    public void downloadPics(final Activity activity, final String url, final DownloadPicInterface downloadPicInterface) {
        //先判断要下载的图片在数据库中有没有
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //查询指定imgUrl有没有
                ImageLoadDbHelper imageLoadDbHelper = ImageLoadDbHelper.getInstance(activity, 0);
                imageLoadDbHelper.openReadLink();
                ArrayList<ImageBean> imageBeans = imageLoadDbHelper.queryImageFileDb("isdelete=0 and imgurl='" + url + "'");
                imageLoadDbHelper.closeLink();
                listPicsMap.put(url, imageBeans);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //转入主线程
                        if (NullUtil.isListEmpty(listPicsMap.get(url))) {
                            //没有下载过该图片，开始去下载
                            realDownload(activity,url,downloadPicInterface);
                        } else {
                            //虽然数据库是有数据的，但还是要判断本地有没有
                            File file = new File(listPicsMap.get(url).get(0).getImgpath());
                            if (file.exists()) {
                                //下载过该图片了
                                downloadPicInterface.success(listPicsMap.get(url).get(0).getImgpath());
                            } else {
                                //本地是没有这张图的
                                realDownload(activity,url,downloadPicInterface);
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 真实下载图片
     * @param activity
     * @param url
     * @param downloadPicInterface
     */
    private void realDownload(final Activity activity, final String url, final DownloadPicInterface downloadPicInterface){
        List<String> list = new ArrayList<>();
        list.add("hzh");
        list.add("pics");
        if (NullUtil.isStringEmpty(imgPathDir)) {
            //没有路径，用自己的
            imgPathDir = PathGetUtil.getPath(list);
        }
        //这个图片没有下载中，正常下载即可
        RequestCenter.getInstance(activity)
                .download(url)
                .setPathDir(imgPathDir)
                .setDownloadFileName(UrlUtil.getNameFromUrl(url))
                .setDownListener(new DownloadListener() {
                    @Override
                    public void onDownloadSuccess(final String path) {
                        //写入数据库
                        singleThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                //组装数据实体
                                ImageBean imageBean = new ImageBean();
                                imageBean.setUuid(UUID.randomUUID().toString());
                                imageBean.setImgurl(url);
                                imageBean.setImgpath(path);
                                imageBean.setIsdelete(0);
                                List<ImageBean> listImageBean = new ArrayList<>();
                                listImageBean.add(imageBean);
                                //操作数据库
                                ImageLoadDbHelper imageLoadDbHelper = ImageLoadDbHelper.getInstance(activity, 0);
                                imageLoadDbHelper.openReadLink();
                                imageLoadDbHelper.insertImageFileDb(listImageBean);
                                imageLoadDbHelper.closeLink();
                            }
                        });
                        downloadPicInterface.success(path);
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {
                        downloadPicInterface.failure();
                    }
                })
                .go();
    }
}
