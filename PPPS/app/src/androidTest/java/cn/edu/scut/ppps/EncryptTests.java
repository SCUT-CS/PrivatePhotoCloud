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
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Encrypt Unit Tests
 * @author Cui Yuxin
 */
@RunWith(AndroidJUnit4.class)
public class EncryptTests {

    Encrypt encrypt = null;
    Field[] fields = null;
    Method[] methods = null;

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     * @author Cui Yuxin
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
        String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin"
                + File.separator + "jpg_medium.jpg";
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // 通过反射创造Encrypt类
        Class encryptClass = null;
        encryptClass = Class.forName("cn.edu.scut.ppps.Encrypt");
        Constructor constructor = null;
        constructor = encryptClass.getConstructor(String.class, Context.class);
        encrypt = (Encrypt) constructor.newInstance(imgPath, appContext);
        // 获取类内部变量和方法
        fields = encryptClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        methods = encryptClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
        }
        // 测试构造函数
        for (Field field : fields) {
            if (field.getName().equals("filePath")) {
                Assert.assertNotNull(field.get(encrypt));
            } else if (field.getName().equals("context")) {
                Assert.assertNotNull(field.get(encrypt));
            }
        }
    }

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
        for (Field field : fields) {
            Log.d("EncryptTests", field.getName());
        }
        for (Method method : methods) {
            Log.d("EncryptTests", method.getName());
        }
    }

    /**
     * Test encrypt openFile method.
     * @author Cui Yuxin
     */
    @Test
    public void openFileTest() throws Exception {
        Bitmap img = null;
        for (Method method : methods) {
            if (method.getName().equals("openFile")) {
                method.invoke(encrypt);
                for (Field field : fields) {
                    if (field.getName().equals("img")) {
                        img = (Bitmap) field.get(encrypt);
                        Assert.assertNotNull(img);
                        Assert.assertEquals("图片获取失败！", 4512, img.getWidth());
                        Assert.assertEquals("图片获取失败！", 6016, img.getHeight());
                    } else if (field.getName().equals("fileName")) {
                        Assert.assertNotNull(field.get(encrypt));
                        Assert.assertEquals("文件名获取失败！", field.get(encrypt), "jpg_medium.jpg");
                    }
                }
                break;
            }
        }
    }

    /**
     * Test encrypt encryptFile method.
     * @author Cui Yuxin
     */
    @Test
    public void encryptTest() throws Exception {
        Bitmap img1 = null;
        Bitmap img2 = null;
        Bitmap img = null;
        for (Method method : methods) {
            if (method.getName().equals("openFile")) {
                method.invoke(encrypt);
                break;
            }
        }
        for (Method method : methods) {
            if (method.getName().equals("encrypt")) {
                method.invoke(encrypt);
                for (Field field : fields) {
                    if (field.getName().equals("width")) {
                        Assert.assertNotNull(field.get(encrypt));
                        Assert.assertEquals("图片宽度不一致！", 4512, field.get(encrypt));
                    } else if (field.getName().equals("height")) {
                        Assert.assertNotNull(field.get(encrypt));
                        Assert.assertEquals("图片高度不一致！", 6016, field.get(encrypt));
                    } else if (field.getName().equals("img1")) {
                        Assert.assertNotNull(field.get(encrypt));
                        img1 = (Bitmap) field.get(encrypt);
                    } else if (field.getName().equals("img2")) {
                        Assert.assertNotNull(field.get(encrypt));
                        img2 = (Bitmap) field.get(encrypt);
                    } else if (field.getName().equals("overflow")) {
                        Assert.assertNotNull(field.get(encrypt));
                    } else if (field.getName().equals("img")) {
                        Assert.assertNotNull(field.get(encrypt));
                        img = (Bitmap) field.get(encrypt);
                    }
                }
                break;
            }
        }
        // 检查加密结果
        Assert.assertNotNull(img);
        Assert.assertNotNull(img1);
        Assert.assertNotNull(img2);
        // 加密算法正确性检查
        for (int i = 0; i < 1000; i++) {
            int row = (int) (Math.random() * img.getHeight());
            int col = (int) (Math.random() * img.getWidth());
            int pixel = img.getPixel(col, row);
            int pixel1 = img1.getPixel(col, row);
            int pixel2 = img2.getPixel(col, row);
            Assert.assertEquals("加密算法错误！(R)row：" + row + "col:" + col,
                    Color.red(pixel),
                    (Color.red(pixel2) + Color.red(pixel1)) % 256);
            Assert.assertEquals("加密算法错误！(G)row：" + row + "col:" + col,
                    Color.green(pixel),
                    (Color.green(pixel2) + Color.green(pixel1)) % 256);
            Assert.assertEquals("加密算法错误！(B)row：" + row + "col:" + col,
                    Color.blue(pixel),
                    (Color.blue(pixel2) + Color.blue(pixel1)) % 256);
            if (img.hasAlpha()) {
                Assert.assertEquals("加密算法错误！(A)row：" + row + "col:" + col,
                        Color.alpha(pixel),
                        (Color.alpha(pixel2) + Color.alpha(pixel1)) % 256);
            }
        }
    }

    /**
     * Test encrypt saveFile method.
     * @author Cui Yuxin
     */
    @Test
    public void saveFileTest() throws Exception {
        Bitmap img1 = null;
        Bitmap img2 = null;
        Bitmap img = null;
        Bitmap img11 = null;
        Bitmap img22 = null;
        for (Method method : methods) {
            if (method.getName().equals("openFile")) {
                method.invoke(encrypt);
                break;
            }
        }
        for (Method method : methods) {
            if (method.getName().equals("encrypt")) {
                method.invoke(encrypt);
                break;
            }
        }
        for (Method method : methods) {
            if (method.getName().equals("saveFile")) {
                method.invoke(encrypt);
                break;
            }
        }
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath1 = cachePath + File.separator + "Disk1" + File.separator + "jpg_medium.jpg" + ".ori";
        String savePath2 = cachePath + File.separator + "Disk2" + File.separator + "jpg_medium.jpg" + ".ori";
        String savePath = context.getDataDir().getAbsolutePath() + File.separator + "overflow" + File.separator + "jpg_medium.jpg";
        savePath1 += ".webp";
        savePath2 += ".webp";
        File file1 = new File(savePath1);
        File file2 = new File(savePath2);
        File file = new File(savePath);
        Assert.assertTrue("保存文件失败！", file.exists());
        Assert.assertTrue("保存文件失败！", file1.exists());
        Assert.assertTrue("保存文件失败！", file2.exists());
        img11 = Utils.openImg(savePath1);
        img22 = Utils.openImg(savePath2);
        Assert.assertNotNull(img11);
        Assert.assertNotNull(img22);
        for (Field field : fields) {
            if (field.getName().equals("img")) {
                img = (Bitmap) field.get(encrypt);
                break;
            }
        }
        for (int i = 0; i < 500; i++) {
            int row = (int) (Math.random() * img.getHeight());
            int col = (int) (Math.random() * img.getWidth());
            int pixel = img.getPixel(col, row);
            int pixel1 = img11.getPixel(col, row);
            int pixel2 = img22.getPixel(col, row);
            Assert.assertEquals("加密算法错误！(R)row：" + row + "col:" + col,
                    Color.red(pixel),
                    (Color.red(pixel2) + Color.red(pixel1)) % 256);
            Assert.assertEquals("加密算法错误！(G)row：" + row + "col:" + col,
                    Color.green(pixel),
                    (Color.green(pixel2) + Color.green(pixel1)) % 256);
            Assert.assertEquals("加密算法错误！(B)row：" + row + "col:" + col,
                    Color.blue(pixel),
                    (Color.blue(pixel2) + Color.blue(pixel1)) % 256);
            if (img.hasAlpha()) {
                Assert.assertEquals("加密算法错误！(A)row：" + row + "col:" + col,
                        Color.alpha(pixel),
                        (Color.alpha(pixel2) + Color.alpha(pixel1)) % 256);
            }
        }
    }

    /**
     * Test encrypt call method.
     * @author Cui Yuxin
     */
    @Test
    public void callTest() throws Exception {
        String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin"
                + File.separator + "jpg_medium.jpg";
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Encrypt encrypt = new Encrypt(imgPath, context);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(16));
        Future<Bitmap[]> results = threadPoolExecutor.submit(encrypt);
        Bitmap[] result = results.get();
        Assert.assertNotNull(result);
        String cachePath = context.getCacheDir().getAbsolutePath();
        String savePath1 = cachePath + File.separator + "Disk1" + File.separator + "jpg_medium.jpg" + ".ori";
        String savePath2 = cachePath + File.separator + "Disk2" + File.separator + "jpg_medium.jpg" + ".ori";
        String savePath = context.getDataDir().getAbsolutePath() + File.separator + "overflow" + File.separator + "jpg_medium.jpg";
        savePath1 += ".webp";
        savePath2 += ".webp";
        File file1 = new File(savePath1);
        File file2 = new File(savePath2);
        File file = new File(savePath);
        Assert.assertTrue("保存文件失败！", file.exists());
        Assert.assertTrue("保存文件失败！", file1.exists());
        Assert.assertTrue("保存文件失败！", file2.exists());
    }

    /**
     * Test encrypt call method.
     * Specific random seed.
     * @author Cui Yuxin
     */
    @Test
    public void randomTest() throws Exception {
        String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin"
                + File.separator + "ZHAO.jpg";
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Encrypt encrypt = new Encrypt(imgPath, context);
        for (Field field : fields) {
            if (field.getName().equals("rnd")) {
                field.set(encrypt, new Random(0));
                break;
            }
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(16));
        Future<Bitmap[]> results = threadPoolExecutor.submit(encrypt);
        Bitmap[] result = results.get();
        String imgPath1 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + "ZHAO.jpg.ori";
        String imgPath2 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk2" + File.separator + "ZHAO.jpg.ori";
        Utils.genThumbnail(result[0], imgPath1, 15);
        Utils.genThumbnail(result[1], imgPath2, 15);
        //String imgPath0 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk2" + File.separator + "thumb";
        //Utils.genThumbnail(Utils.openImg(imgPath), imgPath0, 15);
    }
}
