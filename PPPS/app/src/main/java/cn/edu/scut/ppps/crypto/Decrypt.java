package cn.edu.scut.ppps.crypto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.concurrent.Callable;

import cn.edu.scut.ppps.Utils;

/**
 * Decrypt a image.
 * @author Cui Yuxin, Zhao Bowen
 */
public class Decrypt implements Callable {

    private Bitmap img;
    private Bitmap img1;
    private Bitmap img2;
    private int[] encryptedPixels3;
    private int scaledHeight;
    private int scaledWidth;
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
    public Decrypt(String imgFilePath1, String imgFilePath2, Context context) {
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
    public Decrypt(String imgFilePath1, String imgFilePath2, Context context, Handler handler) {
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
        imgName = Utils.getFileName(imgFilePath1);
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "PPPS-Thumbnail"
                + File.separator + imgName;
        Bitmap img3 = Utils.openImg(filePath);
//            // 高斯模糊
//            img3 = FastBlur.doBlur(img3, 10, true);
        scaledHeight = img3.getHeight();
        scaledWidth = img3.getWidth();
        encryptedPixels3 = new int[scaledWidth * scaledHeight];
        img3.getPixels(encryptedPixels3, 0, scaledWidth, 0, 0, scaledWidth, scaledHeight);
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
        Thread[] threads = new Decrypt.DecryptThread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new Decrypt.DecryptThread(i, threadNum);
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

        int fixOverflow1(int decrypt, int thumbnail) {
            if (Math.abs(decrypt - thumbnail) > 100) {
                if (thumbnail < 127) {
                    return 0;
                }
                return 255;
            }
            return decrypt;
        }


        int fixOverflow2(int decrypt, int thumbnail){
            if (Math.abs(decrypt - thumbnail) > 100) {
                return thumbnail;
            }
            return decrypt;
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
            double dScaledHeight = scaledHeight;
            double scale = dScaledHeight / height;
            img1.getPixels(encryptedPixels1, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
            img2.getPixels(encryptedPixels2, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
            if (img1.hasAlpha()) {
                Log.d("Decrypt", "图片有透明度通道");
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int index = (row - rowStart) * (colEnd - colStart) + (col - colStart);
                        int pixel1 = encryptedPixels1[index];
                        int pixel2 = encryptedPixels2[index];
                        int rowIndex = (int) (row * scale);
                        int colIndex = (int) (col * scale);
                        int pixel3 = encryptedPixels3[rowIndex * scaledWidth + colIndex];
                        int talpha = (pixel3 >>> 24) & 0xff;
                        int tred = (pixel3 >> 16) & 0xff;
                        int tgreen = (pixel3 >> 8) & 0xff;
                        int tblue = pixel1 & 0xff;
                        int alpha = ((pixel1 >>> 24) + (pixel2 >>> 24) + talpha) & 0xff;
                        int red = (((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF) + tred) & 0xff;
                        int green = (((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF) + tgreen) & 0xff;
                        int blue = ((pixel1 & 0xFF) + (pixel2 & 0xFF) + tblue) & 0xff;
                        alpha = fixOverflow1(alpha, talpha);
                        red = fixOverflow1(red, tred);
                        green = fixOverflow1(green, tgreen);
                        blue = fixOverflow1(blue, tblue);
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
                        int rowIndex = (int) (row * scale);
                        int colIndex = (int) (col * scale);
                        int pixel3 = encryptedPixels3[rowIndex * scaledWidth + colIndex];
                        int tred = ((pixel3 >> 16) & 0xFF);
                        int tgreen = ((pixel3 >> 8) & 0xFF);
                        int tblue = (pixel3 & 0xFF);

//                        // 无缩略图解密
//                        tred = 0;
//                        tgreen = 0;
//                        tblue = 0;

                        int red = (((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF) + tred) & 0xff;
                        int green = (((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF) + tgreen) & 0xff;
                        int blue = ((pixel1 & 0xFF) + (pixel2 & 0xFF) + tblue) & 0xff;
                        red = fixOverflow1(red, tred);
                        green = fixOverflow1(green, tgreen);
                        blue = fixOverflow1(blue, tblue);
                        int pixel = Color.rgb(red,green,blue);
                        originalPixels[index] = pixel;
                    }
                }
            }
            img.setPixels(originalPixels, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
        }
    }
}
