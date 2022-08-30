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
 * Decrypt Unit Tests
 * @author Cui Yuxin
 */
@RunWith(AndroidJUnit4.class)
public class DecryptTests {

    Decrypt decrypt = null;
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
        String imgPath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin"
                + File.separator + "encrypted1.png";
        String imgPath2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin"
                + File.separator + "encrypted2.png";
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // 通过反射创造Encrypt类
        Class decryptClass = null;
        try {
            decryptClass = Class.forName("cn.edu.scut.ppps.Decrypt");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Constructor constructor = null;
        try {
            constructor = decryptClass.getConstructor(String.class, String.class, Context.class, boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Assert.fail();
        }
        try {
            decrypt = (Decrypt) constructor.newInstance(imgPath1, imgPath2, appContext, false);
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
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        try {
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
                    Assert.assertEquals(appContext, field.get(decrypt));
                } else if (field.getName().equals("isThumbnail")) {
                    Assert.assertEquals(false, field.get(decrypt));
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
            Log.d("DecryptTests", field.getName());
        }
        for (Method method : methods) {
            Log.d("DecryptTests", method.getName());
        }
    }

    /**
     * Test decrypt openFile method.
     * @author Cui Yuxin
     */
    @Test
    public void openFileTest() {
        for (Method method : methods) {
            if (method.getName().equals("openFile")) {
                try {
                    method.invoke(decrypt);
                    for (Field field : fields) {
                        if (field.getName().equals("img1")) {
                            Bitmap img = (Bitmap) field.get(decrypt);
                            Assert.assertNotNull(img);
                            Assert.assertEquals("图片获取失败！", 450, img.getWidth());
                            Assert.assertEquals("图片获取失败！", 450, img.getHeight());
                        } else if (field.getName().equals("img2")) {
                            Bitmap img = (Bitmap) field.get(decrypt);
                            Assert.assertNotNull(img);
                            Assert.assertEquals("图片获取失败！", 450, img.getWidth());
                            Assert.assertEquals("图片获取失败！", 450, img.getHeight());
                        }
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
     * Test decrypt initialize method.
     * @author Cui Yuxin
     */
    @Test
    public void initializeTest() {
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
                    for (Field field : fields) {
                        if (field.getName().equals("width")) {
                            Assert.assertEquals("初始化失败！", 450, (int) field.get(decrypt));
                        } else if (field.getName().equals("height")) {
                            Assert.assertEquals("初始化失败！", 450, (int) field.get(decrypt));
                        } else if (field.getName().equals("img")) {
                            Bitmap img = (Bitmap) field.get(decrypt);
                            Assert.assertNotNull(img);
                            Assert.assertEquals("初始化失败！", 450, img.getWidth());
                            Assert.assertEquals("初始化失败！", 450, img.getHeight());
                        }
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
     * Test decrypt decrypt method.
     * @author Cui Yuxin
     */
    @Test
    public void decryptTest() {
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
        for (Method method : methods) {
            if (method.getName().equals("decrypt")) {
                try {
                    method.invoke(decrypt);
                    for (Field field : fields) {
                        if (field.getName().equals("img")) {
                            String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                    + File.separator + "WeiXin"
                                    + File.separator + "ZHAO.jpg";
                            Bitmap originalImg = Utils.openImg(imgPath);
                            Bitmap img = (Bitmap) field.get(decrypt);
                            Assert.assertNotNull(img);
                            // 解密算法正确性检查
                            for (int i = 0; i < 1000; i++) {
                                int row = (int) (Math.random() * img.getHeight());
                                int col = (int) (Math.random() * img.getWidth());
                                int originalPixel = originalImg.getPixel(col, row);
                                int pixel = img.getPixel(col, row);
                                Assert.assertEquals("加密算法错误！(R)row：" + row + "col:" + col,
                                        Color.red(originalPixel), Color.red(pixel));
                                Assert.assertEquals("加密算法错误！(G)row：" + row + "col:" + col,
                                        Color.green(originalPixel), Color.green(pixel));
                                Assert.assertEquals("加密算法错误！(B)row：" + row + "col:" + col,
                                        Color.blue(originalPixel), Color.blue(pixel));
                                if (img.hasAlpha()) {
                                    Assert.assertEquals("加密算法错误！(A)row：" + row + "col:" + col,
                                            Color.alpha(originalPixel),  Color.alpha(pixel));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                break;
            }
        }
    }
}
