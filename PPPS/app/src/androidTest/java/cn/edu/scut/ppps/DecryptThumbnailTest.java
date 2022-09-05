package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public void setUp() {
        // 构造函数参数
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String imgPath1 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + fileName + ".ori.png";
        String imgPath2 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk2" + File.separator + fileName + ".ori.png";
        // 通过反射创造Encrypt类
        Class decryptClass = null;
        try {
            decryptClass = Class.forName("cn.edu.scut.ppps.Decrypt");
            Constructor constructor = null;
            constructor = decryptClass.getConstructor(String.class, String.class, Context.class, boolean.class);
            decrypt = (Decrypt) constructor.newInstance(imgPath1, imgPath2, context, true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        // 获取类内部变量和方法
        try {
            fields = decryptClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
            }
            methods = decryptClass.getDeclaredMethods();
            for (Method method : methods) {
                method.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        // 测试构造函数
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        // 准备测试前期工作
        for (Method method : methods) {
            if (method.getName().equals("openFile")) {
                try {
                    method.invoke(decrypt);
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                break;
            }
        }
        for (Method method : methods) {
            if (method.getName().equals("initialize")) {
                try {
                    method.invoke(decrypt);
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                break;
            }
        }
        try {
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
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test decrypt decryptThumbnail method.
     * @author Cui Yuxin
     */
    @Test
    public void decryptThumbnailTest() throws Exception{
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
        //Utils.saveImg(img, context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + "111.png");
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Assert.assertEquals(expectedImg.getPixel(i, j), img.getPixel(i, j));
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
