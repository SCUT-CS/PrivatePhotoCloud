package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Decrypt a image.
 * @author Cui Yuxin, Zhao Bowen
 */
public class Decrypt implements Callable {

    private Bitmap img;
    private Bitmap img1;
    private Bitmap img2;
    private String imgFilePath1;
    private String imgFilePath2;
    private int width;
    private int height;
    private boolean isThumbnail;
    private byte[][][] overflow = null;
    private Context context;

    /**
     * Constructor.
     * @param imgFilePath1 The path of the first image.
     * @param imgFilePath2 The path of the second image.
     * @param isThumbnail Whether the image is a thumbnail.
     * @param context The context.
     * @author Cui Yuxin
     */
    public Decrypt(String imgFilePath1, String imgFilePath2, Context context, boolean isThumbnail) {
        this.imgFilePath1 = imgFilePath1;
        this.imgFilePath2 = imgFilePath2;
        this.isThumbnail = isThumbnail;
        this.context = context;
    }

    /**
     * Constructor.
     * @param imgFilePath1 The path of the first image.
     * @param imgFilePath2 The path of the second image.
     * @param context The context.
     * @author Cui Yuxin
     */
    public Decrypt(String imgFilePath1, String imgFilePath2, Context context) {
        this.imgFilePath1 = imgFilePath1;
        this.imgFilePath2 = imgFilePath2;
        this.isThumbnail = false;
        this.context = context;
    }

    /**
     * Open images.
     * @author Cui Yuxin
     */
    private void openFile() throws Exception {
        img1 = Utils.openImg(imgFilePath1);
        img2 = Utils.openImg(imgFilePath2);
        if (isThumbnail) {
            String imgName = Utils.getFileName(imgFilePath1);
            String originalImgName = imgName.substring(0, imgName.lastIndexOf(".ori"));
            String filePath = context.getDataDir().getAbsolutePath() + File.separator + "overflow" + File.separator;
            overflow = Utils.loadBytesArray(filePath + originalImgName);
        }
    }

    /**
     * Initialize the images.
     * @author Cui Yuxin
     */
    private void initialize() {
        width = img1.getWidth();
        height = img1.getHeight();
        img = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGBA_F16,
                img1.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
    }

    /**
     * Decrypt a image in some new threads.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void decrypt() {
        // TODO: optimize for the number of threads.
        int threadNum = height / 1000;
        if (threadNum == 0) {
            threadNum = 1;
        }
        Log.d("Decrypt", "threadNum: " + threadNum);
        Thread[] threads = new Decrypt.DecryptThread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new Decrypt.DecryptThread(i, threadNum);
            threads[i].start();
        }
        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                // TODO handle exception.
                e.printStackTrace();
            }
        }
    }

    /**
     * Decrypt a image`s thumbnail.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void decryptThumbnail() throws IOException {
        int[][][] overflow = Utils.collapse(this.overflow, height, width);
        assert overflow != null;
        if (img.hasAlpha()) {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int pixel1 = img1.getPixel(row, col);
                    int pixel2 = img2.getPixel(row, col);
                    int alpha = (pixel1 >>> 24 + pixel2 >>> 24 - overflow[3][row][col]) & 0xff;
                    int red = ((pixel1 >> 16) & 0xFF + (pixel2 >> 16) & 0xFF - overflow[0][row][col]) & 0xff;
                    int green =((pixel1 >> 8) & 0xFF + (pixel2 >> 8) & 0xFF - overflow[1][row][col]) & 0xff;
                    int blue = (pixel1 & 0xFF + pixel2 & 0xFF - overflow[2][row][col]) & 0xff;
                    img.setPixel(row, col, blue | green << 8 | red << 16 | alpha << 24);
                }
            }
        } else {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int pixel1 = img1.getPixel(row, col);
                    int pixel2 = img2.getPixel(row, col);
                    int red = ((pixel1 >> 16) & 0xFF + (pixel2 >> 16) & 0xFF - overflow[0][row][col]) & 0xff;
                    int green =((pixel1 >> 8) & 0xFF + (pixel2 >> 8) & 0xFF - overflow[1][row][col]) & 0xff;
                    int blue = (pixel1 & 0xFF + pixel2 & 0xFF - overflow[2][row][col]) & 0xff;
                    img.setPixel(row, col, blue | green << 8 | red << 16);
                }
            }
        }
    }

    /**
     * Run decryption and return results.
     * @author Cui Yuxin
     */
    @Override
    public Bitmap call() throws Exception {
        openFile();
        initialize();
        if (isThumbnail) {
            decryptThumbnail();
        } else {
            decrypt();
        }
        return img;
    }

    /**
     * Decrypt a part of the image.
     * @author Cui Yuxin, Zhao Bowen
     */
    private class DecryptThread extends Thread {

        private int rowStart;
        private int rowEnd;
        private int colStart;
        private int colEnd;

        /**
         * Constructor.
         * @param id The thread id.
         * @param threadNum The number of threads.
         * @author Cui Yuxin
         */
        DecryptThread(int id, int threadNum) {
            colStart = 0;
            colEnd = width;
            if (id == threadNum - 1) {
                rowStart = (height / threadNum) * id;
                rowEnd = height;
            } else {
                rowStart = (height / threadNum) * id;
                rowEnd = (height / threadNum) * (id + 1);
            }
        }

        /**
         * Decrypt a part of the image.
         * @author Cui Yuxin, Zhao Bowen
         */
        @Override
        public void run() {
            if (img.hasAlpha()) {
                Log.d("Decrypt", "图片有透明度通道");
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int pixel1 = img1.getPixel(row, col);
                        int pixel2 = img2.getPixel(row, col);
                        int pixel = Color.argb(((pixel1 >>> 24) + (pixel2 >>> 24)) & 0xff,
                                (((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF)) & 0xff,
                                (((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF)) & 0xff,
                                ((pixel1 & 0xFF) + (pixel2 & 0xFF)) & 0xff);
                        img.setPixel(row, col, pixel);
                    }
                }
            } else {
                Log.d("Decrypt", "图片没有透明度通道");
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int pixel1 = img1.getPixel(row, col);
                        int pixel2 = img2.getPixel(row, col);
                        int pixel = Color.rgb((((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF)) & 0xff,
                                (((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF)) & 0xff,
                                ((pixel1 & 0xFF) + (pixel2 & 0xFF)) & 0xff);
                        img.setPixel(row, col, pixel);
                    }
                }
            }
        }
    }
}
