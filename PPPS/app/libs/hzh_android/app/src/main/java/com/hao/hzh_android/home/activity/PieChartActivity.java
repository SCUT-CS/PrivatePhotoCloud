package com.hao.hzh_android.home.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.utils.ToastUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.PieChartCallback;
import com.hao.hzh_android.home.model.PieChartModel;
import java.util.ArrayList;

/**
 * 饼状图
 * @author WaterWood
 */
public class PieChartActivity extends BaseActivity<PieChartModel> implements PieChartCallback, OnChartValueSelectedListener {

    private PieChart chart;
    protected Typeface tfLight;
    protected Typeface tfRegular;
    protected final String[] parties = new String[] {
            "张", "王", "李", "赵"
    };

    @Override
    protected PieChartModel getModelImp() {
        return new PieChartModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_pie_chart;
    }

    @Override
    protected void initWidget() {
        tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        chart = findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText());
        chart.setCenterTextSize(20);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chart.animateY(1400, Easing.EaseInOutQuad);
        setData(4,100);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);
    }

    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
                    parties[i % parties.length],
                    getResources().getDrawable(R.mipmap.star)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "该死的标注");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.BLACK);
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tfLight);
        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
    }

    private String generateCenterSpannableText() {
        return "今天的天气真不错";
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        //饼图点击事件，提供了点击的是第几个块
        ToastUtil.toastWord(this,"我是"+parties[(int) h.getX()]);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}
