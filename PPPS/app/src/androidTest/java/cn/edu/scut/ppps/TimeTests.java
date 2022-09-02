package cn.edu.scut.ppps;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

/**
 * Android Time Tests
 * @author Cui Yuxin, Feng Yucheng
 */
@RunWith(AndroidJUnit4.class)
public class TimeTests {

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Test Utils openImg *.HEIC time.
     * @author Feng Yucheng
     */
    @Test
    public void utilsOpenImgHEICTimeTest() {
        // 获取设备上微信图片文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("微信图片文件夹不存在！请检查是否拥有读取外部存储权限或文件夹是否存在。",
                weiXinPictureDir.exists());
        // 找到微信图片文件夹下的第一张图片
        File[] files = weiXinPictureDir.listFiles((file) -> {
            return file.getName().endsWith("test.HEIC");
        });
        Assert.assertTrue("微信图片文件夹下不存在文件图片",
                files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = files[0].getAbsolutePath();
        Bitmap img = null;
        // 调用测试方法并计时
        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            try {
                img = Utils.openImg(imgFilePath);
            } catch (IOException e) {
                Assert.fail("调用目标函数失败！");
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            long time = end - start;
            Log.d("打开HEIF图片计时", "打开时间: " + time + "ms");
        }
    }

    /**
     * Test Utils openImg *.jpg time.
     * @author Cui Yuxin
     */
    @Test
    public void utilsOpenImgJPGTimeTest() {
        // 获取设备上微信图片文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("微信图片文件夹不存在！请检查是否拥有读取外部存储权限或文件夹是否存在。",
                weiXinPictureDir.exists());
        // 找到微信图片文件夹下的第一张图片
        File[] files = weiXinPictureDir.listFiles((file) -> {
            return file.getName().endsWith("small.jpg");
        });
        Assert.assertTrue("微信图片文件夹下不存在文件图片",
                files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = files[0].getAbsolutePath();
        Bitmap img = null;
        // 调用测试方法并计时
        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            try {
                img = Utils.openImg(imgFilePath);
            } catch (IOException e) {
                Assert.fail("调用目标函数失败！");
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            long time = end - start;
            Log.d("打开JPG图片计时", "打开时间: " + time + "ms");
        }
    }

    /**
     * Test Utils openImg *.webp time.
     * @author Feng Yucheng
     */
    @Test
    public void utilsOpenImgWebpTimeTest() {
        // 获取设备上微信图片文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("微信图片文件夹不存在！请检查是否拥有读取外部存储权限或文件夹是否存在。",
                weiXinPictureDir.exists());
        // 找到微信图片文件夹下的第一张图片
        File[] files = weiXinPictureDir.listFiles((file) -> {
            return file.getName().endsWith("medium.webp");
        });
        Assert.assertTrue("微信图片文件夹下不存在文件图片",
                files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = files[0].getAbsolutePath();
        Bitmap img = null;
        // 调用测试方法并计时
        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            try {
                img = Utils.openImg(imgFilePath);
            } catch (IOException e) {
                Assert.fail("调用目标函数失败！");
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            long time = end - start;
            Log.d("打开webp图片计时", "打开时间: " + time + "ms");
        }
    }

    /**
     * Test Utils saveImg optimize time.
     * @author Feng Yucheng
     */
    @Test
    public void utilsSaveImgTimeTest() {
        // 获取设备上微信图片文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("该文件不存在！请检查路径是否正确。", weiXinPictureDir.exists());
        File[] files = weiXinPictureDir.listFiles((file) -> {
            return file.getName().endsWith("small.jpg");
        });
        Assert.assertTrue("该文件夹不存在文件内容！", files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = imgFileDir + File.separator + "test";
        Bitmap img = null;
        try {
            img = Utils.openImg(files[0].getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 调用测试方法并计时
        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            try {
                Utils.saveImg(img, imgFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            long time = end - start;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                imgFilePath += ".HEIC";
            } else {
                imgFilePath += ".webp";
            }
            File file = new File(imgFilePath);
            long fileSize = file.length();
            double fileSizeMB = fileSize / 1024.0 / 1024.0;
            Log.d("保存图片计时", "保存时间：" + time + "ms, 图片大小：" + fileSizeMB + "MB");
        }
    }
}
