package cn.edu.scut.ppps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
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
    int cloudHandler1Count = 0;
    int cloudHandler2Count = 0;
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

                }
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler decryptAlgoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.DECRYPT_SUCCESS) {
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
            if (msg.what == Utils.THUMBNAIL_SUCCESS) {
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
        cloudHandler1Count = 0;
        cloudHandler2Count = 0;
    }

    /**
     * Encrypt pipeline.
     * @param path Paths of the file.
     */
    public void encryptPipeline(String[] path) {
        init();
        int length = path.length;
        // TODO 准备上传路径
        mainHandler.sendEmptyMessage(Utils.START_ALGORITHM);
        encryptAlgoHandlerCount = length;
        for (int i = 0; i < length; i++) {
            Encrypt encrypt = new Encrypt(path[i], context, encryptAlgoHandler);
            EncryptThreadPool.submit(encrypt);
        }
    }

    private void uploadCloud(String[] path1, String[] path2) {
        int length = path1.length;
        cloudHandler1Count = length;
        cloudHandler2Count = length;
        for (int i = 0; i < length; i++) {

        }
    }


}
