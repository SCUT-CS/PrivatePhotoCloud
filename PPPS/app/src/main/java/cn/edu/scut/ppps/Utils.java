package cn.edu.scut.ppps;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && format.equals("heif")) {
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
        } else if (format.equals("jpeg")) {
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
     * Save bytes array.
     * @param bytesArray The bytes array.
     * @param filePath The path of the overflow array file.
     * @author Feng Yucheng
     */
    public static void saveBytesArray(byte[][][] bytesArray, String filePath, int width) throws IOException {
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
        out.writeObject(new Overflow(bytesArray, width));
    }

    /**
     * Compress and overflow matrix into thumbnail size and return results.
     * @param filePath The path of the overflow file.
     * @param height The height of the thumbnail.
     * @param width The width of the thumbnail.
     * @author Feng Yucheng, Zuo Xiaole, Cui Yuxin
     */
    public static int[][][] collapse(String filePath, int height, int width) throws Exception {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Overflow overflow = (Overflow) in.readObject();
        byte[][][] bytesArray = overflow.getBytesArray();
        int originalHeight = bytesArray[0].length;
        int originalChannel = bytesArray.length;
        int originalWidth = overflow.getWidth();
        double averageRatio = ((double) (height * width)) / (originalHeight * originalWidth);
        int[][][] collapsed = null;
        if (originalChannel == 3) {
            collapsed = new int[3][][];
            for (int i = 0; i < 3; i++) {
                collapsed[i] = collapseHeight(collapseWidth(bytesArray[i], width, originalWidth), height, averageRatio);
            }
        } else if (originalChannel == 4) {
            collapsed = new int[4][][]; // 4 channels (R, G, B, A)
            for (int i = 0; i < 4; i++) {
                collapsed[i] = collapseHeight(collapseWidth(bytesArray[i], width, originalWidth), height, averageRatio);
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
    private static int[][] collapseWidth(byte[][] bytesArray, int width, int originalWidth) {
        int arrayWidth = bytesArray[0].length;
        int originalHeight = bytesArray.length;
        int mappingSize = (int) Math.ceil(originalWidth * 1.0 / width);
        int[][] result = new int[originalHeight][width];
        for (int i = 0; i < originalHeight; i++) {
            for (int j = 0; j < arrayWidth; j++) {
                byte currentByte = bytesArray[i][j];
                for (int index = 0; index < 8; index++) {
                    if ((currentByte & (1 << index)) != 0) {
                        // 应该是255
                        result[i][((j << 3) + index) / mappingSize] += 250;
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
        double[] temp = null;
        for (int i = 0; i < originalHeight; i++) {
            if (i % mappingSize == 0) {
                temp = new double[originalWidth];
            }
            for (int j = 0; j < originalWidth; j++) {
                if (i % mappingSize == mappingSize - 1 || i == originalHeight - 1) {
                    result[i / mappingSize][j] = (int) (temp[j] + bytesArray[i][j] * ratio);
                } else {
                    temp[j] += bytesArray[i][j] * ratio;
                }
            }
        }
        return result;
    }

    /**
     * Generate a thumbnail from the image.
     * @param origin The original image.
     * @param newfile The path of the thumbnail.
     * @param size The compress size.
     * @author Cui Yuxin, Zhao Bowen
     */
    @Ignore("只适用于450*450的图片")
    public static void genThumbnail(Bitmap origin, String newfile, int size) throws Exception {
        Bitmap bitmap = origin;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap thumbnail = Bitmap.createBitmap(width / size, height / size, Bitmap.Config.ARGB_8888);
        for (int y = 0; y < height; y += size) {
            for (int x = 0; x < width; x += size) {
                int sumr = 0, sumg = 0, sumb = 0;
                for (int j = y; j < y + size; j++) {
                    for (int i = x; i < x + size; i++) {
                        int pixel = bitmap.getPixel(i, j);
                        sumr += (pixel & 0xff0000) >> 16; //r
                        sumg += (pixel & 0xff00) >> 8; //g
                        sumb += (pixel & 0xff); //b
                    }
                }
                sumr /= (size * size);
                sumg /= (size * size);
                sumb /= (size * size);
                int pixel1 = Color.rgb(sumr, sumg, sumb);
                thumbnail.setPixel(x / size, y / size, pixel1);
            }
        }
        Utils.saveImg(thumbnail, newfile);
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
     * @source http://t.zoukankan.com/androidxiaoyang-p-4968663.html
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
     * Wrap the bytes array into a serializable object.
     * @author Cui Yuxin
     */
    public static class Overflow implements Serializable {

        private byte[][][] bytesArray;
        private int width;

        /**
         * Constructor.
         * @param bytesArray The bytes array.
         * @param width The width of the original image.
         * @author Cui Yuxin
         */
        public Overflow(byte[][][] bytesArray, int width) {
            this.bytesArray = bytesArray;
            this.width = width;
        }

        /**
         * Get the bytes array and return.
         * @author Cui Yuxin
         */
        public byte[][][] getBytesArray() {
            return bytesArray;
        }

        /**
         * Get the width and return.
         * @author Cui Yuxin
         */
        public int getWidth() {
            return width;
        }
    }
}
