package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
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
            return file.getName().endsWith(".HEIC");
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
     * Test Utils openImg method.
     * Case: HEIC encrypted image.
     * @author Cui Yuxin
     */
    @Test
    public void utilsOpenImgTestCaseEncryptedHEIC() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath1 = cachePath + File.separator + "Disk1" + File.separator + "jpg_small.jpg.ori.HEIC";
        //String savePath1 = cachePath + File.separator + "Disk1" + File.separator + "test.HEIC";
        File file1 = new File(savePath1);
        Assert.assertTrue("文件不存在！", file1.exists());
        try {
            Bitmap img = Utils.openImg(savePath1);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("打开HEIC加密图片失败！");
        }
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            imgFilePath += ".HEIC";
        } else {
            imgFilePath += ".webp";
        }
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

        //列数不能整除的测试
        byte[][][] testArray2 = {
                {
                        {-25,0b0111011},
                        {-27,0b0000100},
                        {0b01000111,112},
                        {-86,67}
                },
                {
                        {-25,0b0111011},
                        {-27,0b0000100},
                        {0b01000111,112},
                        {-86,67}
                },
                {
                        {-25,0b0111011},
                        {-27,0b0000100},
                        {0b01000111,112},
                        {-86,67}
                },
                {
                        {-25,0b0111011},
                        {-27,0b0000100},
                        {0b01000111,112},
                        {-86,67}
                }
        };
        double ratio2 = ( 2 * 8 ) / ( 15.0 * 4 );
        double[][][] temp2 = {
                {
                    {4,2,2,3,1,2,2,1},
                    {2,1,2,3,2,1,1,1}
                },
                {
                    {4,2,2,3,1,2,2,1},
                    {2,1,2,3,2,1,1,1}
                },
                {
                    {4,2,2,3,1,2,2,1},
                    {2,1,2,3,2,1,1,1}
                }};
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp2[i].length; j++) {
                for (int k = 0; k < temp2[i][j].length; k++) {
                    expected[i][j][k] = (int) (temp2[i][j][k] * 255 * ratio2);
                }
            }
        }
        int[][][] result2 = Utils.collapse(testArray2, 2, 8);
        Assert.assertArrayEquals("结果错误!", expected, result2);

        //行数不能整除的情况
        byte[][][] testArray3 = {
                {
                        {0b01000111,-31},
                        {-81,-13},
                        {-37,0b00111101}
                },
                {
                        {0b01000111,-31},
                        {-81,-13},
                        {-37,0b00111101}
                },
                {
                        {0b01000111,-31},
                        {-81,-13},
                        {-37,0b00111101}
                }
        };
        double ratio3 = (2.0*8)/(16*3);
        double[][][] temp3 = {
                {
                        {2,1,3,4,4,3,0,3},
                        {2,1,1,2,0,2,2,1}
                },
                {
                        {2,1,3,4,4,3,0,3},
                        {2,1,1,2,0,2,2,1}
                },
                {
                        {2,1,3,4,4,3,0,3},
                        {2,1,1,2,0,2,2,1}
                }};
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp3[i].length; j++) {
                for (int k = 0; k < temp3[i][j].length; k++) {
                    expected[i][j][k] = (int) (temp2[i][j][k] * 255 * ratio2);
                }
            }
        }
        int[][][] result3 = Utils.collapse(testArray3, 2, 8);
        Assert.assertArrayEquals("结果错误!", expected, result3);

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
        byte[][][] testArray={{{1,1},{2,2}},
                {{1,1},{2,2}},
                {{1,1},{2,2}}
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

