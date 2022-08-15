package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
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
 * Android Unit Tests
 * @author Cui Yuxin
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
        Assert.assertTrue(weiXinPictureDir.exists());
        // 找到微信图片文件夹下的第一张图片
        File[] files = weiXinPictureDir.listFiles((file)->{
            return file.getName().endsWith(".jpg");
        });
        Assert.assertTrue(files.length > 0);
        // 初始化测试方法的参数
        String imgFilePath = files[0].getAbsolutePath();
        String imgName = null;
        Bitmap img = null;
        // 调用测试方法
        try {
            imgName = Utils.getFileName(imgFilePath);
            img = Utils.openImg(imgFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 断言测试方法的结果
        Assert.assertNotNull(img);
        Assert.assertNotNull(imgName);
        Assert.assertEquals(files[0].getName(), imgName);
        Assert.assertTrue(img.getWidth() > 0);
    }

    /**
     * Test Utils getFileName method.
     * @author Feng YuCheng
     */
    @Test
    public void utilsGetFileNameTest(){
        String photoName;
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue(weiXinPictureDir.exists());
        if(weiXinPictureDir.getName().endsWith(".jpg")){
            photoName=weiXinPictureDir.getName();
        }
        System.out.println(photoName);
    }

    /**
     * Test Utils saveImg method.
     * @author TODO:YOUR_NAME
     */
    @Test
    public void utilsSaveImgTest(){
        // TODO YOUR CODE HERE
    }
}

