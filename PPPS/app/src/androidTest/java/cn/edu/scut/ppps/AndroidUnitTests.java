package cn.edu.scut.ppps;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AndroidUnitTests {
    @Test
    public void timeTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        /*
        Encrypt encrypt = new Encrypt("./images/ZHAO.jpg");
        long startTime = System.currentTimeMillis();
        encrypt.run();
        long endTime = System.currentTimeMillis();
        long usedTime = endTime - startTime;
        System.out.println("demo" + usedTime);*/
    }
}

