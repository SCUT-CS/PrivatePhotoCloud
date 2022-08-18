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
    private byte[][][] overflow;

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
        img1 = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGBA_F16,
                img.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
        img2 = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGBA_F16,
                img.hasAlpha(),
                ColorSpace.get(ColorSpace.Named.SRGB));
        if (img.hasAlpha()) {
            overflow = new byte[4][height][(int) Math.ceil(width / 8.0)];
        } else {
            overflow = new byte[3][height][(int) Math.ceil(width / 8.0)];
        }
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
        String savePath1 = cachePath + File.separator + "Disk1" + File.separator + fileName + ".ori" + ".webp";
        String savePath2 = cachePath + File.separator + "Disk2" + File.separator + fileName +  ".ori" + ".webp";
        Utils.saveImg(img1, savePath1);
        Utils.saveImg(img2, savePath2);
        String savePath = context.getDataDir().getAbsolutePath() + File.separator + "overflow" + File.separator + fileName;
        Utils.saveBytesArray(overflow, savePath);
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
            int c = x - r;
            return c < 0 ? c + d : c;
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
                        int pixel = img.getPixel(row, col);
                        argb[0] = pixel >>> 24;
                        argb[1] = (pixel >> 16) & 0xFF;
                        argb[2] = (pixel >> 8) & 0xFF;
                        argb[3] = pixel & 0xFF;
                        int a1 = rnd.nextInt(256);
                        int r1 = rnd.nextInt(256);
                        int g1 = rnd.nextInt(256);
                        int b1 = rnd.nextInt(256);
                        //pixel = Color.argb(a1, r1, g1, b1);
                        // int color = (A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff)
                        pixel = b1 | g1 << 8 | r1 << 16 | a1 << 24;
                        img1.setPixel(row, col, pixel);
                        int a2 = (argb[0] - a1) & 0xff;
                        int r2 = (argb[1] - r1) & 0xff;
                        int g2 = (argb[2] - g1) & 0xff;
                        int b2 = (argb[3] - b1) & 0xff;
                        //pixel = Color.argb(a2, r2, g2, b2);
                        pixel = b2 | g2 << 8 | r2 << 16 | a2 << 24;
                        img2.setPixel(row, col, pixel);
                        // encrypt the overflow information
                        if (argb[0] < a1) {
                            overflow[0][row][col & 037777777770] = (byte) (overflow[0][row][col & 037777777770] | (1 << (col & 0b111)));
                        }
                        if (argb[1] < r1) {
                            overflow[1][row][col & 037777777770] = (byte) (overflow[1][row][col & 037777777770] | (1 << (col & 0b111)));
                        }
                        if (argb[2] < g1) {
                            overflow[2][row][col & 037777777770] = (byte) (overflow[2][row][col & 037777777770] | (1 << (col & 0b111)));
                        }
                        if (argb[3] < b1) {
                            overflow[3][row][col & 037777777770] = (byte) (overflow[3][row][col & 037777777770] | (1 << (col & 0b111)));
                        }
                    }
                }
            } else {
                int[] rgb = new int[3];
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        int pixel = img.getPixel(row, col);
                        rgb[0] = (pixel >> 16) & 0xFF;
                        rgb[1] = (pixel >> 8) & 0xFF;
                        rgb[2] = pixel & 0xFF;
                        int r1 = rnd.nextInt(256);
                        int g1 = rnd.nextInt(256);
                        int b1 = rnd.nextInt(256);
                        pixel = b1 | g1 << 8 | r1 << 16;
                        img1.setPixel(row, col, pixel);
                        int r2 = (rgb[0]- r1) & 0xff;
                        int g2 = (rgb[1]- g1) & 0xff;
                        int b2 = (rgb[2]- b1) & 0xff;
                        pixel = b2 | g2 << 8 | r2 << 16;
                        img2.setPixel(row, col, pixel);
                        // encrypt the overflow information
                        if (rgb[0] < r1) {
                            overflow[0][row][col & 037777777770] = (byte) (overflow[0][row][col & 037777777770] | (1 << (col & 0b111)));
                        }
                        if (rgb[1] < g1) {
                            overflow[1][row][col & 037777777770] = (byte) (overflow[1][row][col & 037777777770] | (1 << (col & 0b111)));
                        }
                        if (rgb[2] < b1) {
                            overflow[2][row][col & 037777777770] = (byte) (overflow[2][row][col & 037777777770] | (1 << (col & 0b111)));
                        }
                    }
                }
            }
        }
    }
}

