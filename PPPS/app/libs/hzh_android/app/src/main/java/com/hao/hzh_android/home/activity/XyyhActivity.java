package com.hao.hzh_android.home.activity;

import android.text.Layout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.XyyhCallback;
import com.hao.hzh_android.home.model.XyyhModel;
import com.hao.hzh_android.widge.XyyhHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * 兴业银行轮播图
 */
public class XyyhActivity extends BaseActivity<XyyhModel> implements XyyhCallback {

    private XyyhHeader header;

    @Override
    protected XyyhModel getModelImp() {
        return new XyyhModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_xyyh;
    }

    @Override
    protected void initWidget() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        header = findViewById(R.id.header);
        List<View> listView = new ArrayList<>();
        TextView textView1 = new TextView(this);
        textView1.setText("第一张");
        textView1.setBackgroundColor(0xffff0000);
        textView1.setLayoutParams(params);
        listView.add(textView1);
        TextView textView2 = new TextView(this);
        textView2.setText("第二张");
        textView2.setBackgroundColor(0xff00ff00);
        textView2.setLayoutParams(params);
        listView.add(textView2);
        TextView textView3 = new TextView(this);
        textView3.setText("第三张");
        textView3.setBackgroundColor(0xff0000ff);
        textView3.setLayoutParams(params);
        listView.add(textView3);
        header.setView(listView);
    }
}
