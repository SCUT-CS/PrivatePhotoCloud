package com.hao.hzh_android.home.activity;

import android.widget.ListView;

import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.utils.ToastUtil;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.GetValueCallback;
import com.hao.hzh_android.home.model.GetValueModel;
import com.hao.initlib.ConfigType;
import com.hao.initlib.Latte;
import com.hao.initlib.Student;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 获取值
 */
public class GetValueActivity extends BaseActivity<GetValueModel> implements GetValueCallback {

    @BindView(R.id.listview)
    ListView listview;

    @Override
    protected GetValueModel getModelImp() {
        return new GetValueModel(this,this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_get_value;
    }

    @Override
    protected void initWidget() {
        //获取值的方法
        Student student = Latte.getInstance().<Student>getValueByKey(ConfigType.LOGIN);
        ToastUtil.toastWord(this,student.toString());
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("嘻嘻嘻"+i);
        }
        InfoAdapter infoAdapter = new InfoAdapter(this,list);
        listview.setAdapter(infoAdapter);
    }
}
