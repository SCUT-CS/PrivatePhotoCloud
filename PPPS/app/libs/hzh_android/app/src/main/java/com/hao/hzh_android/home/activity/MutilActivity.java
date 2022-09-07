package com.hao.hzh_android.home.activity;

import android.widget.ListView;

import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.adapter.CourseAdapter;
import com.hao.hzh_android.home.callback.MutilCallback;
import com.hao.hzh_android.home.model.MutilModel;

import java.util.ArrayList;

/**
 * 多布局页面
 * @author WaterWood
 */
public class MutilActivity extends BaseActivity<MutilModel> implements MutilCallback {

    private ListView listView;

    @Override
    protected MutilModel getModelImp() {
        return new MutilModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_mutil;
    }

    @Override
    protected void initWidget() {
        listView = findViewById(R.id.listView);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("");
        }
        CourseAdapter courseAdapter = new CourseAdapter(this,list);
        listView.setAdapter(courseAdapter);
    }
}
