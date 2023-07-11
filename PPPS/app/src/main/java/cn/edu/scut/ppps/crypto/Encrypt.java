package cn.edu.scut.ppps.crypto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;

import cn.edu.scut.ppps.Utils;

/**
 * Encrypt a image.
 * @author Cui Yuxin, Zhao Bowen
 */
public class Encrypt implements Callable {

    private final String filePath;
    private String fileName;
    private final Context context;
    private Bitmap img;
    private Bitmap img1;
    private Bitmap img2;
    private Bitmap img3;
    private int[] encryptedPixels3;
    private final Random rnd = new Random();
    private int width;
    private int height;
    //private byte[][][] overflow;
    private final Handler handler;

    /**
     * Constructor.
     * @param filePath Path of the image file to be encrypted.
     * @param context Context of the application.
     * @param handler Handler of the activity.
     * @author Cui Yuxin
     */
    public Encrypt(String filePath, Context context, Handler handler) {
        this.filePath = filePath;
        this.context = context;
        this.handler = handler;
    }

    /**
     * Constructor.
     * @param filePath Path of the image file to be encrypted.
     * @param context Context of the application.
     * @author Cui Yuxin
     */
    public Encrypt(String filePath, Context context) {
        this(filePath, context, null);
    }

    /**
     * Open the image.
     * @author Cui Yuxin
     */
    private void openFile() throws IOException {
        img = Utils.openImg(filePath);
        fileName = Utils.getFileName(filePath);
    }

