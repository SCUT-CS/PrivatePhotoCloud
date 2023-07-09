package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Decrypt a image.
 * @author Cui Yuxin, Zhao Bowen
 */
public class HalfDecrypt implements Callable {

    private Bitmap img;
    private Bitmap img1;
    private Bitmap img2;
    private final String imgFilePath1;
    private final String imgFilePath2;
    private int width;
    private int height;
    private final Context context;
    private final Handler handler;
    private String imgName;

    /**
     * Constructor.
     * @param imgFilePath1 The path of the first image.
     * @param imgFilePath2 The path of the second image.
     * @param context The context.
     * @author Cui Yuxin
     */
    public HalfDecrypt(String imgFilePath1, String imgFilePath2, Context context) {
        this(imgFilePath1, imgFilePath2, context, null);
    }

    /**
     * Constructor.
     * @param imgFilePath1 The path of the first image.
     * @param imgFilePath2 The path of the second image.
     * @param context The context.
     * @param handler The handler.
     * @author Cui Yuxin
     */
    public HalfDecrypt(String imgFilePath1, String imgFilePath2, Context context, Handler handler) {
        this.imgFilePath1 = imgFilePath1;
        this.imgFilePath2 = imgFilePath2;
        this.context = context;
        this.handler = handler;
    }

    /**
     * Open images.
     * @author Cui Yuxin
     */
    private void openFile() throws Exception {
        img1 = Utils.openImg(imgFilePath1);
        img2 = Utils.openImg(imgFilePath2);
    }

    /**
     * Initialize the images.
     * @author Cui Yuxin
     */
    private void initialize() throws Exception {
        width = img1.getWidth();
        height = img1.getHeight();
        img = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888,
                img1.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
        imgName = Utils.getFileName(imgFilePath1).substring(0, imgName.lastIndexOf(".ori"));
    }

    /**
     * Decrypt a image in some new threads.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void decrypt() {
        /*int threadNum = height / 1000;
        if (threadNum == 0) {
            threadNum = 1;
        }*/
        int threadNum = 2;
        Log.d("Decrypt", "threadNum: " + threadNum);
        Thread[] threads = new HalfDecrypt.DecryptThread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new HalfDecrypt.DecryptThread(i, threadNum);
            threads[i].start();
        }
        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save images.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @author Cui Yuxin
     */
    private void saveFile() throws Exception {
        String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "PPPS-Download"
                + File.separator + imgName;
        Utils.saveJpgImg(img, imgPath);
    }

    /**
     * Run decryption and return results.
     * @author Cui Yuxin
     */
    @Override
    public Bitmap call() throws Exception {
        openFile();
        initialize();
        decrypt();
        saveFile();
        if (handler != null) {
            handler.sendEmptyMessage(Utils.DECRYPT_SUCCESS);
        }
        return img;
    }

    /**
     * Decrypt a part of the image.
     * @author Cui Yuxin, Zhao Bowen
     */
    private class DecryptThread extends Thread {

        private final int rowStart;
        private final int rowEnd;
        private final int colStart;
        private final int colEnd;

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
            int[] encryptedPixels1 = new int[(rowEnd - rowStart) * (colEnd - colStart)];
            int[] encryptedPixels2 = new int[(rowEnd - rowStart) * (colEnd - colStart)];
            int[] originalPixels = new int[(rowEnd - rowStart) * (colEnd - colStart)];


            img1.getPixels(encryptedPixels1, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
            img2.getPixels(encryptedPixels2, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
            if (img1.hasAlpha()) {
                Log.d("Decrypt", "图片有透明度通道");
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int index = (row - rowStart) * (colEnd - colStart) + (col - colStart);
                        int pixel1 = encryptedPixels1[index];
                        int pixel2 = encryptedPixels2[index];
                        int alpha = ((pixel1 >>> 24) + (pixel2 >>> 24) ) & 0xff;
                        int red = (((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF) ) & 0xff;
                        int green = (((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF) ) & 0xff;
                        int blue = ((pixel1 & 0xFF) + (pixel2 & 0xFF)) & 0xff;
                        int pixel = Color.argb(alpha, red, green, blue);
                        originalPixels[index] = pixel;
                    }
                }
            } else {
                Log.d("Decrypt", "图片没有透明度通道");
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int index = (row - rowStart) * (colEnd - colStart) + (col - colStart);
                        int pixel1 = encryptedPixels1[index];
                        int pixel2 = encryptedPixels2[index];
                        int red = (((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF)) & 0xff;
                        int green = (((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF)) & 0xff;
                        int blue = ((pixel1 & 0xFF) + (pixel2 & 0xFF)) & 0xff;
                        int pixel = Color.rgb(red,green,blue);
                        originalPixels[index] = pixel;
                    }
                }
            }
            img.setPixels(originalPixels, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
        }
    }
}
