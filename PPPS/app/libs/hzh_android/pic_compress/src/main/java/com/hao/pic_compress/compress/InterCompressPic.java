package com.hao.pic_compress.compress;

import java.io.File;

/**
 * 图片压缩回调接口
 * @author WaterWood
 */
public interface InterCompressPic {
    public void start();
    public void success(File file);
    public void error();
}
