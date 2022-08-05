package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.ImageDecoder;
import android.os.Build;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;

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
    private final Random rnd = new Random();

    /**
     * Constructor.
     * @param file Path of the image file to be encrypted.
     * @param context Android context from caller.
     * @author Cui Yuxin
     */
    public Encrypt(String file, Context context) {
        this.filePath = file;
        this.context = context;
    }

    /**
     * Open the image and return.
     * @author Cui Yuxin
     */
    private void openFile() throws IOException {
        File file = new File(filePath);
        fileName = file.getName();
        ImageDecoder.Source source = ImageDecoder.createSource(file);
        img = ImageDecoder.decodeBitmap(source);
    }

    /**
     * Encrypt a image in some new threads.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void encrypt() {
        // TODO: optimize for the number of threads.
        int threadNum = img.getHeight() / 1000;
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
    }

    /**
     * Save images.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @author Cui Yuxin
     */
    private void saveFile() throws IOException {
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath1 = cachePath + File.separator + "Disk1";
        String savePath2 = cachePath + File.separator + "Disk2";
        File saveDir1 = new File(savePath1);
        File saveDir2 = new File(savePath2);
        if (!saveDir1.exists()) {
            saveDir1.mkdirs();
        }
        if (!saveDir2.exists()) {
            saveDir2.mkdirs();
        }
        File saveFile1 = new File(savePath1 + File.separator + fileName + ".webp");
        File saveFile2 = new File(savePath2 + File.separator + fileName + ".webp");
        BufferedOutputStream outStream1 = new BufferedOutputStream(new FileOutputStream(saveFile1));
        BufferedOutputStream outStream2 = new BufferedOutputStream(new FileOutputStream(saveFile2));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // lossless compression quality 100, resulting in a smaller file.
            // TODO: optimize for small files or high speed.
            img1.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outStream1);
            img2.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outStream2);
        } else {
            // lossless compression quality 100
            img1.compress(Bitmap.CompressFormat.WEBP, 100, outStream1);
            img2.compress(Bitmap.CompressFormat.WEBP, 100, outStream2);
        }
        outStream1.flush();
        outStream2.flush();
    }

    /**
     * Run encryption and return results.
     * @author Cui Yuxin
     */
    @Override
    public Bitmap[] call() {
        try {
            openFile();
            encrypt();
            saveFile();
            return new Bitmap[]{img1, img2};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
         * Constructor.
         * @param id The thread id.
         * @param threadNum The number of threads.
         * @author Cui Yuxin, Zhao Bowen
         */
        EncryptThread(int id, int threadNum) {
            int width = img.getWidth();
            int height = img.getHeight();
            colStart = 0;
            colEnd = width;
            if (id == threadNum - 1) {
                rowStart = (height / threadNum) * id;
                rowEnd = height;
            } else {
                rowStart = (height / threadNum) * id;
                rowEnd = (height / threadNum) * (id + 1);
            }
            img1 = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGBA_F16,
                    img.hasAlpha(),
                    ColorSpace.get(ColorSpace.Named.SRGB));
            img2 = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGBA_F16,
                    img.hasAlpha(),
                    ColorSpace.get(ColorSpace.Named.SRGB));
        }

        /**
         * Encrypt a part of the image.
         * @author Cui Yuxin, Zhao Bowen
         */
        @Override
        public void run() {
            // TODO Save overflow information in the images
            if (img.hasAlpha()) {
                int[] argb = new int[4];
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int pixel = img.getPixel(row, col);
                        argb[0] = Color.red(pixel);
                        argb[1] = Color.green(pixel);
                        argb[2] = Color.blue(pixel);
                        argb[3] = Color.alpha(pixel);
                        int r1 = rnd.nextInt(256);
                        int g1 = rnd.nextInt(256);
                        int b1 = rnd.nextInt(256);
                        int a1 = rnd.nextInt(256);
                        pixel = Color.argb(r1, g1, b1, a1);
                        img1.setPixel(row, col, pixel);
                        int r2 = encrypt(argb[0], r1, 256);
                        int g2 = encrypt(argb[1], g1, 256);
                        int b2 = encrypt(argb[2], b1, 256);
                        int a2 = encrypt(argb[3], b1, 256);
                        pixel = Color.argb(r2, g2, b2, a2);
                        img2.setPixel(row, col, pixel);
                    }
                }
            } else {
                int[] rgb = new int[3];
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
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
                    }
                }
            }
        }
    }
}

