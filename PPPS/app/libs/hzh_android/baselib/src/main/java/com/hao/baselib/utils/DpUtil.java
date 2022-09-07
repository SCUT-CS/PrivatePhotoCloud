package com.hao.baselib.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * 像素转换工具类
 * @author WaterWood
 */
public class DpUtil {
    /**
     * dp转px
     *
     * @param context
     * @param dpval
     * @return
     */
    public static int dp2px(Context context, float dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpval, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }
}
