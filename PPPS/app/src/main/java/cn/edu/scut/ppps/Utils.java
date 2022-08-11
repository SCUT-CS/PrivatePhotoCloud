package cn.edu.scut.ppps;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Build;

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
     * @param img The image.
     * @param imgName The name of the image.
     * @author Cui Yuxin
     */
    public static void openImg(String imgPath, Bitmap img, String imgName) throws IOException {
        File file = new File(imgPath);
        if (imgName != null) {
            imgName = file.getName();
        }
        ImageDecoder.Source source = ImageDecoder.createSource(file);
        img = ImageDecoder.decodeBitmap(source);
    }

    /**
     * Open an image.
     * @param imgPath The path of the image.
     * @param img The image.
     * @author Cui Yuxin
     */
    public static void openImg(String imgPath, Bitmap img) throws IOException {
        Utils.openImg(imgPath, img, null);
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
}
