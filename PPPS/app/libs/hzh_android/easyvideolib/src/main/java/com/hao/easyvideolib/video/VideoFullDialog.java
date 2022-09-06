package com.hao.easyvideolib.video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.hao.baselib.utils.PathGetUtil;
import com.hao.baselib.utils.ScreenUtil;
import com.hao.baselib.utils.StatusBarUtil;
import com.hao.easyvideolib.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 全屏弹框
 * @author WaterWood
 */
public class VideoFullDialog extends Dialog implements MyVideoView.PlayListener {

    /**
     * 定义TAG
     */
    private static final String TAG = VideoFullDialog.class.getSimpleName();
    /**
     * 定义相关UI
     */
    private MyVideoView mVideoView;//视频播放器
    private ViewGroup mParentView;//要将视频播放器添加到的父容器
    private ImageView mBackButton;//返回按钮
    private RelativeLayout root_view;//根部局
    /**
     * 从小屏到全屏时视频的播放位置
     */
    private int mPosition;
    /**
     * dialog和原有视图交互接口
     */
    private FullInter fullInter;
    /**
     * 当前播放位置
     */
    private int playPosition;

    private Activity activity;

    /**
     * 构造方法
     * @param context
     */
    public VideoFullDialog(@NonNull Context context,int playPosition) {
        super(context, R.style.dialog_full_screen);
        this.activity = (Activity) context;
        this.mVideoView = new MyVideoView(context);
        mVideoView.hideFull();
        mVideoView.setPlayListener(this);
        this.playPosition = playPosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.xadsdk_dialog_video_layout);
        initVideoView();
    }

    /**
     * 初始化控件操作
     */
    private void initVideoView(){
        mParentView = findViewById(R.id.content_layout);
        mBackButton = findViewById(R.id.xadsdk_player_close_btn);
        root_view = findViewById(R.id.root_view);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBackBtn();
            }
        });
        mParentView.addView(mVideoView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) root_view.getLayoutParams();
        params.width = ScreenUtil.getScreenHeight(getContext());
        params.height = ScreenUtil.getScreenWidth(getContext());
        root_view.setLayoutParams(params);
        root_view.setPivotX(ScreenUtil.getScreenWidth(getContext())/2);
        root_view.setPivotY(ScreenUtil.getScreenWidth(getContext())/2);
        root_view.setRotation(90);
    }

    /**
     * 点击关闭全屏的按钮
     */
    private void clickBackBtn(){
        dismiss();
        fullInter.fullToSmall(mVideoView.getCurrentPosition());
    }

    @Override
    public void onBackPressed() {
        clickBackBtn();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus){
            //未取得焦点时逻辑
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }else{
            //取得焦点时逻辑
            List<String> list = new ArrayList<>();
            list.add("aaaa");
            mVideoView.setmUrl(PathGetUtil.getLongwayPath(getContext(),list)+ File.separator+"aaa.mp4");
            mVideoView.setParentViewGroup(mParentView);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mParentView.removeView(mVideoView);
    }

    /**
     * 传入接口实现类
     * @param fullInter
     */
    public void setFullInter(FullInter fullInter) {
        this.fullInter = fullInter;
    }

    /**
     * 准备成功，开始播放了
     */
    @Override
    public void prepareOk() {
        mVideoView.seekToPosition(playPosition);
    }
}
