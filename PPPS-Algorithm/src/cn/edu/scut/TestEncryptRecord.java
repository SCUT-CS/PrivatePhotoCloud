package cn.edu.scut;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TestEncryptRecord {
    private static int encrypt(int x, int r, int d) {
        int c = x - r % d;
        c = c < 0 ? c + d : c;
        return c;
    }

    /**
     * 读取一张图片的RGB值
     */
    public static int[][][] getImagePixel(String image, String image1, String image2) {
        Random rnd = new Random();
        File file = new File(image);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException e) {

            e.printStackTrace();
        }
        int width = bi.getWidth();
        int height = bi.getHeight();
        int[][][] record = new int[3][width][height];
        BufferedImage bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int minX = bi.getMinX();
        int minY = bi.getMinY();
        int[] rgb = new int[3];
        for(int y = minY; y < height; y++) {
            for(int x = minX; x < width; x++) {
                //获取包含这个像素的颜色信息的值, int型

                int pixel = bi.getRGB(x, y);
                //从pixel中获取rgb的值
                rgb[0] = (pixel & 0xff0000) >> 16; //r
                rgb[1] = (pixel & 0xff00) >> 8; //g
                rgb[2] = (pixel & 0xff); //b
                int r1 = rnd.nextInt(256);
                int g1 = rnd.nextInt(256);
                int b1 = rnd.nextInt(256);
//                pixel = rgb[2] & 0xff | (rgb[1] & 0xff) << 8 | (rgb[0] & 0xff) << 16;
                //pixel = b1 & 0xff | (g1 & 0xff) << 8 | (r1 & 0xff) << 16;
                //bi1.setRGB(x, y, pixel);
                int r2 = encrypt(rgb[0], r1, 256);
                int g2 = encrypt(rgb[1], g1, 256);
                int b2 = encrypt(rgb[2], b1, 256);
//                System.out.println(rgb[0] + ", " + rgb[1] + ", " + rgb[2]);
//                System.out.println(((r1 + r2) % 256) + ", " + ((g1 + g2) % 256) + ", " + ((b1 + b2) % 256));
                //pixel = b2 & 0xff | (g2 & 0xff) << 8 | (r2 & 0xff) << 16;
                //bi2.setRGB(x, y, pixel);

                //------Record-----

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
                bi1.setRGB(x, y, r3 & 0xff | (g3 & 0xff) << 8 | (b3 & 0xff) << 16);
                bi2.setRGB(x, y, r4 & 0xff | (g4 & 0xff) << 8 | (b4 & 0xff) << 16);
            }
        }
        try {
            ImageIO.write(bi1, "jpg", new File(image1));
            ImageIO.write(bi2, "jpg", new File(image2));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return record;
    }

    public static void main(String [] args){

        int[][][] record = getImagePixel("images/ZHAO.jpg", "images//zbw1.jpg", "images//zbw2.jpg");

    }
}
