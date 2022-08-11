package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Encrypt a image.
 * @author Cui Yuxin, Zhao Bowen
 */
public class Encrypt implements Callable {

    private String filePath;
    private String fileName;
    private Context context;
    private Bitmap img;
    private Bitmap img1;
    private Bitmap img2;
    private Random rnd = new Random();
    private int width;
    private int height;

    /**
     * Constructor.
     * @param filePath Path of the image file to be encrypted.
     * @param context Context of the application.
     * @author Cui Yuxin
     */
    public Encrypt(String filePath, Context context) {
        this.filePath = filePath;
        this.context = context;
    }

    /**
     * Open the image.
     * @author Cui Yuxin
     */
    private void openFile() throws IOException {
        Utils.openImg(filePath, img, fileName);
    }

    /**
     * Encrypt a image in some new threads.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void encrypt() {
        width = img.getWidth();
        height = img.getHeight();
        img1 = Bitmap.createBitmap(width, height * 2,
                Bitmap.Config.RGBA_F16,
                img.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
        img2 = Bitmap.createBitmap(width, height * 2,
                Bitmap.Config.RGBA_F16,
                img.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
        // TODO: optimize for the number of threads.
        int threadNum = height / 1000;
        if (threadNum == 0) {
            threadNum = 1;
        }
        Thread[] threads = new EncryptThread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new EncryptThread(i, threadNum);
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
     * Save images.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @author Cui Yuxin
     */
    private void saveFile() throws IOException {
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath1 = cachePath + File.separator + "Disk1" + File.separator + fileName + ".webp";
        String savePath2 = cachePath + File.separator + "Disk2" + File.separator + fileName + ".webp";
        Utils.saveImg(img1, savePath1);
        Utils.saveImg(img2, savePath2);
    }

    /**
     * Run encryption and return results.
     * @author Cui Yuxin
     */
    @Override
    public Bitmap[] call() throws IOException {
        openFile();
        encrypt();
        saveFile();
        return new Bitmap[]{img1, img2};
    }

    /**
     * Encrypt a part of the image.
     * @author Cui Yuxin, Zhao Bowen
     */
    private class EncryptThread extends Thread {

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
        EncryptThread(int id, int threadNum) {
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
         * encrypt a channel of a pixel.
         * @param x the original value.
         * @param r the random value.
         * @param d always be 256.
         * @return the encrypted value.
         * @author Zhao Bowen
         */
        private int encrypt(int x, int r, int d) {
            int c = x - r % d;
            c = c < 0 ? c + d : c;
            return c;
        }

        /**
         * Encrypt a part of the image.
         * @author Cui Yuxin, Zhao Bowen
         */
        @Override
        public void run() {
            if (img.hasAlpha()) {
                int[] argb = new int[4];
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        // TODO optimize the function call
                        int pixel = img.getPixel(row, col);
                        argb[0] = Color.red(pixel);
                        argb[1] = Color.green(pixel);
                        argb[2] = Color.blue(pixel);
                        argb[3] = Color.alpha(pixel);
                        int r1 = rnd.nextInt(256);
                        int g1 = rnd.nextInt(256);
                        int b1 = rnd.nextInt(256);
                        int a1 = rnd.nextInt(256);
                        pixel = Color.argb(a1, r1, g1, b1);
                        img1.setPixel(row, col, pixel);
                        int r2 = encrypt(argb[0], r1, 256);
                        int g2 = encrypt(argb[1], g1, 256);
                        int b2 = encrypt(argb[2], b1, 256);
                        int a2 = encrypt(argb[3], b1, 256);
                        pixel = Color.argb(a2, r2, g2, b2);
                        img2.setPixel(row, col, pixel);
                        // TODO encrypt the overflow information
                        int r3, g3, b3, a3;
                        int r4, g4, b4, a4;
                        if (argb[0] < r1){
                            r3 = rnd.nextInt(256);
                            r4 = 255 - r3;
                        } else {
                            r3 = r4 = 0;
                        }
                        if (argb[1] < g1){
                            g3 = rnd.nextInt(256);
                            g4 = 255 - g3;
                        } else {
                            g3 = g4 = 0;
                        }
                        if (argb[2] < b1){
                            b3 = rnd.nextInt(256);
                            b4 = 255 - b3;
                        } else {
                            b3 = b4 = 0;
                        }
                        if (argb[3] < a1){
                            a3 = rnd.nextInt(256);
                            a4 = 255 - a3;
                        } else {
                            a3 = a4 = 0;
                        }
                        img1.setPixel(row + height, col, Color.argb(a3, r3, g3, b3));
                        img2.setPixel(row + height, col, Color.argb(a4, r4, g4, b4));
                    }
                }
            } else {
                int[] rgb = new int[3];
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        // TODO optimize the function call
                        int pixel = img.getPixel(row, col);
                        rgb[0] = Color.red(pixel);
                        rgb[1] = Color.green(pixel);
                        rgb[2] = Color.blue(pixel);
                        int r1 = rnd.nextInt(256);
                        int g1 = rnd.nextInt(256);
                        int b1 = rnd.nextInt(256);
                        pixel = Color.rgb(r1, g1, b1);
                        img1.setPixel(row, col, pixel);
                        int r2 = encrypt(rgb[0], r1, 256);
                        int g2 = encrypt(rgb[1], g1, 256);
                        int b2 = encrypt(rgb[2], b1, 256);
                        pixel = Color.rgb(r2, g2, b2);
                        img2.setPixel(row, col, pixel);
                        // TODO encrypt the overflow information
                        int r3, g3, b3;
                        int r4, g4, b4;
                        if (rgb[0] < r1){
                            r3 = rnd.nextInt(256);
                            r4 = 255 - r3;
                        } else {
                            r3 = r4 = 0;
                        }
                        if (rgb[1] < g1){
                            g3 = rnd.nextInt(256);
                            g4 = 255 - g3;
                        } else {
                            g3 = g4 = 0;
                        }
                        if (rgb[2] < b1){
                            b3 = rnd.nextInt(256);
                            b4 = 255 - b3;
                        } else {
                            b3 = b4 = 0;
                        }
                        img1.setPixel(row + height, col, Color.rgb(r3, g3, b3));
                        img2.setPixel(row + height, col, Color.rgb(r4, g4, b4));
                    }
                }
            }
        }
    }
}

