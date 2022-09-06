package com.hao.hzh_android.home.activity;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.TpjzCallback2;
import com.hao.hzh_android.home.model.TpjzModel2;
import com.hao.uil_imageload.inner.FailReasonBean;
import com.hao.uil_imageload.inner.UILImageLoadInfo;
import com.hao.uil_imageload.inner.UILImageLoadListener;
import com.hao.uil_imageload.loadimage.UILImageLoadManager;

/**
 * 图片加载示例
 * @author WaterWood
 */
public class TpjzActivity2 extends BaseActivity<TpjzModel2> implements TpjzCallback2 {

    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;

    @Override
    protected TpjzModel2 getModelImp() {
        return new TpjzModel2(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tpjz2;
    }

    @Override
    protected void initWidget() {
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        UILImageLoadManager.getInstance(this).displayImage(iv1
                ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999" +
                "_10000&sec=1598242730783&di=6a429ba4044557d8cc3546245124e0" +
                "35&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F201" +
                "61229%2F20161229104552_78541dd68e6709a00941eedb4f67073d_6." +
                "jpeg");
        UILImageLoadManager.getInstance(this).displayImage(iv2
                ,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999" +
                        "_10000&sec=1598242730783&di=6a429ba4044557d8cc3546245124e0" +
                        "35&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F201" +
                        "61229%2F20161229104552_78541dd68e6709a00941eedb4f67073d_6." +
                        "jpeg");
        UILImageLoadManager.getInstance(this).displayImage(iv3
                , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999" +
                        "_10000&sec=1598242730783&di=6a429ba4044557d8cc3546245124e0" +
                        "35&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F201" +
                        "61229%2F20161229104552_78541dd68e6709a00941eedb4f67073d_6." +
                        "jpeg");
    }
}
