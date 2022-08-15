package cn.edu.scut.ppps;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utils class.
 * @author Cui Yuxin
 */
public class Utils {

    /**
     * Open an image.
     * @param imgPath The path of the image.
     * @author Cui Yuxin
     */
    public static Bitmap openImg(String imgPath) throws IOException {
        ImageDecoder.Source source = ImageDecoder.createSource(new File(imgPath));
        return ImageDecoder.decodeBitmap(source);
    }

    /**
     * Get file name.
     * @param filePath The path of the file.
     * @author Cui Yuxin
     */
    public static String getFileName(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.getName();
        } else {
            // TODO handle exception
            throw new IOException("File not found or ?.");
        }
    }

    /**
     * Save an image.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @param img The image.
     * @param imgPath The path of the image.
     * @author Cui Yuxin
     */
    public static void saveImg(Bitmap img, String imgPath) throws IOException {
        File file = new File(imgPath);
        String imgDir = file.getParent();
        if (imgDir != null) {
            File dir = new File(imgDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // lossless compression quality 100, resulting in a smaller file.
            // TODO: optimize for small files or high speed.
            img.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outStream);
        } else {
            // lossless compression quality 100
            img.compress(Bitmap.CompressFormat.WEBP, 100, outStream);
        }
        outStream.flush();
    }

    /**
     * Save bytes array.
     * @param bytes The bytes array.
     * @param filePath The path of the file.
     * @author //TODO YOUR_NAME
     */
    public static void saveByteArray(byte[][][] bytes, String filePath) throws IOException {
        // TODO YOUR CODE HERE
    }


    /*// 请求权限
        ActivityCompat.requestPermissions(, new String[]{
        Manifest.permission.READ_PHONE_STATE
    }, 1);
    // 检查权限是否已经授予
    int PermissionState = ContextCompat.checkSelfPermission(appContext, Manifest.permission.READ_PHONE_STATE);
        if(PermissionState == PackageManager.PERMISSION_GRANTED){
        Toast.makeText(this, "已授权！", Toast.LENGTH_LONG).show();
    }else if(PermissionState == PackageManager.PERMISSION_DENIED){
        Toast.makeText(this, "未授权！", Toast.LENGTH_LONG).show();
    }*/
}
