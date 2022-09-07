package com.hao.baselib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.hao.baselib.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Activity基类
 * Created by WaterWood on 2018/5/9.
 */
public abstract class BaseActivity<T extends MvcBaseModel> extends AppCompatActivity {

    protected T mModel;
    //日志打印标志
    protected String TAG;
    protected Bundle savedInstanceState;
    /**
     * 拍照专用
     */
    public static final int CAMERA_RC = 0x99;
    protected File filePicCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在界面未初始化之前调用的初始化窗口
        initWindows();
        if (initArgs(getIntent().getExtras())) {
            this.savedInstanceState = savedInstanceState;
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(getContentLayoutId());
            TAG = getComponentName().getShortClassName();
            //绑定butterKnife
            ButterKnife.bind(this);
            initWidget();
            mModel = getModelImp();
            initData();
        } else {
            finish();
        }
    }

    protected abstract T getModelImp();

    /**
     * 初始化窗口
     */
    protected void initWindows() {

    }

    /**
     * 初始化相关参数
     *
     * @param bundle 参数bundle
     * @return 如果参数正确返回true, 错误返回false
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 得到当前界面的资源文件Id
     *
     * @return 资源文件Id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {

    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        //点击当前界面导航返回时，finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //得到当前Activity下的所有Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        //判断集合是否为空
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                //判断是否为我们能够处理的Fragment类型
                if (fragment instanceof BaseFragment) {
                    //是否拦截了返回按钮
                    if (((BaseFragment) fragment).onBackPressed()) {
                        //如果有，直接return
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }

    /**
     * 设置状态栏颜色
     * @param color 状态栏颜色
     * @param isDark 选择的颜色是否是深色
     */
    protected void statusBarColor(int color,boolean isDark){
        //改变状态栏颜色
        StatusBarUtil.setColor(this, getResources().getColor(color), 0);
        if (isDark){
            StatusBarUtil.setDarkMode(this);
        }else {
            StatusBarUtil.setLightMode(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CAMERA_RC:
                    getCameraPath(filePicCamera.getPath());
                    break;
            }
        }
    }

    /**
     * 图片回调回来的方法，不用抽象，可以不重写
     */
    protected void getCameraPath(String path) {

    }

    /**
     * EventBus发送消息
     * @param object
     */
    protected void sendBusMessage(Object object){
        EventBus.getDefault().post(object);
    }

    /**
     * 注册EventBus
     */
    protected void registerBus(){
        EventBus.getDefault().register(this);
    }

    /**
     * 解除注册
     * @param context
     */
    protected void unRegisterBus(Context context){
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    //接收消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(Object messageEvent) {
        receiveBusInfo(messageEvent);
    }

    /**
     * 接收消息的复写方法
     * @param messageEvent
     */
    protected void receiveBusInfo(Object messageEvent){

    }
}