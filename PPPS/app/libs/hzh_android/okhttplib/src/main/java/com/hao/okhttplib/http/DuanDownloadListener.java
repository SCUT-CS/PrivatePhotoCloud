package com.hao.okhttplib.http;

/**
 * 下载回调
 * @author WaterWood
 */
public interface DuanDownloadListener {
    /**
     * 下载成功
     */
    void onDownloadSuccess(String path);

    /**
     * @param progress
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载失败
     */
    void onDownloadFailed(int time,long sum);
}
