package cn.edu.scut.ppps;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Build;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Utils class.
 * @author Cui Yuxin, Feng Yucheng
 */
public class Utils {

    /**
     * Open an image and return.
     * @param imgPath The path of the image.
     * @author Cui Yuxin
     */
    public static Bitmap openImg(String imgPath) throws IOException {
        ImageDecoder.Source source = ImageDecoder.createSource(new File(imgPath));
        return ImageDecoder.decodeBitmap(source);
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
     * @param bytesArray The bytes array.
     * @param filePath The path of the overflow array file.
     * @author //TODO YOUR_NAME
     */
    public static void saveBytesArray(byte[][][] bytesArray, String filePath) throws IOException {
        // TODO YOUR CODE HERE
    }

    /**
     * Load bytes array and return.
     * @param filePath The path of the overflow array file.
     * @author //TODO YOUR_NAME
     */
    public static byte[][][] loadBytesArray(String filePath) throws IOException {
        // TODO YOUR CODE HERE
        return null;
    }

    /**
     * Compress and overflow matrix into thumbnail size and return results.
     * @param bytesArray The bytes array.
     * @param height The height of the thumbnail.
     * @param width The width of the thumbnail.
     * @author Zuo Xiaole, Cui Yuxin
     */
    public static int[][][] collapse(byte[][][] bytesArray, int height, int width) {
        int originalHeight = bytesArray[0].length;
        int originalWidth = bytesArray[0][0].length;
        int originalChannel = bytesArray.length;
        double averageRatio = ((double) (height * width)) / (originalHeight * originalWidth);
        int[][][] collapsed = new int[4][][]; // 4 channels (R, G, B, A)
        if (originalChannel == 3) {
            for (int i = 0; i < 3; i++) {
                collapsed[i] = collapseHeight(collapseWidth(bytesArray[i], width), height, averageRatio);
            }
        } else if (originalChannel == 4) {
            for (int i = 0; i < 4; i++) {
                collapsed[i] = collapseHeight(collapseWidth(bytesArray[i], width), height, averageRatio);
            }
        }
        return collapsed;
    }

    /**
     * A helping method to collapse the width of the matrix and return results.
     * @param bytesArray The bytes array.
     * @param width The width of the thumbnail.
     * @author Zuo Xiaole, Cui Yuxin
     */
    private static int[][] collapseWidth(byte[][] bytesArray, int width) {
        int originalWidth = bytesArray[0].length;
        int originalHeight = bytesArray.length;
        int mappingSize = (int) Math.ceil(originalWidth * 8.0 / width);
        int[][] result = new int[originalHeight][width];
        for (int i = 0; i < originalHeight; i++) {
            for (int j = 0; j < originalWidth; j++) {
                byte currentByte = bytesArray[i][j];
                for (int index = 0; index < 8; index++) {
                    if ((currentByte & (1 << index)) != 0) {
                        result[i][((j << 3) + index) / mappingSize] += 256;
                    }
                }
            }
        }
        return result;
    }

    /**
     * A helping method to collapse the height of the matrix and return results.
     * @param bytesArray The bytes array.
     * @param height The height of the thumbnail.
     * @param ratio The compress ratio of the thumbnail.
     * @author Zuo Xiaole, Cui Yuxin
     */
    private static int[][] collapseHeight(int[][] bytesArray, int height, double ratio) {
        int originalWidth = bytesArray[0].length;
        int originalHeight = bytesArray.length;
        int mappingSize = (int) Math.ceil(originalHeight * 1.0 / height);
        int[][] result = new int[height][originalWidth];
        for (int i = 0; i < originalHeight; i++) {
            for (int j = 0; j < originalWidth; j++) {
                result[i / mappingSize][j] += (bytesArray[i][j] * ratio);
            }
        }
        return result;
    }

    /**
     * Wrap the bytes array into a serializable object.
     * @author Cui Yuxin
     */
    public class Overflow implements Serializable {

        private byte[][][] bytesArray;

        /**
         * Constructor.
         * @param bytesArray The bytes array.
         * @author Cui Yuxin
         */
        public Overflow(byte[][][] bytesArray) {
            this.bytesArray = bytesArray;
        }

        /**
         * Get the bytes array and return.
         * @author Cui Yuxin
         */
        public byte[][][] getBytesArray() {
            return bytesArray;
        }
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
