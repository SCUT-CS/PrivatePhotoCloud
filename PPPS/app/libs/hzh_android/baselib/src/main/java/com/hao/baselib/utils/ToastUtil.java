package com.hao.baselib.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司工具类
 * @author WaterWood
 */
public class ToastUtil {
    /**
     * 吐司
     * @param context
     * @param str
     */
    public static void toastWord(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
