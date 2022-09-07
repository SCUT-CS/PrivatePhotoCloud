package com.hao.homelib;

import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.widge.NoScrollViewPager;
import com.hao.initlib.ConfigRoute;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

@Route(path = ConfigRoute.HOME_ACTIVITY)
public class HomeActivity extends BaseActivity<HomeModel> implements HomeCallback {

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
    @BindView(R2.id.frame_room)
    NoScrollViewPager frame_room;
    private List<Fragment> listFragment;
    private FragmentPagerAdapter mAdapter;

    @Override
    protected HomeModel getModelImp() {
        return new HomeModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initWidget() {
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

    @OnClick({R2.id.bt_sy,R2.id.bt_fx,R2.id.bt_xx,R2.id.bt_wd})
    void onClick(View v){
        if (v.getId() == R.id.bt_sy){
            frame_room.setCurrentItem(0, false);
        }else if (v.getId() == R.id.bt_fx){
            frame_room.setCurrentItem(1, false);
        }else if (v.getId() == R.id.bt_xx){
            frame_room.setCurrentItem(2, false);
        }else if (v.getId() == R.id.bt_wd){
            frame_room.setCurrentItem(3, false);
        }
    }
}
