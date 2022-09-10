package cn.edu.scut.ppps;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

import cn.edu.scut.ppps.cloud.AliOSS;
import cn.edu.scut.ppps.cloud.Tokens;

/**
 * AliOSS Unit Tests
 * @author Feng Yucheng, Huang Zixi
 */
@RunWith(AndroidJUnit4.class)
public class AliOSSTests {

    AliOSS aliOSSTests = null;
    public final String downloadImgFileDir = "/storage/emulated/0/Pictures/WeiXin/图片1.png";
    public final String downloadThumbnailDir = "/storage/emulated/0/Pictures/WeiXin/thumbnail.png";
    public final String filePath_test = "/storage/emulated/0/Pictures/WeiXin/jpg_small.jpg";

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
        //构造参数
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokenMaps = new Tokens(appContext);
        Map<String, String> token_1 = new HashMap<>();
        //阿里云token赋值
        token_1.put("type", "aliyun");
        token_1.put("accessId", "LTAI5t9Wx9ZwYxCuPEGoxoct");
        token_1.put("accessSecret", "IJWyl2xxwYC1vwaTkw8mZ4hWnKZXxP");
        token_1.put("endpoint", "https://oss-cn-hangzhou.aliyuncs.com");
        token_1.put("bucketName", "ppps1");
        token_1.put("filePath", "test/");
        tokenMaps.updateToken("test", token_1);
        //构造函数
        aliOSSTests = new AliOSS("test", appContext, tokenMaps, new Handler());
    }


    /**
     * Test AliOSS upload method.
     * @author Feng Yucheng, Huang Zixi
     */
    @Test
    public void uploadTest() {
        aliOSSTests.upload(filePath_test);
    }

    /**
     * A helping method to get the byteArray of the image.
     * @author Feng Yucheng
     */
    private byte[] pictureToByteArray(String picturePath) {
        File file = new File(String.valueOf(picturePath));
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
    @Test
     public void uploadTestCaseByteArray() throws Exception {
         byte[] file = pictureToByteArray(filePath_test);
         aliOSSTests.upload(file, "图片4.png");
     }

    /**
     * Test AliOSS download method.
     * @author Huang Zixi
     */
    @Test
    public void downloadTest() {
        aliOSSTests.download("图片3.png", "Disk1");
    }

    /**
     * Test AliOSS getThumbnail method.
     * @author Huang Zixi
     */
    @Test
    public void getThumbnailTest() {
        aliOSSTests.getThumbnail("图片2.png", "Disk1Thumbnail");
    }

    /**
     * Test AliOSS getFileList method.
     * @author Feng yucheng
     */
    @Test
    public void getFileListTest() throws Exception{
        List<String> temp = aliOSSTests.getFileList();
        Log.d("AliOSSTest", temp.toString());
    }

    /**
     * Test AliOSS delete method.
     * boolean delete (String fileName)
     * @author Huang Zixi
     */
    @Test
    public void deleteTestCaseString() {
        String fileName = "图片3.png";
        aliOSSTests.delete(fileName);
    }

    /**
     * Test AliOSS deleteAll method.
     * @author Huang Zixi
     */
    @Test
    public void deleteAllTest() throws Exception{
        aliOSSTests.deleteAll();
    }

    /**
     * Test AliOSS delete method.
     * boolean delete (List<String> fileNames)
     * @author Huang Zixi
     */
    @Test
    public void deleteTestCaseList() {
        List<String> fileNames = new ArrayList<>();
        fileNames.add("图片1.png");
        fileNames.add("图片2.png");
        aliOSSTests.delete(fileNames);
    }
}



