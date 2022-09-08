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
    public String imgFileDir = null;
    public final File weiXinPictureDir = null;
    public String downloadImgFileDir = null;

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
        //文件路径
        imgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin";
        File weiXinPictureDir = new File(imgFileDir);
        downloadImgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "PPPS";
        //构造参数
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Tokens tokenMaps = new Tokens(appContext);
        Map<String, String> token_1 = new HashMap<>();
        //阿里云token赋值
        token_1.put("type", "aliyun");
        token_1.put("accessID", "LTAI5t9Wx9ZwYxCuPEGoxoct");
        token_1.put("accessSecret", "IJWyl2xxwYC1vwaTkw8mZ4hWnKZXxP");
        token_1.put("endpoint", "oss-cn-hangzhou.aliyuncs.com");
        token_1.put("bucketName", "ppps1.oss-cn-hangzhou.aliyuncs.com ");
        token_1.put("filePath", "test/");
        tokenMaps.updateToken("test", token_1);
        //构造函数
        AliOSSTests = new AliOSS("test", appContext, tokenMaps);
    }


    /**
     * Test AliOSS upload method.
     *
     * @author Feng Yucheng
     */
    @Test
    public void uploadTest() throws FileNotFoundException {
        AliOSSTests.upload(imgFileDir);
    }

    public byte[] picture_to_byteArray(String picturePath) {
        File file = new File(String.valueOf(imgFileDir));
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
         byte[] file = picture_to_byteArray(imgFileDir);
         AliOSSTests.upload(file,imgFileDir);
         
 }

    /**
     * Test AliOSS download method.
     * @author Huang Zixi
     */
    @Test
    public void downloadTest() throws Exception{
        //测试
        boolean isDownload = AliOSSTests.download("图片2.png",downloadImgFileDir);
        System.out.println(isDownload);
    }

    /**
     * Test AliOSS getThumbnail method.
     * @author Huang Zixi
     */
    @Test
    public void getThumbnailTest() throws Exception{
        boolean isDownload = AliOSSTests.getThumbnail("testImg",downloadImgFileDir);
        System.out.println(isDownload);
    }

    /**
     * Test AliOSS getFileList method.
     * @author Feng yucheng
     */
    @Test
    public void getFileListTest() throws Exception{

    }

    /**
     * Test AliOSS delete method.
     * boolean delete (String fileName)
     * @author Huang Zixi
     */
    @Test
    public void deleteTest_String() throws Exception{
        String fileName = "testImg";
        boolean isDelete = AliOSSTests.delete(fileName);
        System.out.println(isDelete);
    }

    /**
     * Test AliOSS deleteAll method.
     * @author Huang Zixi
     */
    @Test
    public void deleteAllTest() throws Exception{
        boolean isDelete = AliOSSTests.deleteAll();
        System.out.println(isDelete);
    }

    /**
     * Test AliOSS delete method.
     * boolean delete (List<String> fileNames)
     * @author Huang Zixi
     */
    @Test
    public void deleteTest_List() throws Exception{

    }



}



