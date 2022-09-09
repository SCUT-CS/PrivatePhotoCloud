package cn.edu.scut.ppps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * Decrypt Unit Tests
 * @author Feng Yucheng
 */
@RunWith(AndroidJUnit4.class)
public class AlgorithmTest{
    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Test Algorithm Validity.
     * @author Cui Yuxin
     */
    @Test
    public void test() throws IOException {
        // 照片路径
        String imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin"
                + File.separator + "jpg_medium.jpg";
        // 储存路径
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String cachePath = context.getCacheDir().getAbsolutePath();
        String Path1 = cachePath + File.separator + "Disk1" + File.separator + "jpg_medium.jpg" + ".ori";
        String Path2 = cachePath + File.separator + "Disk2" + File.separator + "jpg_medium.jpg" + ".ori";
        Bitmap img = null;
        Bitmap originalImg = null;
        Encrypt encrypt = new Encrypt(imgPath,context);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(16));
        Future<Bitmap[]> results = threadPoolExecutor.submit(encrypt);
        File file1 = new File(Path1+=".jpg");
        File file2 = new File(Path2+=".jpg");
        Decrypt decrypt = new Decrypt(Path1,Path1,context);
        ThreadPoolExecutor threadPoolExecutor2 = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(16));
        Future<Bitmap> results2 = threadPoolExecutor2.submit(decrypt);
        img = results.get();
        String imgPath3 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + "WeiXin"
                + File.separator + "ZHAO.jpg";
        originalImg = Utils.openImg(imgPath);
    }
}
