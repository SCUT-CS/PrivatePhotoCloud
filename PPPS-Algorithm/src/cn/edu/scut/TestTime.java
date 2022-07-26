package cn.edu.scut;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TestTime {
        private static int encrypt(int x, int r, int d) {
            int c = x - r % d;
            c = c < 0 ? c + d : c;
            return c;
        }

        /**
         * 读取一张图片的RGB值
         */
        public static void getImagePixel(String image) {
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
//                System.out.println(rgb[0] + ", " + rgb[1] + ", " + rgb[2]);
//                System.out.println(((r1 + r2) % 256) + ", " + ((g1 + g2) % 256) + ", " + ((b1 + b2) % 256));
                    pixel = b2 & 0xff | (g2 & 0xff) << 8 | (r2 & 0xff) << 16;
                    bi2.setRGB(x, y, pixel);

                    //------Record-----
                    if(rgb[0] < r1)
                        record[0][x][y] = 1;
                    if(rgb[1] < g1)
                        record[1][x][y] = 1;
                    if(rgb[2] < b1)
                        record[2][x][y] = 1;
                }
            }


        }


        public static void main(String [] args){

            //时间测试1
            long startTime = System.currentTimeMillis();
            getImagePixel("images/ZHAO.jpg");
            long endTime = System.currentTimeMillis();
            long usedTime = endTime-startTime;
            System.out.println("demo"+usedTime);

            //时间测试2
            startTime = System.currentTimeMillis();
            getImagePixel("images/jpg_medium.jpg");
            endTime = System.currentTimeMillis();
            usedTime = endTime-startTime;
            System.out.println("medium"+usedTime);

            //时间测试3
            startTime = System.currentTimeMillis();
            getImagePixel("images/jpg_high.jpg");
            endTime = System.currentTimeMillis();
            usedTime = endTime-startTime;
            System.out.println("high"+usedTime);

        }


}
