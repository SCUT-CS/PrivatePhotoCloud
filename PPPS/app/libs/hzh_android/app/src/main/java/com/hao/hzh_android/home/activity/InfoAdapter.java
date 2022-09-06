package com.hao.hzh_android.home.activity;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import com.hao.baselib.base.BaseAdapterHolder;
import com.hao.baselib.base.WaterBaseAdapter;
import com.hao.hzh_android.R;
import java.util.List;
import butterknife.BindView;

public class InfoAdapter extends WaterBaseAdapter<String> {
    /**
     * 如果这个不止一个列表，就在子类的Adapter中重写一个set方法放进来
     *
     * @param activity
     * @param list
     */
    public InfoAdapter(Activity activity, List<String> list) {
        super(activity, list);
    }

    @Override
    protected Object getHolderChild(View rootView) {
        return new Holder(rootView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_haha;
    }

    @Override
    protected void initHolderData(Object object, int position) {
        Holder holder = (Holder) object;
        holder.tv_item.setText(list.get(position));
    }

    class Holder extends BaseAdapterHolder {

        @BindView(R.id.tv_item)
        TextView tv_item;

        public Holder(View view) {
            super(view);
        }
    }
}
