package com.hao.updatelib.core;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.hao.baselib.dialog.CommonDialog;
import com.hao.baselib.dialog.DownLoadDialog;
import com.hao.baselib.utils.PathGetUtil;
import com.hao.baselib.utils.ToastUtil;
import com.hao.okhttplib.http.DownloadListener;
import com.hao.okhttplib.http.DuanDownloadListener;
import com.hao.okhttplib.http.RequestCenter;
import com.hao.updatelib.utils.VersionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 版本更新类
 *
 * @author WaterWood
 */
public class UpdateUtil {
    /**
     * 本类单例对象
     */
    private static UpdateUtil mInstance;
    /**
     * 进度条对话框
     */
    private DownLoadDialog downloadDialog;

    private long totalSum;
    private int totalTime;

    /**
     * 单例获取的方法
     *
     * @return
     */
    public static UpdateUtil getInstance() {
        if (mInstance == null) {
            synchronized (UpdateUtil.class) {
                if (mInstance == null) {
                    mInstance = new UpdateUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 私有构造方法
     */
    private UpdateUtil() {
    }

    /**
     * 开始版本更新的入口
     *
     * @param activity      调用该方法的Activity对象
     * @param newVerionCode 服务器返回的新版本号
     * @param updateExplain 版本更新说明，如果有换行，将"\\\\n"替换为"\n"
     * @param isOne         是否强制更新
     * @param downloadUrl   下载的全路径地址
     * @param fileName      文件名称
     */
    public void startUpdate(final Activity activity
            , int newVerionCode
            , String updateExplain
            , boolean isOne
            , final String downloadUrl
            , String fileName) {
        if (VersionUtil.getVersionCode(activity) < newVerionCode) {
            //当前版本小于服务器版本，需要更新
            showUpdateDialog(activity, updateExplain, isOne, downloadUrl, fileName);
        }
    }

    /**
     * 显示确认取消弹框
     */
    private void showUpdateDialog(final Activity activity
            , final String updateExplain
            , final boolean isOne
            , final String downloadUrl
            , final String fileName) {
        //弹框
        final CommonDialog commonDialog = new CommonDialog(activity);
        commonDialog.setTitleText("版本更新")
                .setContentText(updateExplain)
                .setOne(isOne)
                .setOneColor(0xFF00A684)
                .setTwoColor(0xFFFFFFFF)
                .setTitleSize(20)
                .setContentSize(14)
                .setOneSize(14)
                .setTwoSize(14)
                .setOneDrawable(0xFFE8EFEE)
                .setTwoDrawable(0xFF19BD9B)
                .setTwoClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //用户点击确定操作，弹出进度对话框
                        commonDialog.dismiss();
                        downloadDialog = new DownLoadDialog(activity);
                        downloadDialog.setContent("安装包下载中，请稍后...")
                                .show();
                        //进行下载工作，并修改对话框对应的值，下载完成后打开apk
                        downLoad(activity, updateExplain, isOne, downloadUrl, fileName);
                    }
                })
                .show();
        commonDialog.setCancelable(false);
    }

    /**
     * 显示失败的确认取消弹框
     */
    private void showErrorDialog(final Activity activity
            , final String updateExplain
            , final boolean isOne
            , final String downloadUrl
            , final String fileName) {
        //弹框
        final CommonDialog commonDialog = new CommonDialog(activity);
        commonDialog.setTitleText("下载失败")
                .setContentText("新版本软件下载失败，是否重试？")
                .setOneColor(0xFF00A684)
                .setTwoColor(0xFFFFFFFF)
                .setTitleSize(20)
                .setContentSize(14)
                .setOneSize(14)
                .setTwoSize(14)
                .setOneDrawable(0xFFE8EFEE)
                .setTwoDrawable(0xFF19BD9B)
                .setTwoClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //用户点击确定操作，弹出进度对话框
                        commonDialog.dismiss();
                        downloadDialog = new DownLoadDialog(activity);
                        downloadDialog.setContent("安装包下载中，请稍后...")
                                .show();
                        //进行下载工作，并修改对话框对应的值，下载完成后打开apk
                        downLoad(activity, updateExplain, isOne, downloadUrl, fileName);
                    }
                })
                .show();
        commonDialog.setCancelable(false);
    }

    /**
     * 下载APK
     */
    private void downLoad(final Activity activity
            , final String updateExplain
            , final boolean isOne
            , final String downloadUrl
            , final String fileName) {
        List<String> listPath = new ArrayList<>();
        listPath.add("apk");
        String path = PathGetUtil.getLongwayPath(activity, listPath);
        RequestCenter.getInstance(activity)
                .duanDownload(downloadUrl)
                .setPathDir(path)
                .setDownloadFileName(fileName)
                .setTotalTime(totalTime)
                .setTotalSum(totalSum)
                .setDuanDownListener(new DuanDownloadListener() {
                    @Override
                    public void onDownloadSuccess(final String path) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //消失进度框
                                downloadDialog.dismiss();
                                Log.i("myDownload", "下载成功");
                                //打开apk
                                startInstall(activity, path);
                            }
                        });
                    }

                    @Override
                    public void onDownloading(final int progress) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //设置进度
                                downloadDialog.setProgress(progress);
                            }
                        });

                    }

                    @Override
                    public void onDownloadFailed(final int time, final long sum) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                totalTime = time;
                                totalSum = sum;
                                //消失对话框
                                downloadDialog.dismiss();
                                //吐司下载失败
                                ToastUtil.toastWord(activity, "下载失败");
                                //弹出版本更新对话框
                                showErrorDialog(activity, updateExplain, isOne, downloadUrl, fileName);
                            }
                        });
                    }
                })
                .go();
    }


    /**
     * 打开apk
     *
     * @param activity
     * @param filePath
     */
    private void startInstall(Activity activity, String filePath) {
        //分别进行7.0以上和7.0以下的尝试
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0以上
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName()+".fileProvider", apkfile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        }
        activity.startActivity(intent);
    }
}
