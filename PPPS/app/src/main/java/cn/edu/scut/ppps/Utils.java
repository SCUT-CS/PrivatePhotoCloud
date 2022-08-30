package cn.edu.scut.ppps;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Build;

import androidx.heifwriter.HeifWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.*;

/**
 * Utils class.
 * @author Cui Yuxin, Feng Yucheng
 */
public class Utils {

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
            // TODO handle exception
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
     * @author Cui Yuxin
     */
    public static void saveImg(Bitmap img, String imgPath, byte[] exifData) throws Exception {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            File file = new File(imgPath + ".HEIC");
            String imgDir = file.getParent();
            if (imgDir != null) {
                File dir = new File(imgDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            HeifWriter.Builder heifBuilder = new HeifWriter.Builder(imgPath + ".HEIC", img.getWidth(), img.getHeight(),  HeifWriter.INPUT_MODE_BITMAP);
            HeifWriter heifWriter = heifBuilder.build();
            heifWriter.start();
            heifWriter.addBitmap(img);
            if (exifData != null) {
                heifWriter.addExifData(0, exifData,0, exifData.length);
            }
            heifWriter.stop(0);
            heifWriter.close();
        } else {
            File file = new File(imgPath + ".webp");
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
        Utils.saveImg(img, imgPath, null);
    }


    /**
     * Save bytes array.
     * @param bytesArray The bytes array.
     * @param filePath The path of the overflow array file.
     * @author Feng Yucheng
     */
    public static void saveBytesArray(byte[][][] bytesArray, String filePath) throws IOException {
        File file = new File(filePath);
        String OverflowDir = file.getParent();
        if (OverflowDir != null) {
            File dir = new File(OverflowDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(new Overflow(bytesArray));
    }

    /**
     * Load bytes array and return.
     * @param filePath The path of the overflow array file.
     * @author Feng Yucheng
     */
    public static byte[][][] loadBytesArray(String filePath) throws Exception {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        return ((Overflow) in.readObject()).getBytesArray();
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
                        result[i][((j << 3) + index) / mappingSize] += 255;
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
    public static class Overflow implements Serializable {

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
