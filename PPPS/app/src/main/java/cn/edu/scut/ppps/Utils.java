package cn.edu.scut.ppps;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.heifwriter.HeifWriter;

import org.junit.Ignore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utils class.
 * @author Cui Yuxin, Feng Yucheng
 */
public class Utils {

    // Cloud Handler value
    public static final int CLOUD_SUCCESS = 0;
    public static final int CLOUD_FAILURE = -1;
    // MainActivity Handler value
    public static final int START_CLOUD = 1;
    public static final int FINISH_CLOUD = 2;
    public static final int START_ALGORITHM = 3;
    public static final int FINISH_ALGORITHM = 4;
    public static final int ERROR = -2;
    public static final int UI = 8;
    public static final int SUCCESS = 9;
    public static final int SETTINGS_UPDATE = 10;
    // Encrypt Handler value
    public static final int ENCRYPT_SUCCESS = 5;
    // Decrypt Handler value
    public static final int DECRYPT_SUCCESS = 6;
    // Thumbnail Handler value
    public static final int THUMBNAIL_SUCCESS = 7;
    // CameraActivity result value
    public static final int CAMERA_RESULT = 1;
    // PreviewActivity result value
    public static final int PREVIEW_RESULT = 11;
    //Thumbnail Handler value
    public static final int THUMBNAIL_START = 12;

    /**
     * Open an image and return.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @param imgPath The path of the image.
     * @author Cui Yuxin
     */
    public static Bitmap openImg(String imgPath) throws IOException {
        ImageDecoder.Source imgsource = ImageDecoder.createSource(new File(imgPath));
        return ImageDecoder.decodeBitmap(imgsource, (decoder, info, source) -> {
            decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
            decoder.setMutableRequired(true);
        });
    }

    /**
     * Find file`s name and return.
     * @param filePath The path of the file.
     * @author Cui Yuxin
     */
    public static String getFileName(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.getName();
        } else {
            throw new IOException("File not found or ?.");
        }
    }

    /**
     * Save an image.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @param img The image.
     * @param imgPath The path of the image.
     * @param exifData The exif data of the image.Add Exif data for the specified image. The data
     *                 must be a valid Exif data block, starting with "Exif\0\0" followed
     *                 by the TIFF header (See JEITA CP-3451C Section 4.5.2.)
     * @param format The format of the image.
     * @author Cui Yuxin
     */
    public static void saveImg(Bitmap img, String imgPath, byte[] exifData, String format) throws Exception {
        File file = new File(imgPath);
        String imgDir = file.getParent();
        if (imgDir != null) {
            File dir = new File(imgDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && format.equals("heif")) {
            HeifWriter.Builder heifBuilder = new HeifWriter.Builder(imgPath + ".HEIC", img.getWidth(),
                    img.getHeight(),
                    HeifWriter.INPUT_MODE_BITMAP);
            HeifWriter heifWriter = heifBuilder.build();
            heifWriter.start();
            heifWriter.addBitmap(img);
            if (exifData != null) {
                heifWriter.addExifData(0, exifData, 0, exifData.length);
            }
            heifWriter.stop(0);
            heifWriter.close();
        } else */
        if (format.equals("jpeg")) {
            file = new File(imgPath + ".jpg");
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
            img.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
        } else {
            file = new File(imgPath + ".webp");
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // lossless compression quality 100, resulting in a smaller file.
                img.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 0, outStream);
            } else {
                // lossless compression quality 100
                img.compress(Bitmap.CompressFormat.WEBP, 100, outStream);
            }
            outStream.flush();
        }
    }

    /**
     * Save an image.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @param img The image.
     * @param imgPath The path of the image.
     * @author Cui Yuxin
     */
    public static void saveImg(Bitmap img, String imgPath) throws Exception {
        Utils.saveImg(img, imgPath, null, "webp");
    }

    /**
     * Save an image.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @param img The image.
     * @param imgPath The path of the image.
     * @author Cui Yuxin
     */
    public static void saveJpgImg(Bitmap img, String imgPath) throws Exception {
        Utils.saveImg(img, imgPath, null, "jpeg");
    }

    /**
     * Return all files in the directory.
     * @param path The path of the dir.
     * @author Cui YuXin
     */
    public static List<String> getAllFile(String path) {
        File dir = new File(path);
        // 如果文件夹不存在或着不是文件夹，则返回 null
        if (Objects.isNull(dir) || !dir.exists() || dir.isFile()) {
            return null;
        }
        File[] childrenFiles = dir.listFiles();
        if (Objects.isNull(childrenFiles) || childrenFiles.length == 0) {
            return null;
        }
        List<String> files = new ArrayList<>();
        for (File childFile : childrenFiles) {
            // 如果是文件，直接添加到结果集合
            if (childFile.isFile()) {
                files.add(childFile.getName());
            }
        }
        return files;
    }

    /**
     * Convert uri to file path.
     * @param fileUrl The uri of the file.
     * @param context The context of the activity.
     * @author Cui Yuxin
     * @source a href="<a href="http://t.zoukankan.com/androidxiaoyang-p-4968663.html">Source Blog</a>"
     */
    public static String uri2Path(Uri fileUrl, Context context) {
        String fileName = null;
        if (fileUrl != null) {
            if (fileUrl.getScheme().toString().compareTo("content") == 0) { // content://开头的uri
                Cursor cursor = context.getContentResolver().query(fileUrl, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        fileName = cursor.getString(columnIndex); // 取出文件路径
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } finally {
                        cursor.close();
                    }
                }
            } else if (fileUrl.getScheme().compareTo("file") == 0) { // file:///开头的uri
                fileName = fileUrl.getPath();
            }
        }
        return fileName;
    }

    /**
     * 调整亮度
     * @param bm 原图
     * @param val 亮度值 0-255 默认127
     * @author Cui Yuxin
     * @source <a href="https://www.cnblogs.com/java315/archive/2011/11/28/2397404.html">Source Blog</a>
     */
    public static Bitmap modifyBrightness(Bitmap bm, int val) {
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
                Bitmap.Config.ARGB_8888);
        int brightness = val - 127;
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,
                0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(bmp);
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
        canvas.drawBitmap(bm, 0, 0, paint);
        return bmp;
    }

    /**
     * 调整对比度
     * @param bm 原图
     * @param val 对比度值 0-127 默认63
     * @author Cui Yuxin
     * @source <a href="https://www.cnblogs.com/java315/archive/2011/11/28/2397404.html">Source Blog</a>
     */
    public static Bitmap modifyContrast(Bitmap bm, int val) {
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
                Bitmap.Config.ARGB_8888);
        float contrast = (float) ((val + 64) / 128.0);
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0,
                contrast, 0, 0, 0,// 改变对比度
                0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        Canvas canvas = new Canvas(bmp);
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
        canvas.drawBitmap(bm, 0, 0, paint);
        return bmp;
    }

    //        switch (flag) {
//            case FLAG_HUE: // 需要改变色相
//                mHueMatrix.reset();
//                mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1); // 红、绿、蓝三分量按相同的比例,最后一个参数1表示透明度不做变化，此函数详细说明参考
//                // // android
//                // doc
//                break;
//            case FLAG_SATURATION: // 需要改变饱和度
//                // saturation 饱和度值，最小可设为0，此时对应的是灰度图(也就是俗话的“黑白图”)，
//                // 为1表示饱和度不变，设置大于1，就显示过饱和
//                mSaturationMatrix.reset();
//                mSaturationMatrix.setSaturation(mSaturationValue);
//                break;
}
