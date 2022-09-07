package com.hao.hzh_android.home.activity;

import android.os.Handler;
import android.view.View;
import androidx.viewpager.widget.ViewPager;
import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.adapter.HotSalePagerAdapter;
import com.hao.hzh_android.home.callback.LbCallback;
import com.hao.hzh_android.home.model.LbModel;
import java.util.ArrayList;

/**
 * 轮播图
 *
 * @author WaterWood
 */
public class LbActivity extends BaseActivity<LbModel> implements LbCallback {

    private ViewPager mViewPager;
    private HotSalePagerAdapter hotSalePagerAdapter;
    private View view_one;
    private View view_two;
    private View view_three;
    //定时用的
    private Handler mHandler;
    private int currentPoint = 3 * 100;

    @Override
    protected LbModel getModelImp() {
        return new LbModel(this, this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lb;
    }

    @Override
    protected void initWidget() {
        //初始化控件
        mViewPager = findViewById(R.id.viewpager);
        view_one = findViewById(R.id.view_one);
        view_two = findViewById(R.id.view_two);
        view_three = findViewById(R.id.view_three);
        mViewPager.setPageMargin(24);
        //设置适配器
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add("");
        }
        hotSalePagerAdapter = new HotSalePagerAdapter(this, list);
        mViewPager.setAdapter(hotSalePagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPoint = position+1;
                if (position % 3 == 0) {
                    view_one.setBackgroundResource(R.drawable.view_white_circle);
                    view_two.setBackgroundResource(R.drawable.view_gray_circle);
                    view_three.setBackgroundResource(R.drawable.view_gray_circle);
                } else if (position % 3 == 1) {
                    view_one.setBackgroundResource(R.drawable.view_gray_circle);
                    view_two.setBackgroundResource(R.drawable.view_white_circle);
                    view_three.setBackgroundResource(R.drawable.view_gray_circle);
                } else if (position % 3 == 2) {
                    view_one.setBackgroundResource(R.drawable.view_gray_circle);
                    view_two.setBackgroundResource(R.drawable.view_gray_circle);
                    view_three.setBackgroundResource(R.drawable.view_white_circle);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(currentPoint);
        mHandler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(currentPoint);
                //每隔10分钟循环执行run方法
                mHandler.postDelayed(this, 3000);
            }
        };
        //主线程中调用：
        mHandler.postDelayed(r, 3000);//延时100毫秒
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(this);
        }
    }
}
