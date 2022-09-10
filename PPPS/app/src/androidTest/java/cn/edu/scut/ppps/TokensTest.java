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


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.edu.scut.ppps.cloud.Tokens;

/**
 * Tokens Unit Tests
 * @author Huang zixi, Feng Yucheng
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
    public void setUp() throws Exception {
        // 构造函数参数
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // 通过反射创造Tokens类
        Class tokensClass = null;
        tokensClass = Class.forName("cn.edu.scut.ppps.cloud.Tokens");
        Constructor constructor = null;
        constructor = tokensClass.getConstructor(Context.class);
        tokensTest = (Tokens) constructor.newInstance(appContext);
        // 获取类内部变量和方法
        fields = tokensClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        methods = tokensClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
        }
        // 测试构造函数
        for (Field field : fields) {
            if (field.getName().equals("context")) {
                Assert.assertNotNull(field.get(tokensTest));
            } else if (field.getName().equals("tokens")) {
                Assert.assertNotNull(field.get(tokensTest));
            } else if (field.getName().equals("tokensFile")) {
                Assert.assertNotNull(field.get(tokensTest));
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
    }

    /**
     * Test saveTokens method.
     * @author Huang Zixi
     */
    @Test
    public void saveTokensTest() throws Exception {
        // 参数准备
        Map<String, String> token = new HashMap<>();
        token.put("access_token", "123456");
        token.put("refresh_token", "654321");
        Map<String, Map<String, String>> tokensMap = new HashMap<>();
        // 测试saveTokens
        for (Field field : fields) {
            if (field.getName().equals("tokens")) {
                Map<String, Map<String, String>> tokensTemp = (Map<String, Map<String, String>>) field.get(tokensTest);
                tokensMap.put("test", token);
                field.set(tokensTest, tokensMap);
                for (Method method : methods) {
                    if (method.getName().equals("saveTokens")) {
                        method.invoke(tokensTest);
                        Assert.assertEquals("测试saveTokens方法失败！", token, tokensTest.getToken("test"));
                        Assert.assertEquals("测试saveTokens方法失败！", "123456",
                                tokensTest.getToken("test").get("access_token"));
                        Assert.assertEquals("测试updateTokens方法失败！", "654321",
                                tokensTest.getToken("test").get("refresh_token"));
                    }
                }
            }
        }
    }

    /**
     * Test getToken method.
     * @author Feng Yucheng
     */
    @Test
    public void getTokenTest() throws Exception {
        // 准备测试环境
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokens = null;

        tokens = new Tokens(appContext);

        Assert.assertEquals("123456", tokens.getToken("test").get("access_token"));
    }

    /**
     * Test getNames method.
     * @author Feng Yucheng
     */
    @Test
    public void getNamesTest() throws Exception {
        //准备测试环境
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokens = null;
        tokens = new Tokens(appContext);
        //名字为“temp”
        Set<String> expected = new HashSet<>();
        expected.add("test");
        Assert.assertEquals(expected, tokens.getNames());
    }

    /**
     * Test updateToken method.
     * @author Cui Yuxin
     */
    @Test
    public void updateTokenTest() throws Exception {
        // 准备测试环境
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokens = null;
        tokens = new Tokens(appContext);
        Map<String, String> token = new HashMap<>();
        token.put("access_token", "123456");
        token.put("refresh_token", "654321");
        // 调用updateToken方法
        tokens.updateToken("test", token);
        // 测试结果
        Assert.assertEquals("测试updateToken方法失败！", token, tokens.getToken("test"));
        Assert.assertEquals("测试updateToken方法失败！", "123456",
                tokens.getToken("test").get("access_token"));
        Assert.assertEquals("测试updateToken方法失败！", "654321",
                tokens.getToken("test").get("refresh_token"));
    }
}
