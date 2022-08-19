package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;


/**
 * Android Unit Tests
 * @author Cui Yuxin, Feng Yucheng
 */
@RunWith(AndroidJUnit4.class)
public class AndroidUnitTests {

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     * @author Cui Yuxin
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Test android environment.
     * @author Cui Yuxin
     */
    @Test
    public void environmentTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String a = Environment.getDataDirectory().getAbsolutePath();
        String b = Environment.getExternalStorageDirectory().getAbsolutePath();
        String c = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        String d = appContext.getFilesDir().getAbsolutePath();
        String e = appContext.getCacheDir().getAbsolutePath();
    }

    /**
     * Test Utils openImg method.
     * @author Cui Yuxin
     */
    @Test
    public void utilsOpenImgTest(){
        // 获取设备上微信图片文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("微信图片文件夹不存在！请检查是否拥有读取外部存储权限或文件夹是否存在。",
                weiXinPictureDir.exists());
        // 找到微信图片文件夹下的第一张图片
        File[] files = weiXinPictureDir.listFiles((file)->{
            return file.getName().endsWith(".jpg");
        });
        Assert.assertTrue("微信图片文件夹下不存在文件图片",
                files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = files[0].getAbsolutePath();
        String imgName = null;
        Bitmap img = null;
        // 调用测试方法
        try {
            imgName = Utils.getFileName(imgFilePath);
            img = Utils.openImg(imgFilePath);
        } catch (IOException e) {
            Assert.fail("调用目标函数失败！");
            e.printStackTrace();
        }
        // 断言测试方法的结果
        Assert.assertNotNull("目标函数返回结果为空！", img);
        Assert.assertNotNull("目标函数返回结果为空！", imgName);
        Assert.assertEquals("目标函数返回结果不正确！", files[0].getName(), imgName);
        Assert.assertTrue("目标函数结果返回不正确！", img.getWidth() > 0);
    }

    /**
     * Test Utils getFileName method.
     * @author Feng Yucheng
     */
    @Test
    public void utilsGetFileNameTest() {
        // 获取设备上微信图片文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("该文件夹不存在！，请检查是否外部权限不足或文件夹不存在",weiXinPictureDir.exists());
        // 找到微信图片文件夹下的文件
        File[] files = weiXinPictureDir.listFiles();
        Assert.assertTrue("该文件夹不存在文件内容！",files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = files[0].getAbsolutePath();
        String imgName = null;
        // 调用测试方法
        try {
            imgName = Utils.getFileName(imgFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 断言测试方法的结果
        Assert.assertNotNull("调用目标函数失败！",imgName);
        Assert.assertEquals("储存失败！与原照片不一致。",files[0].getName(), imgName);
    }

    /**
     * Test Utils saveImg method.
     * @author Feng Yucheng
     */
    @Test
    public void utilsSaveImgTest(){
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("该路径不存在！请检查路径是否正确。",weiXinPictureDir.exists());
        File[] files = weiXinPictureDir.listFiles((file)->{
            return file.getName().endsWith(".png");
        });
        Assert.assertTrue("该照片文件不存在！",files.length > 0);
        String imgFilePath = imgFileDir + File.separator + "test.webp";
        try {
            Utils.saveImg(Utils.openImg(files[0].getAbsolutePath()), imgFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File imgFile = new File(imgFilePath);
        Assert.assertTrue("该文件不存在！请检查路径是否正确",imgFile.exists());
        try {
            Assert.assertNotNull("目标文件无法正确打开！请检查路径是否正确。",Utils.openImg(imgFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test Utils openImg *.HEIC time.
     * @author Feng Yucheng
     */
    @Test
    public void utilsOpenImgHEICTimeTest(){
        // 获取设备上微信图片文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("微信图片文件夹不存在！请检查是否拥有读取外部存储权限或文件夹是否存在。",
                weiXinPictureDir.exists());
        // 找到微信图片文件夹下的第一张图片
        File[] files = weiXinPictureDir.listFiles((file)->{
            return file.getName().endsWith(".HEIC");
        });
        Assert.assertTrue("微信图片文件夹下不存在文件图片",
                files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = files[0].getAbsolutePath();
        String imgName = null;
        Bitmap img = null;
        // 调用测试方法
        // TODO 计时
        try {
            img = Utils.openImg(imgFilePath);
        } catch (IOException e) {
            Assert.fail("调用目标函数失败！");
            e.printStackTrace();
        }
        // 断言测试方法的结果
        Assert.assertNotNull("目标函数返回结果为空！", img);
        Assert.assertNotNull("目标函数返回结果为空！", imgName);
        Assert.assertEquals("目标函数返回结果不正确！", files[0].getName(), imgName);
        Assert.assertTrue("目标函数结果返回不正确！", img.getWidth() > 0);
    }

    /**
     * Test Utils openImg *.webp time.
     * @author Feng Yucheng
     */
    @Test
    public void utilsOpenImgWebpTimeTest(){

    }

    /**
     * Test Utils saveImg optimize time.
     * @author Feng Yucheng
     */
    @Test
    public void utilsSaveImgOptimizeTest(){
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("该文件不存在！请检查路径是否正确。",weiXinPictureDir.exists());
        File[] files = weiXinPictureDir.listFiles((file)->{
            return file.getName().endsWith(".png");
        });
        Assert.assertTrue("该文件夹不存在文件内容！",files.length > 0);
        String imgFilePath = imgFileDir + File.separator + "test.webp";
        // TODO 计时
        Calendar nowTime = Calendar.getInstance();
        int mins = nowTime.get(Calendar.MINUTE);
        int secs = nowTime.get(Calendar.SECOND);
        int sum1 = 60*mins+secs;
        try {
            Utils.saveImg(Utils.openImg(files[0].getAbsolutePath()), imgFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Calendar finishTime = Calendar.getInstance();
        int mins2 = finishTime.get(Calendar.MINUTE);
        int secs2 = finishTime.get(Calendar.SECOND);
        int sum2 = 60*mins+secs;
        int runningTime = sum2 - sum1;
        // TODO 计算文件大小
        Long fileSize = files[0].length();
        double fileSizeMB = new BigDecimal(fileSize/1024/1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        File imgFile = new File(imgFilePath);
        Assert.assertTrue("该照片文件不存在！请检查路径是否正确",imgFile.exists());
        try {
            Assert.assertNotNull(Utils.openImg(imgFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public static void getFileSize(File file){
        //通过输出流获取长度
        FileInputStream fis = null;
        try {
            if(file.exists() && file.isFile()){
                String fileName = file.getName();
                fis = new FileInputStream(file);
                System.out.println("文件"+fileName+"的大小是："+fis.available()+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(null!=fis){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    */

}

