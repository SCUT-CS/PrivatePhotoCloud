package cn.edu.scut.ppps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Pipeline for processing.
 * @author Cui Yuxin
 */
public class Pipeline {

    // TODO optimize for the parameter
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
    private Handler encryptHandler;
    private Handler decryptHandler;
    private Handler thumbnailHandler;
    private Context context;
    private CloudService cloudStorage1;
    private CloudService cloudStorage2;
    // 该参数负责获取云存储的执行结果
    @SuppressLint("HandlerLeak")
    private Handler cloudHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.CLOUD_SUCCESS) {
                // scucess
            } else {
                // fail
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler cloudHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Utils.CLOUD_SUCCESS) {
                // scucess
            } else {
                // fail
            }
        }
    };

    /**
     * Constructor.
     * @param encryptHandler Handler for encrypting.
     * @param decryptHandler Handler for decrypting.
     * @param thumbnailHandler Handler for generating thumbnail.
     * @param context Context.
     */
    public Pipeline(Handler encryptHandler, Handler decryptHandler, Handler thumbnailHandler, Context context){
        this.encryptHandler = encryptHandler;
        this.decryptHandler = decryptHandler;
        this.thumbnailHandler = thumbnailHandler;
        this.context = context;
    }

    public void encryptSingleFile(String path) {
        encryptHandler.sendEmptyMessage(Utils.START_ALGORITHM);
        Encrypt encrypt = new Encrypt(path, context);
        Future<Bitmap[]> results = EncryptThreadPool.submit(encrypt);
        try {
            Bitmap[] result = results.get();
        } catch (Exception e) {
            encryptHandler.sendEmptyMessage(Utils.ALGORITHM_ERROR);
        }
        encryptHandler.sendEmptyMessage(Utils.FINISH_ALGORITHM);

    }

}
