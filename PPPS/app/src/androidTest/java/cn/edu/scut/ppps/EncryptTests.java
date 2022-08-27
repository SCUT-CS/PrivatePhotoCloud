package cn.edu.scut.ppps;

import android.content.Context;
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
import java.lang.reflect.Method;

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
            encrypt = (Encrypt) constructor.newInstance(imgPath,appContext);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        // 获取类内部变量和方法
        try {
            fields = encryptClass.getFields();
            for (Field field : fields) {
                field.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        try {
            methods = encryptClass.getMethods();
            for (Method method : methods) {
                method.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        // 测试构造函数
        try{
            for (Field field : fields){
                if (field.getName().equals("filePath")){
                    Assert.assertNotNull(field.get(encrypt));
                } else if (field.getName().equals("context")){
                    Assert.assertNotNull(field.get(encrypt));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Test encrypt openFile method.
     * @author Cui Yuxin
     */
    @Test
    public void openFileTest() {
        for (Method method : methods){
            if (method.getName().equals("openFile")){
                try {
                    method.invoke(encrypt);
                    for (Field field : fields){
                        if (field.getName().equals("img")){
                            Assert.assertNotNull(field.get(encrypt));
                        } else if (field.getName().equals("fileName")){
                            Assert.assertNotNull(field.get(encrypt));
                            Assert.assertEquals("文件名获取失败！",field.get(encrypt),"jpg_medium.jpg");
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
