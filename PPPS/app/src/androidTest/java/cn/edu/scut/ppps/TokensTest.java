package cn.edu.scut.ppps;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
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

    Tokens tokens = null;
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
     * @author Feng Yucheng
     */
    @Before
    public void setUp() {
        //TODO Your Code Here.
        // 完成反射和构造函数的测试
    }

    /**
     * Test saveTokenFile method.
     * @author //TODO YOUR_NAME
     */
    @Test
    public void saveTokenFileTest() {
        //TODO Your Code Here.
        // 使用反射完成saveTokenFile方法的测试
    }

    /**
     * Test loadTokenFile method.
     * @author //TODO YOUR_NAME
     */
    @Test
    public void loadTokenFileTest() {
        //TODO Your Code Here.
        setUp();

    }

    /**
     * Test getToken method.
     * @author Feng Yucheng
     */
    @Test
    public void getTokenTest() {
        // 准备测试环境
        setUp();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokens = null;
        try {
            tokens = new Tokens(appContext);
        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(this.tokens,tokens.getToken("temp"));
    }

    /**
     * Test getNames method.
     * @author Feng Yucheng
     */
    @Test
    public void getNamesTest() throws Exception {
        //准备测试环境
        setUp();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokens = null;
        try {
            tokens = new Tokens(appContext);
        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
        //名字为“temp”
        Assert.assertEquals("temp",tokens.getNames());
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
