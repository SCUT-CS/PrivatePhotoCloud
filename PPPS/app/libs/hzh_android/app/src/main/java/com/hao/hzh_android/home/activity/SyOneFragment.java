package com.hao.hzh_android.home.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.hao.baselib.base.BaseFragment;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.SyOneCallback;
import com.hao.hzh_android.home.model.SyOneModel;

import butterknife.BindView;

/**
 * 首页四个内嵌
 * @author WaterWood
 */
public class SyOneFragment extends BaseFragment<SyOneModel> implements SyOneCallback {

    @BindView(R.id.tv_one)
    TextView tv_one;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_sy_one;
    }

    @Override
    protected SyOneModel getModelImp() {
        return new SyOneModel(activity,this);
    }

    @Override
    protected void initWidget(View root) {
        tv_one = root.findViewById(R.id.tv_one);
        Bundle bundle = getArguments();
        int flag = bundle.getInt("flag");
        switch (flag){
            case 1:
                tv_one.setText("第一页");
                break;
            case 2:
                tv_one.setText("第二页");
                break;
            case 3:
                tv_one.setText("第三页");
                break;
            case 4:
                tv_one.setText("第四页");
                break;
        }
    }
}
