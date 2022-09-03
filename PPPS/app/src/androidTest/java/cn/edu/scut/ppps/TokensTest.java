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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Tokens Unit Tests
 * @author //TODO YOUR_NAME
 */
@RunWith(AndroidJUnit4.class)
public class TokensTest {

    Tokens tokensTest = null;
    Field[] fields = null;
    Method[] methods = null;

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Prepare the environment for the test.
     * @author Huang zixi
     */
    @Before
        public void setUp() {
        // 构造函数参数
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            // 通过反射创造Tokens类
            Class tokensClass = null;
            try {
                tokensClass = Class.forName("cn.edu.scut.ppps.Tokens");
                Constructor constructor = null;
                constructor = tokensClass.getConstructor(Context.class);
               Tokens tokensTest = (Tokens) constructor.newInstance(appContext);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
            // 获取类内部变量和方法
            try {
                Field [] fields = tokensClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                }
                Method [] methods = tokensClass.getDeclaredMethods();
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
                    if (field.getName().equals("context")) {
                        Assert.assertNotNull(field.get(tokensTest));
                    }else if(field.getName().equals("tokens")){
                        Assert.assertNotNull(field.get(tokensTest));
                    }else if(field.getName().equals("tokensFile")){
                        Assert.assertNotNull(field.get(tokensTest));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        }

    /**
     * Test saveTokens method.
     * @author Huang Zixi//
     */
    @Test
    public void saveTokensTest() {
        //参数准备
        Map<String, String> token = new HashMap<>();
        token.put("access_token", "123456");
        token.put("refresh_token", "654321");
        Map<String, Map<String, String>> tokensMap=new HashMap<>();
        //测试saveTokens
        for(Field field : fields){
            if(field.getName().equals("tokens")){
                try {
                    Object tokensTemp=field.get(tokensTest);
                    tokensMap=(Map<String, Map<String, String>>)tokensTemp;
                    tokensMap.put("test",token);
                    field.set(tokensTest,tokensMap);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                for(Method method : methods){
                    if(method.getName().equals("saveTokens")){
                        try {
                            method.invoke(tokensTest);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Assert.fail();
                        }
                        Assert.assertEquals("测试saveTokens方法失败！", token, tokensTest.getToken("test"));
                        Assert.assertEquals("测试saveTokens方法失败！", "123456",
                                tokensTest.getToken("test").get("access_token"));
                        Assert.assertEquals("测试updateTokens方法失败！", "654321",
                                tokensTest.getToken("test").get("refresh_token"));
                    }
                }
            }
        }
        //TODO Your Code Here.
        // 使用反射完成saveTokenFile方法的测试
    }


    /**
     * Test getToken method.
     * @author //TODO YOUR_NAME
     */
    @Test
    public void getTokenTest() {

        //TODO Your Code Here.
        // 完成getToken方法的测试
    }

    /**
     * Test getNames method.
     * @author //TODO YOUR_NAME
     */
    @Test
    public void getNamesTest() {
        //TODO Your Code Here.
        // 完成getNames方法的测试
    }

    /**
     * Test updateToken method.
     * @author Cui Yuxin
     */
    @Test
    public void updateTokenTest() {
        // 准备测试环境
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokens = null;
        try {
            tokens = new Tokens(appContext);
        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
        Map<String, String> token = new HashMap<>();
        token.put("access_token", "123456");
        token.put("refresh_token", "654321");
        // 调用updateToken方法
        try {
            tokens.updateToken("test", token);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        // 测试结果
        Assert.assertEquals("测试updateToken方法失败！", token, tokens.getToken("test"));
        Assert.assertEquals("测试updateToken方法失败！", "123456",
                tokens.getToken("test").get("access_token"));
        Assert.assertEquals("测试updateToken方法失败！", "654321",
                tokens.getToken("test").get("refresh_token"));
    }

}
