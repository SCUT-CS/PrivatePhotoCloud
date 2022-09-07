package com.hao.hzh_android.home.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.SyOneCallback;
import com.hao.hzh_android.home.model.SyOneModel;
import com.hao.web_lib.WaterWebViewFragment;

/**
 * 首页四个内嵌
 * @author WaterWood
 */
public class WebFragment extends WaterWebViewFragment<SyOneModel> implements SyOneCallback {

    private FrameLayout fr_root;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_web_jd;
    }

    @Override
    protected SyOneModel getModelImp() {
        return new SyOneModel(activity,this);
    }

    @Override
    protected ViewGroup parentView() {
        return fr_root;
    }

    @Override
    protected String webUrl() {
        return "https://www.jd.com/";
    }

    @Override
    protected void initWidget(View root) {
        fr_root = root.findViewById(R.id.fr_root);
        super.initWidget(root);//这个一定要调用，并要在初始化控件之后，其他逻辑可以放到它之后
    }
}
