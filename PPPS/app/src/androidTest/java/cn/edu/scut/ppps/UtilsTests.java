package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

/**
 * Utils Unit Tests
 * @author Cui Yuxin, Feng Yucheng
 */
@RunWith(AndroidJUnit4.class)
public class UtilsTests {

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
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
    public void utilsOpenImgTest() {
        // 获取设备上微信图片文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("微信图片文件夹不存在！请检查是否拥有读取外部存储权限或文件夹是否存在。",
                weiXinPictureDir.exists());
        // 找到微信图片文件夹下的第一张图片
        File[] files = weiXinPictureDir.listFiles((file) -> {
            return file.getName().endsWith(".webp");
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
        Assert.assertTrue("该文件夹不存在！，请检查是否外部权限不足或文件夹不存在", weiXinPictureDir.exists());
        // 找到微信图片文件夹下的文件
        File[] files = weiXinPictureDir.listFiles();
        Assert.assertTrue("该文件夹不存在文件内容！", files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = files[0].getAbsolutePath();
        String imgName = null;
        // 调用测试方法
        try {
            imgName = Utils.getFileName(imgFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("调用目标函数出错！");
        }
        // 断言测试方法的结果
        Assert.assertNotNull("调用目标函数失败！", imgName);
        Assert.assertEquals("目标函数结果返回不正确！", files[0].getName(), imgName);
    }

    /**
     * Test Utils saveImg method.
     * @author Feng Yucheng, Cui Yuxin
     */
    @Test
    public void utilsSaveImgTest() {
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("该路径不存在！请检查路径是否正确。", weiXinPictureDir.exists());
        File[] files = weiXinPictureDir.listFiles((file) -> {
            return file.getName().endsWith(".jpg");
        });
        Assert.assertTrue("该照片文件不存在！", files.length > 0);
        String imgFilePath = imgFileDir + File.separator + "test";
        try {
            Utils.saveImg(Utils.openImg(files[0].getAbsolutePath()), imgFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("调用目标函数失败！");
        }
        imgFilePath += ".webp";
        File imgFile = new File(imgFilePath);
        Assert.assertTrue("保存失败！", imgFile.exists());
        try {
            Assert.assertNotNull("目标文件无法正确打开！保存失败！", Utils.openImg(imgFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("目标文件无法正确打开！保存失败！");
        }
    }

    /**
     * Test Utils collapse method.
     * @author Feng Yucheng
     */
    @Test
    public void utilsCollapseTestCase1() {
        //整除，压缩率为整数的测试
        byte[][][] testArray = {
                {
                        {-37, 0b00111101},
                        {-81, -123},
                        {0b01010001, -49},
                        {-81, -1}},
                {
                        {-37, 0b00111101},
                        {-81, -123},
                        {0b01010001, -49},
                        {-81, -1}},
                {
                        {-37, 0b00111101},
                        {-81, -123},
                        {0b01010001, -49},
                        {-81, -1}
                }};
        double ratio = (2 * 8.0) / (16 * 4);
        double[][][] temp = {
                {
                        {4, 3, 2, 3, 2, 3, 2, 1},
                        {3, 2, 2, 2, 4, 4, 2, 4}
                },
                {
                        {4, 3, 2, 3, 2, 3, 2, 1},
                        {3, 2, 2, 2, 4, 4, 2, 4}
                },
                {
                        {4, 3, 2, 3, 2, 3, 2, 1},
                        {3, 2, 2, 2, 4, 4, 2, 4}
                }};
        int[][][] expected = new int[temp.length][temp[0].length][temp[0][0].length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                for (int k = 0; k < temp[i][j].length; k++) {
                    expected[i][j][k] = (int) (temp[i][j][k] * 255 * ratio);
                }
            }
        }
        int[][][] result = Utils.collapse(testArray, 2, 8);
        Assert.assertArrayEquals("结果错误!", expected, result);
    }

    /**
     * Test Utils collapse method.
     * @author Feng Yucheng
     */
    @Test
    public void utilsCollapseTestCase2() {
        // 行数不能整除的情况(16*4->8*3)
        byte[][][] testArray = {
                {
                        {0b01000111, -31},
                        {-81, -13},
                        {-37, 0b00111101},
                        {0b01110010, -106}},
                {
                        {0b01000111, -31},
                        {-81, -13},
                        {-37, 0b00111101},
                        {0b01110010, -106}},
                {
                        {0b01000111, -31},
                        {-81, -13},
                        {-37, 0b00111101},
                        {0b01110010, -106}
                }
        };
        double ratio = (3.0 * 8) / (16 * 4);
        double[][][] temp = {
                {
                        {4, 3, 1, 2, 3, 0, 3, 4},
                        {3, 1, 3, 3, 2, 3, 3, 1},
                        {0, 0, 0, 0, 0, 0, 0, 0}},
                {
                        {4, 3, 1, 2, 3, 0, 3, 4},
                        {3, 1, 3, 3, 2, 3, 3, 1},
                        {0, 0, 0, 0, 0, 0, 0, 0}},
                {
                        {4, 3, 1, 2, 3, 0, 3, 4},
                        {3, 1, 3, 3, 2, 3, 3, 1},
                        {0, 0, 0, 0, 0, 0, 0, 0}
                }};
        int[][][] expected = new int[temp.length][temp[0].length][temp[0][0].length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                for (int k = 0; k < temp[i][j].length; k++) {
                    expected[i][j][k] = (int) (temp[i][j][k] * 255 * ratio);
                }
            }
        }
        int[][][] result = Utils.collapse(testArray, 3, 8);
        Assert.assertArrayEquals("结果错误!", expected, result);
    }

    /**
     * Test Utils collapse method.
     * @author Feng Yucheng
     */
    @Test
    public void utilsCollapseTestCase3() {
        // 列数不能整除的测试(16*4->6*2)
        byte[][][] testArray = {
                {
                        {-25, 0b01110110},
                        {-27, 0b00001000},
                        {0b01000111, -32},
                        {-86, -122}},
                {
                        {-25, 0b01110110},
                        {-27, 0b00001000},
                        {0b01000111, -32},
                        {-86, -122}},
                {
                        {-25, 0b01110110},
                        {-27, 0b00001000},
                        {0b01000111, -32},
                        {-86, -122}},
                {
                        {-25, 0b01110110},
                        {-27, 0b00001000},
                        {0b01000111, -32},
                        {-86, -122}}};
        double ratio = (2 * 6.0) / (16 * 4);
        double[][][] temp = {
                {
                        {5, 2, 4, 3, 3, 0},
                        {4, 2, 2, 2, 2, 2}},
                {
                        {5, 2, 4, 3, 3, 0},
                        {4, 2, 2, 2, 2, 2}},
                {
                        {5, 2, 4, 3, 3, 0},
                        {4, 2, 2, 2, 2, 2}},
                {
                        {5, 2, 4, 3, 3, 0},
                        {4, 2, 2, 2, 2, 2}}};
        int[][][] expected = new int[temp.length][temp[0].length][temp[0][0].length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                for (int k = 0; k < temp[i][j].length; k++) {
                    expected[i][j][k] = (int) (temp[i][j][k] * 255 * ratio);
                }
            }
        }
        int[][][] result = Utils.collapse(testArray, 2, 6);
        Assert.assertArrayEquals("结果错误!", expected, result);
    }

    /**
     * Test Utils saveBytesArray method.
     * @author Cui Yuxin
     */
    @Test
    public void utilsSaveBytesArrayTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath = cachePath + File.separator + "overflow" + File.separator + "test.overflow";
        byte[][][] overflow = new byte[][][]{{{1, 2}, {3, 4}},
                {{5, 6}, {7, 8}},
                {{5, 6}, {7, 8}}};
        try {
            Utils.saveBytesArray(overflow, savePath);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("调用目标函数失败");
        }
        File overflowFile = new File(savePath);
        Assert.assertTrue(overflowFile.exists());
    }

    /**
     * Test Utils loadBytesArray method.
     * @author Feng Yucheng
     */
    @Test
    public void utilsLoadBytesArrayTest() {
        byte[][][] testArray = {{{1, 1}, {2, 2}},
                {{1, 1}, {2, 2}},
                {{1, 1}, {2, 2}}
        };
        byte[][][] array = null;
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath = cachePath + File.separator + "overflow" + File.separator + "test.overflow";
        //保存多维数组
        try {
            Utils.saveBytesArray(testArray, savePath);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("调用目标函数失败");
        }
        File overflowFile = new File(savePath);
        Assert.assertTrue(overflowFile.exists());
        //获取多维数组
        try {
            array = Utils.loadBytesArray(savePath);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("调用目标函数失败");
        }
        Assert.assertArrayEquals(array, testArray);
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

