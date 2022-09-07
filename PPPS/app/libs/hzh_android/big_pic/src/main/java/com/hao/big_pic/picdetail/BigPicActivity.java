package com.hao.big_pic.picdetail;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.utils.NullUtil;
import com.hao.big_pic.R;
import java.util.List;

/**
 * 大图预览页面
 * @author WaterWood
 */
public class BigPicActivity extends BaseActivity<BigPicModel> implements BigPicCallback, View.OnClickListener{

    private RelativeLayout rl_back;
    private TextView tv_title;
    private ViewPager view_pager;
    private BigPicAdapter bigPicAdapter;
    private List<String> listPic;
    private int position;

    @Override
    protected BigPicModel getModelImp() {
        return new BigPicModel(this,this);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        listPic = bundle.getStringArrayList("listPic");
        position = bundle.getInt("site",-1);
        if (NullUtil.isListEmpty(listPic) || position == -1){
            return false;
        }else{
            return true;
        }
    }

    @Override
    protected void initWidget() {
        //改变状态栏颜色
        statusBarColor(R.color.white,false);
        rl_back = findViewById(R.id.rl_back);
        tv_title = findViewById(R.id.tv_title);
        view_pager = findViewById(R.id.view_pager);
        rl_back.setOnClickListener(this);
        tv_title.setText("图片浏览");
        bigPicAdapter = new BigPicAdapter(this,listPic);
        view_pager.setAdapter(bigPicAdapter);
        if (position >= listPic.size()){
            position = 0;
        }
        view_pager.setCurrentItem(position);
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_big_pic;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_back){
            finish();
        }
    }
}
