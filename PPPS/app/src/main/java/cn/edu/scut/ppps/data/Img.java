package cn.edu.scut.ppps.data;

import android.graphics.Bitmap;

import java.io.IOException;

import cn.edu.scut.ppps.Utils;

public class Img {
    private Bitmap img;
    private String filePath;
    private String fileName;

    public Img(String filePath) throws IOException {
        this.filePath = filePath;
        this.img = Utils.openImg(filePath);
        this.fileName = Utils.getFileName(filePath);
    }

    public Bitmap getBitmap() {
        return img;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public int getWidth() {
        return img.getWidth();
    }

    public int getHeight() {
        return img.getHeight();
    }

    public boolean hasAlpha() {
        return img.hasAlpha();
    }


}
