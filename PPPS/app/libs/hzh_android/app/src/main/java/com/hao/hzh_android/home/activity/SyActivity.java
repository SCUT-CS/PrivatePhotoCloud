package com.hao.hzh_android.home.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.hao.baselib.base.BaseActivity;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.SyCallback;
import com.hao.hzh_android.home.model.SyModel;
import com.hao.initlib.Student;

/**
 * 首页框架
 * @author WaterWood
 */
public class SyActivity extends BaseActivity<SyModel> implements SyCallback, View.OnClickListener {

    /**
     * 所有的Fragment对象
     */
    private WebFragment fragmentOne;
    private SyOneFragment fragmentTwo;
    private SyOneFragment fragmentThree;
    private SyOneFragment fragmentFour;
    /**
     * 页面构建相关
     */
    private FragmentManager fm;
    private FrameLayout frame_room;
    /**
     * 底部按钮
     */
    private Button bt_sy;
    private Button bt_fx;
    private Button bt_xx;
    private Button bt_wd;

    @Override
    protected SyModel getModelImp() {
        return new SyModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_sy;
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
        fragmentOne = new WebFragment();
        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_room,fragmentOne);
        fragmentTransaction.commit();
        Student student = new Student();
        student.setName("小明");
        student.setAge(22);
        student.setSex("男");
        sendBusMessage(student);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        switch (v.getId()){
            case R.id.bt_sy:
                hideFragment(fragmentTwo,fragmentTransaction);
                hideFragment(fragmentThree,fragmentTransaction);
                hideFragment(fragmentFour,fragmentTransaction);
                if (fragmentOne == null){
                    fragmentOne = new WebFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("flag",1);
                    fragmentOne.setArguments(bundle1);
                    fragmentTransaction.add(R.id.frame_room,fragmentOne);
                }else{
                    fragmentTransaction.show(fragmentOne);
                }
                break;
            case R.id.bt_fx:
                hideFragment(fragmentOne,fragmentTransaction);
                hideFragment(fragmentThree,fragmentTransaction);
                hideFragment(fragmentFour,fragmentTransaction);
                if (fragmentTwo == null){
                    fragmentTwo = new SyOneFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("flag",2);
                    fragmentTwo.setArguments(bundle2);
                    fragmentTransaction.add(R.id.frame_room,fragmentTwo);
                }else{
                    fragmentTransaction.show(fragmentTwo);
                }
                break;
            case R.id.bt_xx:
                hideFragment(fragmentOne,fragmentTransaction);
                hideFragment(fragmentTwo,fragmentTransaction);
                hideFragment(fragmentFour,fragmentTransaction);
                if (fragmentThree == null){
                    fragmentThree = new SyOneFragment();
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("flag",3);
                    fragmentThree.setArguments(bundle3);
                    fragmentTransaction.add(R.id.frame_room,fragmentThree);
                }else{
                    fragmentTransaction.show(fragmentThree);
                }
                break;
            case R.id.bt_wd:
                hideFragment(fragmentOne,fragmentTransaction);
                hideFragment(fragmentTwo,fragmentTransaction);
                hideFragment(fragmentThree,fragmentTransaction);
                if (fragmentFour == null){
                    fragmentFour = new SyOneFragment();
                    Bundle bundle4 = new Bundle();
                    bundle4.putInt("flag",4);
                    fragmentFour.setArguments(bundle4);
                    fragmentTransaction.add(R.id.frame_room,fragmentFour);
                }else{
                    fragmentTransaction.show(fragmentFour);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    /**
     * 隐藏指定Fragment
     * @param fragment
     * @param ft
     */
    private void hideFragment(Fragment fragment,FragmentTransaction ft){
        if (fragment!=null){
            ft.hide(fragment);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragmentOne.getCanBack()){
            fragmentOne.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
