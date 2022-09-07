package com.hao.baselib.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment基类
 * Created by WaterWood on 2018/5/9.
 */
public abstract class BaseFragment<T extends MvcBaseModel> extends Fragment {

    protected T mModel;
    protected View mRoot;
    protected Activity activity;
    //日志打印标志
    protected String TAG;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //初始化参数
        initArgs(getArguments());
        this.activity = activity;
        TAG = activity.getComponentName().getShortClassName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            int layId = getContentLayoutId();
            //初始化当前的根布局，但是不在创建时就添加到container里面
            View root = inflater.inflate(layId, container, false);
            ButterKnife.bind(this,root);
            initWidget(root);
            mModel = getModelImp();
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                //把当前root从其父控件中移除
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //当View创建完成后初始化数据
        initData();
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
    protected void initWidget(View root) {

    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 初始化相关参数
     *
     * @param bundle 参数bundle
     * @return 如果参数正确返回true, 错误返回false
     */
    protected void initArgs(Bundle bundle) {

    }

    /**
     * 返回键触发时调用
     *
     * @return 返回true代表我已处理返回逻辑，Activity不用自己finish。
     * 返回false代表我没有处理逻辑，Activity自己走自己的逻辑
     */
    public boolean onBackPressed() {
        return false;
    }

    protected abstract T getModelImp();
}