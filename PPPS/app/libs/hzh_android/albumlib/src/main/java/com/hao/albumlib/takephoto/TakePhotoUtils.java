package com.hao.albumlib.takephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.hao.baselib.base.BaseActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 拍照工具类
 *
 * @author WaterWood
 */
public class TakePhotoUtils {

    /**
     * 启动相机
     *
     * @param activity
     * @param dirPath
     */
    public static File startCamera(Activity activity, String dirPath) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String takePhotoName = getPhotoFileName() + ".jpg";
            String fileName = takePhotoName;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File filePic = new File(dirPath + File.separator + fileName);
            Uri photoUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0以上
                photoUri = FileProvider.getUriForFile(activity, "com.hao.hzh_android.fileProvider", filePic);
            } else {
                //7.0以下
                photoUri = Uri.fromFile(filePic);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            activity.startActivityForResult(intent, BaseActivity.CAMERA_RC);
            return filePic;
        } else {
            return null;
        }
    }

    /**
     * 根据时间给照片命名
     *
     * @return
     */
    private static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }
}
