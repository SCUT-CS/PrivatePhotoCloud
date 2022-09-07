package com.hao.hzh_android.home.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.ChartCallback;
import com.hao.hzh_android.home.callback.PieChartCallback;
import com.hao.hzh_android.home.model.ChartModel;

/**
 * 图表举例，该功能使用首先引入相关依赖，请参考build.gradle有注释
 * @author WaterWood
 */
public class ChartActivity extends BaseActivity<ChartModel> implements ChartCallback, View.OnClickListener {

    private Button bt_zx;
    private Button bt_zz;
    private Button bt_bzt;

    @Override
    protected ChartModel getModelImp() {
        return new ChartModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_chart;
    }

    @Override
    protected void initWidget() {
        bt_zx = findViewById(R.id.bt_zx);
        bt_zz = findViewById(R.id.bt_zz);
        bt_bzt = findViewById(R.id.bt_bzt);
        bt_zx.setOnClickListener(this);
        bt_zz.setOnClickListener(this);
        bt_bzt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.bt_zx:
                //折线图
                intent = new Intent(this, LineChartActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_zz:
                //柱状图
                intent = new Intent(this, BarChartActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_bzt:
                //饼状图
                intent = new Intent(this, PieChartActivity.class);
                startActivity(intent);
                break;
        }
    }
}
