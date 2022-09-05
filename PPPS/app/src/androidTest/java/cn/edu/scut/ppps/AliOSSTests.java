package cn.edu.scut.ppps;

import android.content.Context;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    public String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "WeiXin";
    public final File weiXinPictureDir = new File(imgFileDir);

    /**
     * Test AliOSS upload method.
     *
     * @author Feng Yucheng
     */
    @Test
    public void uploadTest() throws FileNotFoundException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokens = null;
        try {
            tokens = new Tokens(appContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> token = new HashMap<>();
        token.put("access_token", "123456");
        token.put("refresh_token", "654321");
        Map<String, Map<String, String>> tokensMap = new HashMap<>();
        tokensMap.put("test", token);
        AliOSS aliOSS = new AliOSS("test", appContext, tokens);
        aliOSS.upload(imgFileDir);
    }

    public byte[] picture_to_byteArray(String picturePath) {
        File file = new File(String.valueOf(weiXinPictureDir));
        byte[] ds = null;
        InputStream zp = null;
        ByteArrayOutputStream boos = null;
        boos = new ByteArrayOutputStream();
        try {
            zp = new FileInputStream(file);
            //1024表示1k为一段
            byte[] frush = new byte[1024];
            int len = -1;
            while ((len = zp.read(frush)) != -1) {
            //写出到字节数组中
                boos.write(frush, 0, len);
            }
            boos.flush();
            return boos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zp != null) {
                try {
                    zp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Test AliOSS upload method.
     * @author Feng Yucheng
     */
     public void uploadTest2() {
         Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
         Tokens tokens = null;
         try {
             tokens = new Tokens(appContext);
         } catch (Exception e) {
             e.printStackTrace();
         }
         Map<String, String> token = new HashMap<>();
         token.put("access_token", "123456");
         token.put("refresh_token", "654321");
         Map<String, Map<String, String>> tokensMap = new HashMap<>();
         tokensMap.put("test", token);
         AliOSS aliOSS = new AliOSS("test", appContext, tokens);
         byte[] file = picture_to_byteArray(imgFileDir);
         aliOSS.upload(file,imgFileDir);
 }

    /**
     * Test AliOSS download method.
     * @author Huang Zixi
     */
    @Test
    public void downloadTest() {
        //参数准备
        String imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin";


    }

}
