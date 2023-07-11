package cn.edu.scut.ppps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.edu.scut.ppps.cloud.CloudService;
import cn.edu.scut.ppps.crypto.Decrypt;
import cn.edu.scut.ppps.crypto.Encrypt;

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
    // Thread pool
    private ThreadPoolExecutor cryptThreadPool = new ThreadPoolExecutor(1,
            8,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(20));
    private ThreadPoolExecutor thumbnailThreadPool = new ThreadPoolExecutor(2,
            8,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(100));
    // Cloud handler count
    private int cloudTotalCount = 0;
    private List<String> cloud1Path;
    private List<String> cloud2Path;
    // Algorithm handler
    private int encryptAlgoHandlerCount = 0;
    private int decryptAlgoHandlerCount = 0;
    private int thumbnailAlgoHandlerCount = 0;
    @SuppressLint("HandlerLeak")
    private Handler encryptAlgoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.ENCRYPT_SUCCESS) {
                encryptAlgoHandlerCount--;
                if (encryptAlgoHandlerCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_ALGORITHM);
                    if (isSingle) {
                        Intent intent = new Intent();
                        intent.putExtra("imgPath", path);
                        String cachePath = context.getCacheDir().getAbsolutePath();
                        intent.putExtra("cachePath", cachePath);
                        intent.setClass(context, SingleEncryptActivity.class);
                        context.startActivity(intent);
                    }
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
                    mainHandler.sendEmptyMessage(Utils.SUCCESS);
                }
            } else if (msg.what == Utils.CLOUD_FAILURE) {
                Toast.makeText(context, "上传失败!", Toast.LENGTH_SHORT).show();
                cloudTotalCount--;
                if (cloudTotalCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_CLOUD);
                    mainHandler.sendEmptyMessage(Utils.SUCCESS);
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
                            cryptThreadPool.submit(new Decrypt(path1, path2, context, decryptAlgoHandler));
                        }
                    } catch (Exception e) {
                        mainHandler.sendEmptyMessage(Utils.ERROR);
                    }
                }
            } else if (msg.what == Utils.DECRYPT_SUCCESS) {
                decryptAlgoHandlerCount--;
                if (decryptAlgoHandlerCount <= 0) {
                    mainHandler.sendEmptyMessage(Utils.FINISH_ALGORITHM);
                    mainHandler.sendEmptyMessage(Utils.SUCCESS);
                    if (isSingle) {
                        Intent intent = new Intent();
                        String path1 = null;
                        String path2 = null;
                        String cachePath = context.getCacheDir().getAbsolutePath();
                        String savePath1 = cachePath + File.separator + "Disk1" + File.separator;
                        String savePath2 = cachePath + File.separator + "Disk2" + File.separator;
                        try {
                            for (int i = 0; i < cloud2Path.size(); i++) {
                                path1 = savePath1 + Utils.getFileName(cloud2Path.get(i));
                                path2 = savePath2 + Utils.getFileName(cloud2Path.get(i));
                            }
                        } catch (Exception e) {
                            mainHandler.sendEmptyMessage(Utils.ERROR);
                        }
                        intent.putExtra("imgPath1", path1);
                        intent.putExtra("imgPath2", path2);
                        intent.setClass(context, SingleDecryptActivity.class);
                        context.startActivity(intent);
                    }
                }
            } else if (msg.what == Utils.CLOUD_FAILURE) {
                mainHandler.sendEmptyMessage(Utils.ERROR);
                cloud2Path.clear();
            }
        }
    };

    boolean isSingle = false;
    String path;

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
        isSingle = (path.length == 1);
        this.path = path[0];
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
                cloud1Path.add(savePath1 + Utils.getFileName(path[i]) + ".ori.webp");
                cloud2Path.add(savePath2 + Utils.getFileName(path[i]) + ".ori.webp");
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
            cryptThreadPool.submit(new Encrypt(s, context, encryptAlgoHandler));
        }
    }

    /**
     * Decrypt pipeline.
     * @param path Paths of the file.
     */
    public void decryptPipeline(String[] path) {
        isSingle = (path.length == 1);
        this.path = path[0];
        mainHandler.sendEmptyMessage(Utils.START_CLOUD);
        init();
        cloud1Path = new ArrayList<>();
        cloud2Path = Arrays.asList(path);
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath1 = cachePath + File.separator + "Disk1" + File.separator;
        List<String> existFiles = Utils.getAllFile(savePath1);
        for (String s : path) {
            try {
                String fileName = Utils.getFileName(s);
                if (Objects.isNull(existFiles) || !Objects.requireNonNull(existFiles).contains(fileName)) {
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
            cloudStorage1.download(s, "Disk1");
            cloudStorage2.download(s, "Disk2");
        }
        if (cloud1Path.size() == 0) {
            decryptAlgoHandler.sendEmptyMessage(Utils.CLOUD_SUCCESS);
        }
    }
}
