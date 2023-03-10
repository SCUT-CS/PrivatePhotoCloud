package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Environment;
import android.os.Handler;
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
    private int[] encryptedPixels3;
    private int scaledHeight;
    private int scaledWidth;
    private String imgFilePath1;
    private String imgFilePath2;
    private int width;
    private int height;
    private boolean isThumbnail;
    private int[][][] overflow = null;
    private Context context;
    private Handler handler;

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
     * @param isThumbnail Whether the image is a thumbnail.
     * @param context The context.
     * @param handler The handler.
     * @author Cui Yuxin
     */
    public Decrypt(String imgFilePath1, String imgFilePath2, Context context, boolean isThumbnail, Handler handler) {
        this.imgFilePath1 = imgFilePath1;
        this.imgFilePath2 = imgFilePath2;
        this.isThumbnail = isThumbnail;
        this.context = context;
        this.handler = handler;
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
        if (isThumbnail) {
            String imgName = Utils.getFileName(imgFilePath1);
            String originalImgName = imgName.substring(0, imgName.lastIndexOf(".ori"));
            String filePath = context.getDataDir().getAbsolutePath() + File.separator + "overflow" + File.separator;
            overflow = Utils.collapse(filePath + originalImgName, height, width);
        }
    }

    /**
     * Decrypt a image in some new threads.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void decrypt() {
        String imgName = null;
        try {
            imgName = Utils.getFileName(imgFilePath1);
            String originalImgName = imgName.substring(0, imgName.lastIndexOf(".ori"));
            String filePath = context.getDataDir().getAbsolutePath() + File.separator + "overflow" +
                    File.separator + originalImgName + ".webp";
            Bitmap img3 = Utils.openImg(filePath);
//            // 高斯模糊
//            img3 = FastBlur.doBlur(img3, 10, true);
            scaledHeight = img3.getHeight();
            scaledWidth = img3.getWidth();
            encryptedPixels3 = new int[scaledWidth * scaledHeight];
            img3.getPixels(encryptedPixels3, 0, scaledWidth, 0, 0, scaledWidth, scaledHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * Decrypt a image`s thumbnail.
     * @author Cui Yuxin, Zhao Bowen
     */
    private void decryptThumbnail() {
        int[] encryptedPixels1 = new int[width * height];
        int[] encryptedPixels2 = new int[width * height];
        int[] originalPixels = new int[width * height];
        img1.getPixels(encryptedPixels1, 0, width, 0, 0, width, height);
        img2.getPixels(encryptedPixels2, 0, width, 0, 0, width, height);
        if (img1.hasAlpha() && overflow.length == 4 ) {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    // int pixel1 = img1.getPixel(col, row);
                    // int pixel2 = img2.getPixel(col, row);
                    int pixel1 = encryptedPixels1[row * width + col];
                    int pixel2 = encryptedPixels2[row * width + col];
                    int alpha = (pixel1 >>> 24 + pixel2 >>> 24 - overflow[3][row][col]) & 0xff;
                    int red = ((pixel1 >> 16) & 0xFF + (pixel2 >> 16) & 0xFF - overflow[0][row][col]) & 0xff;
                    int green =((pixel1 >> 8) & 0xFF + (pixel2 >> 8) & 0xFF - overflow[1][row][col]) & 0xff;
                    int blue = (pixel1 & 0xFF + pixel2 & 0xFF - overflow[2][row][col]) & 0xff;
                    // img.setPixel(col, row, blue | green << 8 | red << 16 | alpha << 24);
                    originalPixels[row * width + col] = blue | green << 8 | red << 16 | alpha << 24;
                }
            }
        } else {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    // int pixel1 = img1.getPixel(col, row);
                    // int pixel2 = img2.getPixel(col, row);
                    int pixel1 = encryptedPixels1[row * width + col];
                    int pixel2 = encryptedPixels2[row * width + col];
                    int red = (((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF) - (overflow[0][row][col]) & 0xff);
                    int green =(((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF) - (overflow[1][row][col]) & 0xff);
                    int blue = ((pixel1 & 0xFF) + (pixel2 & 0xFF) - (overflow[2][row][col]) & 0xff);
                    // img.setPixel(col, row, blue | green << 8 | red << 16 | 0xff000000);
                    originalPixels[row * width + col] = blue | green << 8 | red << 16 | 0xff000000;
                }
            }
        }
        img.setPixels(originalPixels, 0, width, 0, 0, width, height);
    }

    /**
     * Save images.
     * This method may take several seconds to complete, so it should only be called from a worker thread.
     * @author Cui Yuxin
     */
    private void saveFile() throws Exception {
        if (isThumbnail) {
            String imgName = Utils.getFileName(imgFilePath1);
            String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + "Thumbnail"
                    + File.separator + imgName.substring(0, imgName.lastIndexOf(".webp"));
            Utils.saveImg(img, imgPath);
        } else {
            String imgName = Utils.getFileName(imgFilePath1);
            String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + "PPPS"
                    + File.separator + imgName.substring(0, imgName.lastIndexOf(".ori"));
            Utils.saveJpgImg(img, imgPath);
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
            saveFile();
            if (handler != null) {
                handler.sendEmptyMessage(Utils.THUMBNAIL_SUCCESS);
            }
        } else {
            decrypt();
            saveFile();
            if (handler != null) {
                handler.sendEmptyMessage(Utils.DECRYPT_SUCCESS);
            }
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
                        // int pixel1 = img1.getPixel(col, row);
                        // int pixel2 = img2.getPixel(col, row);
                        int pixel1 = encryptedPixels1[(row - rowStart) * (colEnd - colStart) + (col - colStart)];
                        int pixel2 = encryptedPixels2[(row - rowStart) * (colEnd - colStart) + (col - colStart)];
                        int rowIndex = (int) (row * scale);
                        int colIndex = (int) (col * scale);
                        int pixel3 = encryptedPixels3[rowIndex * scaledWidth + colIndex];
                        int pixel = Color.argb(((pixel1 >>> 24) + (pixel2 >>> 24) + (pixel3 >>> 24)) & 0xff,
                                (((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF) + ((pixel3 >> 16) & 0xFF)) & 0xff,
                                (((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF) + ((pixel3 >> 8) & 0xFF)) & 0xff,
                                ((pixel1 & 0xFF) + (pixel2 & 0xFF) + (pixel3 & 0xFF)) & 0xff);
                        // img.setPixel(col, row, pixel);
                        originalPixels[(row - rowStart) * (colEnd - colStart) + (col - colStart)] = pixel;
                    }
                }
            } else {
                Log.d("Decrypt", "图片没有透明度通道");
                for (int row = rowStart; row < rowEnd; row++) {
                    for (int col = colStart; col < colEnd; col++) {
                        // int pixel1 = img1.getPixel(col, row);
                        // int pixel2 = img2.getPixel(col, row);
                        int pixel1 = encryptedPixels1[(row - rowStart) * (colEnd - colStart) + (col - colStart)];
                        int pixel2 = encryptedPixels2[(row - rowStart) * (colEnd - colStart) + (col - colStart)];
                        int rowIndex = (int) (row * scale);
                        int colIndex = (int) (col * scale);
                        int pixel3 = encryptedPixels3[rowIndex * scaledWidth + colIndex];
                        int tred = ((pixel3 >> 16) & 0xFF);
                        int tgreen = ((pixel3 >> 8) & 0xFF);
                        int tblue = (pixel3 & 0xFF);
//                        // 提升亮度
//                        tred+=40;
//                        if (tred > 255) {
//                            tred = 255;
//                        }
//                        tgreen+=40;
//                        if (tgreen > 255) {
//                            tgreen = 255;
//                        }
//                        tblue+=40;
//                        if (tblue > 255) {
//                            tblue = 255;
//                        }

//                        // 提升对比度
//                        tred = (int) ((tred - 127.5) * 2 + 127.5);
//                        if (tred > 255) {
//                            tred = 255;
//                        } else if (tred < 0) {
//                            tred = 0;
//                        }
//                        tgreen = (int) ((tgreen - 127.5) * 2 + 127.5);
//                        if (tgreen > 255) {
//                            tgreen = 255;
//                        } else if (tgreen < 0) {
//                            tgreen = 0;
//                        }
//                        tblue = (int) ((tblue - 127.5) * 2 + 127.5);
//                        if (tblue > 255) {
//                            tblue = 255;
//                        } else if (tblue < 0) {
//                            tblue = 0;
//                        }

                        int red = (((pixel1 >> 16) & 0xFF) + ((pixel2 >> 16) & 0xFF) + tred) & 0xff;
                        int green = (((pixel1 >> 8) & 0xFF) + ((pixel2 >> 8) & 0xFF) + tgreen) & 0xff;
                        int blue = ((pixel1 & 0xFF) + (pixel2 & 0xFF) + tblue) & 0xff;

//                        // 上下溢处理
//                        if (Math.abs(red - tred) > 100) {
//                            if(tred<127) {
//                                red = 0;
//                            } else {
//                                red = 255;
//                            }
//                        }
//                        if (Math.abs(green - tgreen) > 100) {
//                            if(tgreen<127) {
//                                green = 0;
//                            } else {
//                                green = 255;
//                            }
//                        }
//                        if (Math.abs(blue - tblue) > 100) {
//                            if(tblue<127) {
//                                blue = 0;
//                            } else {
//                                blue = 255;
//                            }
//                        }

//                        // 上下溢处理
//                        if (Math.abs(red - tred) > 100) {
//                            red = tred;
//                        }
//                        if (Math.abs(green - tgreen) > 100) {
//                            green = tgreen;
//                        }
//                        if (Math.abs(blue - tblue) > 100) {
//                            blue = tblue;
//                        }

//                        //直接输出缩略图
//                        red = tred;
//                        green = tgreen;
//                        blue = tblue;

                        int pixel = Color.rgb(red,green,blue);
                        // img.setPixel(col, row, pixel);
                        originalPixels[(row - rowStart) * (colEnd - colStart) + (col - colStart)] = pixel;
                    }
                }
            }
            img.setPixels(originalPixels, 0, width, colStart, rowStart, (colEnd - colStart), (rowEnd - rowStart));
        }
    }
}
