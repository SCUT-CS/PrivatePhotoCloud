package cn.edu.scut.ppps;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit Tests
 * @author Cui Yuxin
 */
@RunWith(AndroidJUnit4.class)
public class test {

    /**
     * Grant permissions.
     * AndroidManifest.xml中需要声明该权限，手机需要允许USB调试修改权限。
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Test ***.
     * @author Cui Yuxin
     */
    @Test
    public void environmentTest() {

    }

}


