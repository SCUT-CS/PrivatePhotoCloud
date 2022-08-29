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

/**
 * Encrypt Unit Tests
 *
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
    public void setUp() {
        // 构造函数参数
        String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin"
                + File.separator + "jpg_medium.jpg";
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // 通过反射创造Encrypt类
        Class encryptClass = null;
        try {
            encryptClass = Class.forName("cn.edu.scut.ppps.Encrypt");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Constructor constructor = null;
        try {
            constructor = encryptClass.getConstructor(String.class, Context.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Assert.fail();
        }
        try {
            encrypt = (Encrypt) constructor.newInstance(imgPath, appContext);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        // 获取类内部变量和方法
        try {
            fields = encryptClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        try {
            methods = encryptClass.getDeclaredMethods();
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
                if (field.getName().equals("filePath")) {
                    Assert.assertNotNull(field.get(encrypt));
                } else if (field.getName().equals("context")) {
                    Assert.assertNotNull(field.get(encrypt));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
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
    public void openFileTest() {
        Bitmap img = null;
        for (Method method : methods) {
            if (method.getName().equals("openFile")) {
                try {
                    method.invoke(encrypt);
                    for (Field field : fields) {
                        if (field.getName().equals("img")) {
                            Assert.assertNotNull(field.get(encrypt));
                            img = (Bitmap) field.get(encrypt);
                        } else if (field.getName().equals("fileName")) {
                            Assert.assertNotNull(field.get(encrypt));
                            Assert.assertEquals("文件名获取失败！", field.get(encrypt), "jpg_medium.jpg");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                break;
            }
            Assert.assertNotNull(img);
            Assert.assertEquals("图片获取失败！", 4512, img.getWidth());
            Assert.assertEquals("图片获取失败！", 6016, img.getHeight());
        }
    }

    /**
     * Test encrypt encryptFile method.
     * @author Cui Yuxin
     */
    @Test
    public void encryptTest() {
        Bitmap img1 = null;
        Bitmap img2 = null;
        Bitmap img = null;
        for (Method method : methods) {
            if (method.getName().equals("openFile")) {
                try {
                    method.invoke(encrypt);
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                break;
            }
        }
        for (Method method : methods) {
            if (method.getName().equals("encrypt")) {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
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
    public void saveFileTest() {
        for (Method method : methods) {
            if (method.getName().equals("saveFile")) {
                try {
                    method.invoke(encrypt);
                    for (Field field : fields) {
                        // TODO
                        /*if (field.getName().equals("fileName")){
                            Assert.assertNotNull(field.get(encrypt));
                            Assert.assertEquals("文件名获取失败！",field.get(encrypt),"jpg_medium.jpg");
                        }*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                break;
            }
        }
    }

    /**
     * Test encrypt call method.
     * @author Cui Yuxin
     */
    @Test
    public void callTest() {
        // TODO
    }
}
