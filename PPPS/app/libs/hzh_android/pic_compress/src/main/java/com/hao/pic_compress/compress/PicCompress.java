package com.hao.pic_compress.compress;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 图片压缩
 * @author WaterWood
 */
public class PicCompress {

    private static PicCompress mInstance;

    /**
     * 获取对象实例
     * @return
     */
    public static PicCompress getInstance() {
        if (mInstance == null){
            synchronized (PicCompress.class){
                if (mInstance == null){
                    mInstance = new PicCompress();
                }
            }
        }
        return mInstance;
    }

    /**
     * 私有构造方法
     */
    private PicCompress() {
    }

    /**
     * 图片压缩方法
     * @param context
     * @param picSourcePath
     * @param destinationDirectory
     */
    public void picCompress(Context context, String picSourcePath, String destinationDirectory, final InterCompressPic interCompressPic) {
        Luban.with(context)
                .load(picSourcePath)
                .ignoreBy(100)
                .setTargetDir(destinationDirectory)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        //压缩开始前调用，可以在方法内启动 loading UI
                        interCompressPic.start();
                    }

                    @Override
                    public void onSuccess(File file) {
                        //压缩成功后调用，返回压缩后的图片文件
                        interCompressPic.success(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //当压缩过程出现问题时调用
                        interCompressPic.error();
                    }
                }).launch();
    }
}
