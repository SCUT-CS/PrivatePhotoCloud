package cn.edu.scut.ppps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.edu.scut.ppps.cloud.CloudService;

/**
 * Pipeline for processing.
 * @author Cui Yuxin
 */
public class Pipeline {

    // MainActivity handler for callback
    private Handler mainHandler;
    // Objects
    private Context context;
    private CloudService cloudStorage1;
    private CloudService cloudStorage2;
    // TODO optimize for the parameter
    // Thread pool
    private ThreadPoolExecutor EncryptThreadPool = new ThreadPoolExecutor(5,
            10,
            1000,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(16));
    private ThreadPoolExecutor DecryptThreadPool = new ThreadPoolExecutor(5,
            10,
            1000,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(16));
    private ThreadPoolExecutor ThumbnailThreadPool = new ThreadPoolExecutor(5,
            10,
            1000,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(16));
    // Cloud handler count
    int cloudTotalCount = 0;
    List<String> cloud1Path;
    List<String> cloud2Path;
    // Algorithm handler
    int encryptAlgoHandlerCount = 0;
    int decryptAlgoHandlerCount = 0;
    int thumbnailAlgoHandlerCount = 0;
    @SuppressLint("HandlerLeak")
    private Handler encryptAlgoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.ENCRYPT_SUCCESS) {
                encryptAlgoHandlerCount--;
                if (encryptAlgoHandlerCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_ALGORITHM);
                    mainHandler.sendEmptyMessage(Utils.START_CLOUD);
                    int length = cloud1Path.size();
                    cloudTotalCount = length * 2;
                    for (int i = 0; i < length; i++) {
                        cloudStorage1.upload(cloud1Path.get(i));
                        cloudStorage2.upload(cloud2Path.get(i));
                    }
                }
            } else if (msg.what == Utils.CLOUD_SUCCESS) {
                cloudTotalCount--;
                if (cloudTotalCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_CLOUD);
                }
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler decryptAlgoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.CLOUD_SUCCESS) {
                cloudTotalCount--;
                if (cloudTotalCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_CLOUD);
                    mainHandler.sendEmptyMessage(Utils.START_ALGORITHM);
                    String cachePath = context.getCacheDir().getAbsolutePath();
                    String savePath1 = cachePath + File.separator + "Disk1" + File.separator;
                    String savePath2 = cachePath + File.separator + "Disk2" + File.separator;
                    decryptAlgoHandlerCount = cloud2Path.size();
                    try {
                        for (int i = 0; i < cloud2Path.size(); i++) {
                            String path1 = savePath1 + Utils.getFileName(cloud2Path.get(i));
                            String path2 = savePath2 + Utils.getFileName(cloud2Path.get(i));
                            EncryptThreadPool.submit(new Decrypt(path1, path2, context, false, decryptAlgoHandler));
                        }
                    } catch (Exception e) {
                        mainHandler.sendEmptyMessage(Utils.ERROR);
                    }
                }
            } else if (msg.what == Utils.DECRYPT_SUCCESS) {
                decryptAlgoHandlerCount--;
                if (decryptAlgoHandlerCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_ALGORITHM);
                }
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler thumbnailAlgoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.FINISH_CLOUD) {
                cloudTotalCount--;
                if (cloudTotalCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_CLOUD);
                    mainHandler.sendEmptyMessage(Utils.START_ALGORITHM);
                    String cachePath = context.getCacheDir().getAbsolutePath();
                    String savePath1 = cachePath + File.separator + "Disk1Thumbnail" + File.separator;
                    String savePath2 = cachePath + File.separator + "Disk2Thumbnail" + File.separator;
                    thumbnailAlgoHandlerCount = cloud1Path.size();
                    try {
                        for (int i = 0; i < cloud1Path.size(); i++) {
                            String path1 = savePath1 + cloud1Path.get(i);
                            String path2 = savePath2 + cloud1Path.get(i);
                            EncryptThreadPool.submit(new Decrypt(path1, path2, context, true, decryptAlgoHandler));
                        }
                    } catch (Exception e) {
                        mainHandler.sendEmptyMessage(Utils.ERROR);
                    }
                }
            } else if (msg.what == Utils.THUMBNAIL_SUCCESS) {
                thumbnailAlgoHandlerCount--;
                if (thumbnailAlgoHandlerCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_ALGORITHM);
                }
            }
        }
    };

    /**
     * Constructor.
     * @param handler Handler for mainActivity.
     * @param context Context.
     * @param cloudStorage1 Cloud storage 1.
     * @param cloudStorage2 Cloud storage 2.
     * @author Cui Yuxin
     */
    public Pipeline(Handler handler, Context context, CloudService cloudStorage1, CloudService cloudStorage2) {
        this.mainHandler = handler;
        this.context = context;
        this.cloudStorage1 = cloudStorage1;
        this.cloudStorage2 = cloudStorage2;
    }

    /**
     * Init the pipeline.
     * @author Cui Yuxin
     */
    private void init() {
        encryptAlgoHandlerCount = 0;
        decryptAlgoHandlerCount = 0;
        thumbnailAlgoHandlerCount = 0;
        cloudTotalCount = 0;
    }

    /**
     * Encrypt pipeline.
     * @param path Paths of the file.
     */
    public void encryptPipeline(String[] path) {
        mainHandler.sendEmptyMessage(Utils.START_ALGORITHM);
        init();
        int length = path.length;
        // cloud
        cloud1Path = new ArrayList<>();
        cloud2Path = new ArrayList<>();
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath1 = cachePath + File.separator + "Disk1" + File.separator;
        String savePath2 = cachePath + File.separator + "Disk2" + File.separator;
        for (int i = 0; i < length; i++) {
            try {
                cloud1Path.set(i, savePath1 + Utils.getFileName(path[i]) + ".ori.webp");
                cloud2Path.set(i, savePath2 + Utils.getFileName(path[i]) + ".ori.webp");
            } catch (IOException e) {
                e.printStackTrace();
                mainHandler.sendEmptyMessage(Utils.ERROR);
                return;
            }
        }
        cloudStorage1.setHandler(encryptAlgoHandler);
        cloudStorage2.setHandler(encryptAlgoHandler);
        // algorithm
        encryptAlgoHandlerCount = length;
        for (String s : path) {
            EncryptThreadPool.submit(new Encrypt(s, context, encryptAlgoHandler));
        }
    }

    /**
     * Decrypt pipeline.
     * @param path Paths of the file.
     */
    public void decryptPipeline(String[] path) {
        mainHandler.sendEmptyMessage(Utils.START_CLOUD);
        init();
        cloud1Path = new ArrayList<>();
        cloud2Path = Arrays.asList(path);
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath1 = cachePath + File.separator + "Disk1" + File.separator;
        String savePath2 = cachePath + File.separator + "Disk2" + File.separator;
        List<String> existFiles = Utils.getAllFile(savePath1);
        for (String s : path) {
            try {
                String fileName = Utils.getFileName(s);
                if (!existFiles.contains(s)) {
                    cloud1Path.add(fileName);
                }
            } catch (Exception e) {
                mainHandler.sendEmptyMessage(Utils.ERROR);
            }
        }
        cloudStorage1.setHandler(decryptAlgoHandler);
        cloudStorage2.setHandler(decryptAlgoHandler);
        cloudTotalCount = cloud1Path.size() * 2;
        for (String s : cloud1Path) {
            cloudStorage1.download(s, savePath1 + s);
            cloudStorage2.download(s, savePath2 + s);
        }
    }

    public void thumbnailPipeline() {
        mainHandler.sendEmptyMessage(Utils.START_CLOUD);
        init();
        String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "Thumbnail" + File.separator;
        cloudStorage1.setHandler(thumbnailAlgoHandler);
        cloudStorage2.setHandler(thumbnailAlgoHandler);
        cloud1Path = new ArrayList<>();
        List<String> existFiles = Utils.getAllFile(imgPath);
        List<String> cloudFiles = null;
        try {
            cloudFiles = cloudStorage1.getFileList();
        } catch (Exception e) {
            e.printStackTrace();
            mainHandler.sendEmptyMessage(Utils.ERROR);
        }
        for (String s : cloudFiles) {
            if (!existFiles.contains(s)) {
                cloud1Path.add(s);
            }
        }
        cloudTotalCount = cloud1Path.size() * 2;
        for (String s : cloud1Path) {
            cloudStorage1.download(s, imgPath + s);
            cloudStorage2.download(s, imgPath + s);
        }
    }
}
