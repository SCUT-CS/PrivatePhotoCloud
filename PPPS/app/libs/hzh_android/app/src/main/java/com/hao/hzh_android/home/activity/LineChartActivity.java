package com.hao.hzh_android.home.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.LineChartCallback;
import com.hao.hzh_android.home.entity.MyMarkerView;
import com.hao.hzh_android.home.model.LineChartModel;
import java.util.ArrayList;

/**
 * 折线图举例
 * @author WaterWood
 */
public class LineChartActivity extends WaterPermissionActivity<LineChartModel> implements OnChartValueSelectedListener, LineChartCallback {

    private LineChart chart;
    protected Typeface tfRegular;
    protected final String[] months = new String[] {
            "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"
    };

    @Override
    protected LineChartModel getModelImp() {
        return new LineChartModel(this, this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_line_chart;
    }

    @Override
    protected void initWidget() {
        tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        {
            chart = findViewById(R.id.chart1);
            chart.setBackgroundColor(Color.WHITE);
            chart.getDescription().setEnabled(false);
            chart.setTouchEnabled(true);
            chart.setOnChartValueSelectedListener(this);
            chart.setDrawGridBackground(false);
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
            mv.setChartView(chart);
            chart.setMarker(mv);
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            chart.setPinchZoom(true);
            chart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                }
            });
        }
        XAxis xAxis;
        {
            xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setAxisMinimum(0f);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return months[(int) value % months.length];
                }
            });
        }
        YAxis yAxis;
        {
            yAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            yAxis.enableGridDashedLine(10f, 10f, 0f);
            yAxis.setAxisMaximum(200);
            yAxis.setAxisMinimum(-50);
        }
        {
            LimitLine llXAxis = new LimitLine(9f, "Index 10");
            llXAxis.setLineWidth(4f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);
            llXAxis.setTypeface(tfRegular);
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);
        }
        setData(12, 180);
        chart.animateX(1500);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setData(int count, float range) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(i, val, getResources().getDrawable(R.mipmap.star)));
        }
        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "DataSet 1");
            set1.setDrawIcons(false);
            set1.setColor(Color.BLUE);
            set1.setCircleColor(Color.RED);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setFormLineWidth(1f);
            set1.setFormSize(15.f);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });
            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOW HIGH", "low: " + chart.getLowestVisibleX() + ", high: " + chart.getHighestVisibleX());
        Log.i("MIN MAX", "xMin: " + chart.getXChartMin() + ", xMax: " + chart.getXChartMax() + ", yMin: " + chart.getYChartMin() + ", yMax: " + chart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
