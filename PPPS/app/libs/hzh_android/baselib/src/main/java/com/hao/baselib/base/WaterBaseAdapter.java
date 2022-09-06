package com.hao.baselib.base;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * BaseAdapter基类
 * Created by WaterWood on 2018/5/30.
 */
public abstract class WaterBaseAdapter<T> extends BaseAdapter {

    protected Activity activity;
    protected List<T> list;

    /**
     * 如果这个不止一个列表，就在子类的Adapter中重写一个set方法放进来
     * @param activity
     * @param list
     */
    public WaterBaseAdapter(Activity activity, List<T> list){
        this.activity = activity;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //用Object替代Holder，保证子类强转后可以使用
        Object object = null;
        if (null == convertView) {
            convertView = View.inflate(activity, getLayoutId(), null);
            object = getHolderChild(convertView);
            //初始化Holder中的控件
            convertView.setTag(object);
        } else {
            object = (Object) convertView.getTag();
        }
        //加载数据
        initHolderData(object,position);
        return convertView;
    }

    /**
     * 获取Holder的具体实现
     * @return
     */
    protected abstract Object getHolderChild(View rootView);

    /**
     * 获取item布局
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化Holder中的控件数据
     * @param object
     */
    protected abstract void initHolderData(Object object, int position);
}
