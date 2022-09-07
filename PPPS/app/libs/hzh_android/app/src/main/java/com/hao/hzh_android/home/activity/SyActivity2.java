package com.hao.hzh_android.home.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.widge.NoScrollViewPager;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.SyCallback2;
import com.hao.hzh_android.home.model.SyModel2;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页框架
 * @author WaterWood
 */
public class SyActivity2 extends BaseActivity<SyModel2> implements SyCallback2, View.OnClickListener {

    /**
     * 所有的Fragment对象
     */
    private SyOneFragment fragmentOne;
    private SyOneFragment fragmentTwo;
    private SyOneFragment fragmentThree;
    private SyOneFragment fragmentFour;
    /**
     * 页面构建相关
     */
    private FragmentManager fm;
    private NoScrollViewPager frame_room;
    private List<Fragment> listFragment;
    private FragmentPagerAdapter mAdapter;
    /**
     * 底部按钮
     */
    private Button bt_sy;
    private Button bt_fx;
    private Button bt_xx;
    private Button bt_wd;

    @Override
    protected SyModel2 getModelImp() {
        return new SyModel2(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_sy2;
    }

    @Override
    protected void initWidget() {
        frame_room = findViewById(R.id.frame_room);
        bt_sy = findViewById(R.id.bt_sy);
        bt_fx = findViewById(R.id.bt_fx);
        bt_xx = findViewById(R.id.bt_xx);
        bt_wd = findViewById(R.id.bt_wd);
        bt_sy.setOnClickListener(this);
        bt_fx.setOnClickListener(this);
        bt_xx.setOnClickListener(this);
        bt_wd.setOnClickListener(this);
        //准备Fragment的list
        listFragment = new ArrayList<>();
        fragmentOne = new SyOneFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("flag",1);
        fragmentOne.setArguments(bundle1);
        listFragment.add(fragmentOne);
        fragmentTwo = new SyOneFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("flag",2);
        fragmentTwo.setArguments(bundle2);
        listFragment.add(fragmentTwo);
        fragmentThree = new SyOneFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("flag",3);
        fragmentThree.setArguments(bundle3);
        listFragment.add(fragmentThree);
        fragmentFour = new SyOneFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putInt("flag",4);
        fragmentFour.setArguments(bundle4);
        listFragment.add(fragmentFour);
        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return listFragment.get(position);
            }

            @Override
            public int getCount() {
                return listFragment.size();
            }
        };
        //设置适配器
        frame_room.setAdapter(mAdapter);
        //ViewPager切换监听
        frame_room.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_sy:
                frame_room.setCurrentItem(0, false);
                break;
            case R.id.bt_fx:
                frame_room.setCurrentItem(1, false);
                break;
            case R.id.bt_xx:
                frame_room.setCurrentItem(2, false);
                break;
            case R.id.bt_wd:
                frame_room.setCurrentItem(3, false);
                break;
        }
    }
}
