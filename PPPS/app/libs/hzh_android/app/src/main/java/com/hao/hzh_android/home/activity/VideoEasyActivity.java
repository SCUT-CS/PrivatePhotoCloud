package com.hao.hzh_android.home.activity;

import android.view.WindowManager;
import android.widget.LinearLayout;

import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.base.WaterPermissionActivity;
import com.hao.baselib.utils.PathGetUtil;
import com.hao.easyvideolib.video.MyVideoView;
import com.hao.hzh_android.R;
import com.hao.hzh_android.home.callback.VideoEasyCallback;
import com.hao.hzh_android.home.model.VideoEasyModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 自己做的一个视频播放
 *
 * @author WaterWood
 */
public class VideoEasyActivity extends WaterPermissionActivity<VideoEasyModel> implements VideoEasyCallback {

    private MyVideoView video;
    private LinearLayout ll_root;

    @Override
    protected VideoEasyModel getModelImp() {
        return new VideoEasyModel(this, this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_video_easy;
    }

    @Override
    protected void initWidget() {
        statusBarColor(R.color.black,true);
        video = findViewById(R.id.video);
        ll_root = findViewById(R.id.ll_root);
        requestPermission(READ_EXTERNAL_STORAGE);
    }

    @Override
    protected void doSDRead() {
        requestPermission(WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void doSDWrite() {
        List<String> list = new ArrayList<>();
        list.add("aaaa");
        video.setmUrl(PathGetUtil.getLongwayPath(this, list) + File.separator + "aaa.mp4");
        video.setParentViewGroup(ll_root);
    }
}
