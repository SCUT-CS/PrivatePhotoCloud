package com.hao.okhttplib.http;

import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 文件断点下载的回调
 *
 * @author WaterWood
 */
public class CommonDuanDownloadCallback implements Callback {

    private DuanDownloadListener duanDownloadListener;
    private Handler mDeliveryHandler;//进行消息的转发
    private String pathDir;
    private String downloadFileName;
    private int downTime;//下载次数
    private int totalTime;
    private long totalSum;

    /**
     * 构造方法
     */
    public CommonDuanDownloadCallback(String pathDir, String downloadFileName,DuanDownloadListener duanDownloadListener
            ,int totalTime,long totalSum) {
        this.duanDownloadListener = duanDownloadListener;
        this.pathDir = pathDir;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
        this.downloadFileName = downloadFileName;
        this.totalTime = totalTime;
        this.totalSum = totalSum;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                duanDownloadListener.onDownloadFailed(totalTime,totalSum);
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        downTime = 0;
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        //传入的必须是一个文件夹
        File fileParam = new File(pathDir);
        if (!fileParam.exists()) {
            //不存在该文件夹
            fileParam.mkdirs();
        }
        try {
            is = response.body().byteStream();
            long total = response.body().contentLength();
            File file = new File(pathDir, downloadFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            long sum = totalSum;
            while (true) {
                downTime++;
                if (downTime > totalTime) {
                    if ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        duanDownloadListener.onDownloading(progress);
                        totalSum = sum;
                    }else{
                        break;
                    }
                }
            }
            fos.flush();
            // 下载完成
            duanDownloadListener.onDownloadSuccess(file.getPath());
        } catch (Exception e) {
            if (downTime > totalTime) {
                totalTime = downTime;
            }
            duanDownloadListener.onDownloadFailed(totalTime,totalSum);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }
    }
}
