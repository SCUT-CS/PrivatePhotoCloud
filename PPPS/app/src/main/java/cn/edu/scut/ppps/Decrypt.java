package cn.edu.scut.ppps;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;

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
    private String imgName;
    private int width;
    private int height;

    /**
     * Constructor.
     * @param imgFilePath1 The path of the first image.
     * @param imgFilePath2 The path of the second image.
     * @author Cui Yuxin
     */
    public Decrypt(String imgFilePath1, String imgFilePath2) {
        this.imgFilePath1 = imgFilePath1;
        this.imgFilePath2 = imgFilePath2;
    }

    /**
     * Open images.
     * @author Cui Yuxin
     */
    private void openFile() throws IOException {
        Utils.openImg(imgFilePath1, img1, imgName);
        Utils.openImg(imgFilePath2, img2);
    }

    /**
     * Decrypt a image in some new threads.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void decrypt() {
        width = img1.getWidth();
        height = img1.getHeight() / 2;
        img = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGBA_F16,
                img1.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
        // TODO: optimize for the number of threads.
        int threadNum = height / 1000;
        if (threadNum == 0) {
            threadNum = 1;
        }
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
     * Run decryption and return results.
     * @author Cui Yuxin
     */
    @Override
    public Bitmap call() throws IOException {
        openFile();
        decrypt();
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
        private int height;
        private int width;

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
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        // TODO optimize the function call
                        int pixel1 = img1.getPixel(row, col);
                        int pixel2 = img2.getPixel(row, col);
                        int pixel = Color.argb((Color.alpha(pixel1) + Color.alpha(pixel2)) % 256,
                                (Color.red(pixel1) + Color.red(pixel2)) % 256,
                                (Color.green(pixel1) + Color.green(pixel2)) % 256,
                                (Color.blue(pixel1) + Color.blue(pixel2)) % 256);
                        img.setPixel(row, col, pixel);
                    }
                }
            } else {
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        // TODO optimize the function call
                        int pixel1 = img1.getPixel(row, col);
                        int pixel2 = img2.getPixel(row, col);
                        int pixel = Color.rgb((Color.red(pixel1) + Color.red(pixel2)) % 256,
                                (Color.green(pixel1) + Color.green(pixel2)) % 256,
                                (Color.blue(pixel1) + Color.blue(pixel2)) % 256);
                        img.setPixel(row, col, pixel);
                    }
                }
            }
        }
    }
}
