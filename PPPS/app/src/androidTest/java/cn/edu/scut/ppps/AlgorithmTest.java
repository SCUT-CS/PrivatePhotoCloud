package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * Decrypt Unit Tests
 * @author Feng Yucheng , Huang Zixi
 */

@RunWith(AndroidJUnit4.class)
public class AlgorithmTest {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    //原始图片路径
    String imgName = "2022-09-04-10-20-20-918.jpg";
    String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
            + File.separator + "PPPS"
            + File.separator + imgName;
    //加密后的图片路径 除了名字都已经写死了
    String Path1 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk1" + File.separator + imgName + ".ori.webp";
    String Path2 = context.getCacheDir().getAbsolutePath() + File.separator + "Disk2" + File.separator + imgName + ".ori.webp";

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Test Algorithm Validity.
     *
     * @author Feng Yucheng , Huang Zixi
     */
    @Test
    public void test() throws Exception {
        //加密
        Encrypt encrypt = new Encrypt(imgPath, context);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(16));
        Future<Bitmap[]> resultsEncrypt = threadPoolExecutor.submit(encrypt);
        Bitmap[] resultEncrypt = resultsEncrypt.get();
        Assert.assertNotNull(resultEncrypt);
        //解密
        Decrypt decrypt = new Decrypt(Path1, Path2, context);
        ThreadPoolExecutor threadPoolExecutor2 = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(16));
        Future<Bitmap> resultsDecrypt = threadPoolExecutor2.submit(decrypt);
        Bitmap resultDecrypt = resultsDecrypt.get();
        Bitmap originalImg = Utils.openImg(imgPath);
        Assert.assertNotNull(resultDecrypt);
        Assert.assertNotNull(originalImg);
        // 正确性检查
        for (int i = 0; i < 1000; i++) {
            int row = (int) (Math.random() * resultDecrypt.getHeight());
            int col = (int) (Math.random() * resultDecrypt.getWidth());
            int originalPixel = originalImg.getPixel(col, row);
            int pixel = resultDecrypt.getPixel(col, row);
            Assert.assertEquals("加密算法错误！(R)row：" + row + "col:" + col,
                    Color.red(originalPixel), Color.red(pixel));
            Assert.assertEquals("加密算法错误！(G)row：" + row + "col:" + col,
                    Color.green(originalPixel), Color.green(pixel));
            Assert.assertEquals("加密算法错误！(B)row：" + row + "col:" + col,
                    Color.blue(originalPixel), Color.blue(pixel));
            if (resultDecrypt.hasAlpha()) {
                Assert.assertEquals("加密算法错误！(A)row：" + row + "col:" + col,
                        Color.alpha(originalPixel), Color.alpha(pixel));
            }
        }
    }
}