    /**
     * Encrypt a image in some new threads.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void encrypt() {
        width = img.getWidth();
        height = img.getHeight();
        //double scale = Math.min(400.0 / height, 400.0 / width);
        double scale = 0.5;
        int scaledHeight = (int) (height * scale);
        int scaledWidth = (int) (width * scale);
        img3 = Bitmap.createScaledBitmap(img, scaledWidth, scaledHeight, false);
        encryptedPixels3 = new int[scaledWidth * scaledHeight];
        img3.getPixels(encryptedPixels3, 0, scaledWidth, 0, 0, scaledWidth, scaledHeight);
        img1 = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888,
                img.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
        img2 = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888,
                img.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
        if (img.hasAlpha()) {
            Log.d("Encrypt", "hasAlpha");
        } else {
            Log.d("Encrypt", "noAlpha");
        }
        /*int threadNum = height / 1000;
        if (threadNum == 0) {
            threadNum = 1;
        }*/
        int threadNum = 2;
        Log.d("Encrypt", "threadNum: " + threadNum);
        Thread[] threads = new EncryptThread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new EncryptThread(i, threadNum);
            threads[i].start();
        }
        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("Encrypt", "算法执行结束。");
    }

    /**
     * Save images.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @author Cui Yuxin
     */
    private void saveFile() throws Exception {
        String cachePath = context.getCacheDir().getAbsolutePath() + File.separator;
        String savePath1 = cachePath + "Disk1" + File.separator + fileName + ".ori";
        String savePath2 = cachePath + "Disk2" + File.separator + fileName +  ".ori";
        String savePath3 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "PPPS-Thumbnail"
                + File.separator + fileName + ".ori";
        Utils.saveImg(img1, savePath1);
        Utils.saveImg(img2, savePath2);
        Utils.saveImg(img3, savePath3);
    }

    /**
     * Run encryption and return results.
     * @author Cui Yuxin
     */
    @Override
    public Bitmap[] call() throws Exception {
        openFile();
        encrypt();
        saveFile();
        if (handler != null) {
            handler.sendEmptyMessage(Utils.ENCRYPT_SUCCESS);
        }
        return new Bitmap[]{img1, img2};
    }

    /**
     * Encrypt a part of the image.
     * @author Cui Yuxin, Zhao Bowen
     */
    private class EncryptThread extends Thread {

        private final int rowStart;
        private final int rowEnd;
        private final int colStart;
        private final int colEnd;
        private final int scaledWidth;

        /**
         * Constructor.
         * @param id The thread id.
         * @param threadNum The number of threads.
         * @author Cui Yuxin
         */
        EncryptThread(int id, int threadNum) {
            colStart = 0;
            colEnd = width;
            scaledWidth = img3.getWidth();
            if (id == threadNum - 1) {
                rowStart = (height / threadNum) * id;
                rowEnd = height;
            } else {
                rowStart = (height / threadNum) * id;
                rowEnd = (height / threadNum) * (id + 1);
            }
        }

        /**
         * Encrypt a part of the image.
         * @author Cui Yuxin, Zhao Bowen
         */
        @Override
        public void run() {
            int[] encryptedPixels1 = new int[(rowEnd - rowStart) * (colEnd - colStart)];
            int[] encryptedPixels2 = new int[(rowEnd - rowStart) * (colEnd - colStart)];
            int[] originalPixels = new int[(rowEnd - rowStart) * (colEnd - colStart)];
            double scaledHeight = img3.getHeight();
            double scale = scaledHeight / height;
            img.getPixels(originalPixels, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
            if (img.hasAlpha()) {
                int[] argb = new int[4];
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int pixel = originalPixels[(row - rowStart) * (colEnd - colStart) + (col - colStart)];
                        argb[0] = pixel >>> 24;
                        argb[1] = (pixel >> 16) & 0xFF;
                        argb[2] = (pixel >> 8) & 0xFF;
                        argb[3] = pixel & 0xFF;
                        int a1 = rnd.nextInt(256);
                        int r1 = rnd.nextInt(256);
                        int g1 = rnd.nextInt(256);
                        int b1 = rnd.nextInt(256);
                        pixel = b1 | g1 << 8 | r1 << 16 | a1 << 24;
                        encryptedPixels1[(row - rowStart) * (colEnd - colStart) + (col - colStart)] = pixel;
                        int rowIndex = (int) (row * scale);
                        int colIndex = (int) (col * scale);
                        int pixel3 = encryptedPixels3[rowIndex * scaledWidth + colIndex];
                        int a3 = pixel3 >>> 24;
                        int r3 = (pixel3 >> 16) & 0xFF;
                        int g3 = (pixel3 >> 8) & 0xFF;
                        int b3 = pixel3 & 0xFF;
                        int a2 = (argb[0] - a1 - a3) & 0xff;
                        int r2 = (argb[1] - r1 - r3) & 0xff;
                        int g2 = (argb[2] - g1 - g3) & 0xff;
                        int b2 = (argb[3] - b1 - b3) & 0xff;
                        pixel = b2 | g2 << 8 | r2 << 16 | a2 << 24;
                        encryptedPixels2[(row - rowStart) * (colEnd - colStart) + (col - colStart)] = pixel;
                    }
                }
            } else {
                int[] rgb = new int[3];
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int pixel = originalPixels[(row - rowStart) * (colEnd - colStart) + (col - colStart)];
                        rgb[0] = (pixel >> 16) & 0xFF;
                        rgb[1] = (pixel >> 8) & 0xFF;
                        rgb[2] = pixel & 0xFF;
                        int r1 = rnd.nextInt(256); // TODO 预生成
                        int g1 = rnd.nextInt(256);
                        int b1 = rnd.nextInt(256);
                        pixel = b1 | g1 << 8 | r1 << 16 | 0xff000000;
                        encryptedPixels1[(row - rowStart) * (colEnd - colStart) + (col - colStart)] = pixel;
                        int rowIndex = (int) (row * scale);
                        int colIndex = (int) (col * scale);
                        int pixel3 = encryptedPixels3[rowIndex * scaledWidth + colIndex];
                        int r3 = (pixel3 >> 16) & 0xFF;
                        int g3 = (pixel3 >> 8) & 0xFF;
                        int b3 = pixel3 & 0xFF;

//                        // 在Key上添加图片/水印，未授权解密显示水印/文字 水印文字填充随机值
//                        if (row > 50 && row < 100 && col > 50 && col < 100) {
//                            r3 = rnd.nextInt(256);
//                            g3 = rnd.nextInt(256);
//                            b3 = rnd.nextInt(256);
//                        }

                        int r2 = (rgb[0] - r1 - r3) & 0xff;
                        int g2 = (rgb[1] - g1 - g3) & 0xff;
                        int b2 = (rgb[2] - b1 - b3) & 0xff;
                        pixel = b2 | g2 << 8 | r2 << 16 | 0xff000000;
                        encryptedPixels2[(row - rowStart) * (colEnd - colStart) + (col - colStart)] = pixel;
                    }
                }
            }
            img1.setPixels(encryptedPixels1, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
            img2.setPixels(encryptedPixels2, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
            Log.d("EncryptThread", "Thread " + getId() + " finished");
        }
    }
}

