package com.hao.zxinglib.zxing.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hao.baselib.utils.BitmapUtil;
import com.hao.zxinglib.zxing.decode.RGBLuminanceSource;
import com.hao.zxinglib.zxing.inner.QrcodeScanInterface;

import java.util.Hashtable;

/**
 * 二维码工具类
 * @author WaterWood
 */
public class QrCodeUtils {
    /**
     * 生成二维码方法
     * @param text
     * @return
     */
    public static Bitmap createQRCode(String text) {
        int QR_WIDTH = 100;
        int QR_HEIGHT = 100;
        try {
            // 需要引入core包
            QRCodeWriter writer = new QRCodeWriter();
            if (text == null || "".equals(text) || text.length() < 1) {
                return null;
            }
            // 把输入的文本转为二维码
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
            System.out.println("w:" + martix.getWidth() + "h:" + martix.getHeight());
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            // cheng chen de er wei ma
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 外部调用path转扫描结果
     * @param path
     * @return
     */
    public static void scanQrCodeImage(final Activity activity, final String path, final QrcodeScanInterface qrcodeScanInterface){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Result result = scanningImage(path);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == null) {
                            qrcodeScanInterface.scanFailure("图片格式有误");
                        } else {
                            // 数据返回，在这里去处理扫码结果
                            String recode = result.toString();
                            qrcodeScanInterface.scanSuccess(recode);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * path转扫描结果
     * @param path
     * @return
     */
    private static Result scanningImage(String path) {
        if (path == null || path.equals("")) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        try {
            //将path转为bitmap
            Bitmap scanBitmap = BitmapUtil.openImage(path);
            RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();
            return reader.decode(bitmap1, hints);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
