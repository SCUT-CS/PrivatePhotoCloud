package com.hao.baselib.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathGetUtil {

    /**
     * 根据多个短名称组成外存储器的路径
     * @param shortPathList
     */
    public static String getPath(List<String> shortPathList){
        StringBuilder stringBuilder = new StringBuilder(Environment.getExternalStorageDirectory() + File.separator);
        for (int i = 0; i < shortPathList.size(); i++) {
            stringBuilder.append(shortPathList.get(i));
            if (i!=shortPathList.size()-1){
                //不是最后一个
                stringBuilder.append(File.separator);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 龙为公司专用路径获取
     * @param shortPathList
     * @return
     */
    public static String getLongwayPath(Context context,List<String> shortPathList){
        List<String> list = new ArrayList<>();
        list.add("longway");
        list.add(context.getPackageName());
        list.addAll(shortPathList);
        return getPath(list);
    }
}
