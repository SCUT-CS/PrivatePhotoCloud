package com.hao.hzh_android.home.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.WebJdCallback;
import com.hao.hzh_android.home.model.WebJdModel;
import com.hao.web_lib.WaterWebViewActivity;

public class WebJdActivity extends WaterWebViewActivity<WebJdModel> implements WebJdCallback, View.OnClickListener {

    private FrameLayout fr_root;
    private RelativeLayout rl_back;
    private TextView tv_title;

    @Override
    protected ViewGroup parentView() {
        return fr_root;
    }

    @Override
    protected String webUrl() {
        return "https://www.jd.com/";
    }

    @Override
    protected WebJdModel getModelImp() {
        return new WebJdModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_web_jd;
    }

    @Override
    protected void initWidget() {
        fr_root = findViewById(R.id.fr_root);
        rl_back = findViewById(R.id.rl_back);
        tv_title = findViewById(R.id.tv_title);
        rl_back.setOnClickListener(this);
        super.initWidget();//这个一定要调用，并要在初始化控件之后，其他逻辑可以放到它之后
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Override
    protected void finishLoad() {
        tv_title.setText(getWebName());
    }
}
