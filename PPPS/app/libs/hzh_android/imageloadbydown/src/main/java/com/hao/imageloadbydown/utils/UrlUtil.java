package com.hao.imageloadbydown.utils;

import androidx.annotation.NonNull;

/**
 * 获取url的文件名
 */
public class UrlUtil {
    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    @NonNull
    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
