package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AliOSS Unit Tests
 * @author Feng Yucheng , Huang Zixi , Zuo Xiaole
 */
@RunWith(AndroidJUnit4.class)
public class AliOSSTests {

    AliOSS AliOSSTests = null;

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Test AliOSS upload method.
     * @author Feng Yucheng
     */
    @Test
    public void uploadTest() {
        //找到一个文件夹
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        Assert.assertTrue("微信图片文件夹不存在！请检查是否拥有读取外部存储权限或文件夹是否存在。",
                weiXinPictureDir.exists());


    }
}