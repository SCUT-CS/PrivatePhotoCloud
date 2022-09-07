package com.hao.hzh_android.home.activity;

import android.widget.ImageView;
import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.TpjzCallback;
import com.hao.hzh_android.home.model.TpjzModel;
import com.hao.imageloadlib.loadimage.GlideImageLoader;

/**
 * 图片加载示例
 * @author WaterWood
 */
public class TpjzActivity extends BaseActivity<TpjzModel> implements TpjzCallback {

    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ImageView iv4;

    @Override
    protected TpjzModel getModelImp() {
        return new TpjzModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tpjz;
    }

    @Override
    protected void initWidget() {
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);
        GlideImageLoader.getInstance()
                .loadImage(this
                        ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999" +
                                "_10000&sec=1598242730783&di=6a429ba4044557d8cc3546245124e0" +
                                "35&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F201" +
                                "61229%2F20161229104552_78541dd68e6709a00941eedb4f67073d_6." +
                                "jpeg"
                        ,R.mipmap.ic_about
                        ,R.mipmap.ic_about
                        ,iv1);
        GlideImageLoader.getInstance()
                .loadCornerImage(this
                        ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999" +
                                "_10000&sec=1598242730783&di=6a429ba4044557d8cc3546245124e0" +
                                "35&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F201" +
                                "61229%2F20161229104552_78541dd68e6709a00941eedb4f67073d_6." +
                                "jpeg"
                        ,20
                        ,iv2);
        GlideImageLoader.getInstance()
                .loadRoundImage(this
                        ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999" +
                                "_10000&sec=1598242730783&di=6a429ba4044557d8cc3546245124e0" +
                                "35&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F201" +
                                "61229%2F20161229104552_78541dd68e6709a00941eedb4f67073d_6." +
                                "jpeg"
                        ,iv3);
        GlideImageLoader.getInstance()
                .loadImage(this
                        ,R.mipmap.aaaaa
                        ,R.mipmap.ic_about
                        ,R.mipmap.ic_about
                        ,iv4);
    }
}
