package cn.edu.scut.ppps.old;

import android.content.Context;
import android.graphics.Bitmap;

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

import cn.edu.scut.ppps.crypto.Decrypt;
import cn.edu.scut.ppps.Utils;

/**
 * Decrypt Thumbnail Unit Tests
 * @author Cui Yuxin
 */
@RunWith(AndroidJUnit4.class)
public class DecryptThumbnailTest {

    Decrypt decrypt = null;
    Field[] fields = null;
    Method[] methods = null;
    final String fileName = "ZHAO.jpg";

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Prepare the environment for the test.
     * @author Cui Yuxin
     */
    @Before
    public void setUp() throws Exception {
        // 构造函数参数
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String imgPath1 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + fileName + ".ori.webp";
        String imgPath2 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk2" + File.separator + fileName + ".ori.webp";
        // 通过反射创造Encrypt类
        Class decryptClass = null;
        decryptClass = Class.forName("cn.edu.scut.ppps.crypto.Decrypt");
        Constructor constructor = null;
        constructor = decryptClass.getConstructor(String.class, String.class, Context.class, boolean.class);
        decrypt = (Decrypt) constructor.newInstance(imgPath1, imgPath2, context, true);
        // 获取类内部变量和方法
        fields = decryptClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        methods = decryptClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
        }
        // 测试构造函数
        for (Field field : fields) {
            if (field.getName().equals("imgFilePath1")) {
                Assert.assertEquals(imgPath1, field.get(decrypt));
            } else if (field.getName().equals("imgFilePath2")) {
                Assert.assertEquals(imgPath2, field.get(decrypt));
            } else if (field.getName().equals("appContext")) {
                Assert.assertEquals(context, field.get(decrypt));
            } else if (field.getName().equals("isThumbnail")) {
                Assert.assertEquals(true, field.get(decrypt));
            }
        }
        // 准备测试前期工作
        for (Method method : methods) {
            if (method.getName().equals("openFile")) {
                method.invoke(decrypt);
            }
        }
        for (Method method : methods) {
            if (method.getName().equals("initialize")) {
                method.invoke(decrypt);
                break;
            }
        }
        for (Field field : fields) {
            if (field.getName().equals("img1")
                    || field.getName().equals("img2")
                    || field.getName().equals("img")
                    || field.getName().equals("width")
                    || field.getName().equals("height")
                    || field.getName().equals("overflow")) {
                Assert.assertNotNull(field.get(decrypt));
            }
        }
    }

    /**
     * Test decrypt decryptThumbnail method.
     * @author Cui Yuxin
     */
    @Test
    public void decryptThumbnailTest() throws Exception {
        Bitmap img = null;
        for (Method method : methods) {
            if (method.getName().equals("decryptThumbnail")) {
                method.invoke(decrypt);
                for (Field field : fields) {
                    if (field.getName().equals("img")) {
                        img = (Bitmap) field.get(decrypt);
                    }
                }
                break;
            }
        }
        Assert.assertNotNull(img);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String imgPath1 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + "ZHAO_thumbnail.png";
        Bitmap expectedImg = Utils.openImg(imgPath1);
        Assert.assertEquals(expectedImg.getWidth(), img.getWidth());
        Assert.assertEquals(expectedImg.getHeight(), img.getHeight());
        Utils.saveImg(img, context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + "111.png");
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Assert.assertEquals("H:W" + j + i, expectedImg.getPixel(i, j), img.getPixel(i, j));
            }
        }
    }

    /**
     * Test decrypt call method.
     * @author Cui Yuxin
     */
    @Test
    public void callTest() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String imgPath1 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + fileName + ".ori.png";
        String imgPath2 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk2" + File.separator + fileName + ".ori.png";
        Decrypt decrypt = new Decrypt(imgPath1, imgPath2, context, true);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(16));
        Future<Bitmap> results = threadPoolExecutor.submit(decrypt);
        Bitmap result = null;
        result = results.get();
        String imgPath = context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + "ZHAO_thumbnail.png";
        Bitmap expectedImg = Utils.openImg(imgPath);
        Assert.assertEquals(expectedImg.getWidth(), result.getWidth());
        Assert.assertEquals(expectedImg.getHeight(), result.getHeight());
        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
                Assert.assertEquals(expectedImg.getPixel(i, j), result.getPixel(i, j));
            }
        }
    }
}
