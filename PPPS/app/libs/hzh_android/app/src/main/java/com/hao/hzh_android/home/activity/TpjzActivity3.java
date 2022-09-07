package com.hao.hzh_android.home.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.baselib.utils.ToastUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.TpjzCallback;
import com.hao.hzh_android.home.callback.TpjzCallback3;
import com.hao.hzh_android.home.model.TpjzModel;
import com.hao.hzh_android.home.model.TpjzModel3;
import com.hao.imageloadbydown.imageload.DownloadImageLoader;
import com.hao.imageloadbydown.inner.DownloadPicInterface;
import com.hao.imageloadlib.loadimage.GlideImageLoader;

/**
 * 图片加载示例
 * @author WaterWood
 */
public class TpjzActivity3 extends WaterPermissionActivity<TpjzModel3> implements TpjzCallback3 {

    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ImageView iv4;
    private Button bt_clear;
    private Button bt_download;

    @Override
    protected TpjzModel3 getModelImp() {
        return new TpjzModel3(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tpjz3;
    }

    @Override
    protected void initWidget() {
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);
        bt_clear = findViewById(R.id.bt_clear);
        bt_download = findViewById(R.id.bt_download);
        requestPermission(READ_EXTERNAL_STORAGE);
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImageLoader.getInstance().deleteAllPic(TpjzActivity3.this);
            }
        });
        bt_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImageLoader.getInstance().downloadPics(TpjzActivity3.this, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598529925750&di=05" +
                        "6ed264b89ad79ce873e8a46c5c6737&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2" +
                        "Fimages%2F20180204%2Fc42bc2c821724c86ad223ed4000d3da4.jpeg", new DownloadPicInterface() {
                    @Override
                    public void success(String path) {
                        TpjzActivity3.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastWord(TpjzActivity3.this,"下载成功");
                            }
                        });
                    }

                    @Override
                    public void failure() {
                        TpjzActivity3.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.toastWord(TpjzActivity3.this,"下载失败");
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void doSDRead() {
        requestPermission(WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void doSDWrite() {
        DownloadImageLoader.getInstance()
                .loadImage(this
                        ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598529925750&di=" +
                                "f1120431e7e086309e4040fd3a61862f&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.co" +
                                "m%2Fq_70%2Cc_zoom%2Cw_640%2Fimages%2F20180809%2F77ab0f4c71e644ddb57448dac2d36713.jpg"
                        ,R.mipmap.ic_about
                        ,R.mipmap.ic_about
                        ,iv1);
        DownloadImageLoader.getInstance()
                .loadCornerImage(this
                        ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598529925750&di=05" +
                                "6ed264b89ad79ce873e8a46c5c6737&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2" +
                                "Fimages%2F20180204%2Fc42bc2c821724c86ad223ed4000d3da4.jpeg"
                        ,20
                        ,iv2);
        DownloadImageLoader.getInstance()
                .loadRoundImage(this
                        ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598530003007&di" +
                                "=7c90c734c742aa50cbf2041b5e8eb730&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs." +
                                "com%2Fimages%2F20181024%2F7a6eeefa8bb8431e8ebed8c8ddb4c6b5.jpeg"
                        ,iv3);
        DownloadImageLoader.getInstance()
                .loadImage(this
                        ,R.mipmap.aaaaa
                        ,R.mipmap.ic_about
                        ,R.mipmap.ic_about
                        ,iv4);
    }
}
