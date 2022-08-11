package cn.edu.scut;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TestRecordImg {

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
                pixel = b1 & 0xff | (g1 & 0xff) << 8 | (r1 & 0xff) << 16;
                bi1.setRGB(x, y, pixel);
                int r2 = encrypt(rgb[0], r1, 256);
                int g2 = encrypt(rgb[1], g1, 256);
                int b2 = encrypt(rgb[2], b1, 256);
                //------Record----- 这里使用了反色
                if(rgb[0] < r1)
                    r2 = 0;
                else
                    r2 = 255;
                if(rgb[1] < g1)
                    g2 = 0;
                else
                    g2 = 255;
                if(rgb[2] < b1)
                    b2 = 0;
                else
                    b2 = 255;

//                System.out.println(rgb[0] + ", " + rgb[1] + ", " + rgb[2]);
//                System.out.println(((r1 + r2) % 256) + ", " + ((g1 + g2) % 256) + ", " + ((b1 + b2) % 256));
                pixel = b2 & 0xff | (g2 & 0xff) << 8 | (r2 & 0xff) << 16;
                bi2.setRGB(x, y, pixel);


            }
        }
        try {
            ImageIO.write(bi1, "jpg", new File(image1));
            ImageIO.write(bi2, "jpg", new File(image2));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String newfile = "images/zbw.jpg";
        BufferedImage bi3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int y = minY; y < height; y++) {
            for(int x = minX; x < width; x++) {
                //获取包含这个像素的颜色信息的值, int型
                int pixel1 = bi1.getRGB(x, y);
                //从pixel中获取rgb的值
                int r1 = (pixel1 & 0xff0000) >> 16; //r
                int g1 = (pixel1 & 0xff00) >> 8; //g
                int b1 = (pixel1 & 0xff); //b

                int pixel2 = bi2.getRGB(x, y);
                //从pixel中获取rgb的值
                int r2 = (pixel2 & 0xff0000) >> 16; //r
                int g2 = (pixel2 & 0xff00) >> 8; //g
                int b2 = (pixel2 & 0xff); //b

                rgb[0] = (r1 + r2) % 256;
                rgb[1] = (g1 + g2) % 256;
                rgb[2] = (b1 + b2) % 256;
                int pixel = rgb[2] & 0xff | (rgb[1] & 0xff) << 8 | (rgb[0] & 0xff) << 16;
                bi3.setRGB(x, y, pixel);
            }
        }
        try {
            ImageIO.write(bi3, "jpg", new File(newfile));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return record;
    }

    public static void genThumbnail(String origin, String newfile, int size) {

        File file = new File(origin);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException e) {

            e.printStackTrace();
        }
        int width = bi.getWidth();
        int height = bi.getHeight();
        BufferedImage bi1 = new BufferedImage(width/size, height/size, BufferedImage.TYPE_INT_RGB);

        int k = 0, t = 0;
        for(int y = 0; y < height; y += size) {
            for(int x = 0; x < width; x += size) {

                int sumr = 0, sumg = 0, sumb = 0;
                for(int j = y; j < y + size; j++) {
                    for(int i = x; i < x + size; i++) {

                        int pixel = bi.getRGB(i, j);
                        sumr += (pixel & 0xff0000) >> 16; //r
                        sumg += (pixel & 0xff00) >> 8; //g
                        sumb += (pixel & 0xff); //b
                    }
                }
                sumr /= (size * size);
                sumg /= (size * size);
                sumb /= (size * size);

                int pixel1 = sumb & 0xff | (sumg & 0xff) << 8 | (sumr & 0xff) << 16;
                bi1.setRGB(x / size, y /size, pixel1);
            }
        }
        try {
            ImageIO.write(bi1, "jpg", new File(newfile));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void reConstruct(String origin1, String origin2, String newfile, int[][][] record) {

        int[][][] ncord = new int[3][30][30];
        for(int x = 0; x < record[0].length; x += 15) {
            for(int y = 0; y < record[0][0].length; y += 15) {

                for(int i = x; i < x + 15; i++) {
                    for(int j = y; j < y + 15; j++) {

                        ncord[0][x / 15][y / 15] += record[0][i][j];
                        ncord[1][x / 15][y / 15] += record[1][i][j];
                        ncord[2][x / 15][y / 15] += record[2][i][j];
                    }
                }
//    			System.out.println(ncord[0][x / 15][y / 15] + " " + ncord[1][x / 15][y / 15] +
//    					" " + ncord[1][x / 15][y / 15]);
            }
        }

        File file1 = new File(origin1);
        File file2 = new File(origin2);
        BufferedImage image1 = null, image2 = null;
        try {
            image1 = ImageIO.read(file1);
            image2 = ImageIO.read(file2);
        } catch (IOException e) {

            e.printStackTrace();
        }
        int width = image1.getWidth();
        int height = image2.getHeight();

        BufferedImage image3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[3];
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                //获取包含这个像素的颜色信息的值, int型
                int pixel1 = image1.getRGB(x, y);
                //从pixel中获取rgb的值
                int r1 = (pixel1 & 0xff0000) >> 16; //r
                int g1 = (pixel1 & 0xff00) >> 8; //g
                int b1 = (pixel1 & 0xff); //b

                int pixel2 = image2.getRGB(x, y);
                //从pixel中获取rgb的值
                int r2 = (pixel2 & 0xff0000) >> 16; //r
                int g2 = (pixel2 & 0xff00) >> 8; //g
                int b2 = (pixel2 & 0xff); //b

                rgb[0] = r1 + r2 - (ncord[0][x][y] * 255 / 225);
                rgb[1] = (g1 + g2) - (ncord[1][x][y] * 255 / 225);;
                rgb[2] = (b1 + b2) - (ncord[2][x][y] * 255 / 225);;
                int pixel = rgb[2] & 0xff | (rgb[1] & 0xff) << 8 | (rgb[0] & 0xff) << 16;
                image3.setRGB(x, y, pixel);
            }
        }
        try {
            ImageIO.write(image3, "jpg", new File(newfile));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String [] args){

        int[][][] record = getImagePixel("images/ZHAO.jpg", "images//zbw1.jpg", "images//zbw2.jpg");

    }
}
