package com.hao.baselib.utils;

import android.widget.TextView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 判空工具类
 * @author WaterWood
 */
public class NullUtil {
    public static boolean isStringEmpty(String str){
        return str==null||"".equals(str)||"null".equalsIgnoreCase(str);
    }

    public static boolean isTextEmpty(TextView textView) {
        return isStringEmpty(textView.getText().toString().trim());
    }

    public static boolean isListEmpty(List list){
        return list==null||list.size()<=0;
    }


    public static boolean isNullOrEmpty(Object obj) {

        if (obj == null)

            return true;

        if (obj instanceof CharSequence)

            return ((CharSequence) obj).length() == 0;

        if (obj instanceof Collection)

            return ((Collection) obj).isEmpty();

        if (obj instanceof Map)

            return ((Map) obj).isEmpty();

        if (obj instanceof Object[]) {

            Object[] object = (Object[]) obj;

            if (object.length == 0) {

                return true;

            }

            boolean empty = true;

            for (int i = 0; i < object.length; i++) {

                if (!isNullOrEmpty(object[i])) {

                    empty = false;

                    break;

                }

            }

            return empty;

        }

        return false;

    }
}
