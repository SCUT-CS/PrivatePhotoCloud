package com.hao.easyvideolib.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.hao.baselib.base.BaseActivity;
import com.hao.baselib.utils.ScreenUtil;
import com.hao.easyvideolib.R;
import java.io.IOException;

/**
 * 自定义视频播放控件
 *
 * @author WaterWood
 */
public class MyVideoView extends RelativeLayout implements View.OnClickListener
        , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener
        , MediaPlayer.OnBufferingUpdateListener, TextureView.SurfaceTextureListener,FullInter {

    //上下文
    private Context context;
    //布局打气筒
    private LayoutInflater inflater;
    //内嵌视图的根部局
    private RelativeLayout rl_video_root;
    //计算出来的播放器宽高
    private int width;
    private int height;
    //帧布局控件
    private TextureView texture;
    //真正承载帧数据的类，从SurfaceView中可以得到
    private Surface videoSurface;
    //视频地址
    private String mUrl;
    //看设置地址和帧布局哪个先执行完
    private boolean isUrlOk;
    private boolean isSurfaceOk;
    //加载图片
    private ImageView iv_load;
    //媒体播放类
    private MediaPlayer mediaPlayer;
    //播放布局
    private LinearLayout ll_play_layout;
    //状态控制
    private int currentStatus;
    private final int IDLE = 0;
    private final int PLAY = 1;
    private final int PAUSE = 2;
    private final int ERROR = 3;
    //定时器相关
    private Handler handler;
    private Runnable runnable;
    //拖动进度条
    private SeekBar seekbar;
    //进度条更新相关
    private boolean isDown;
    //播放按钮
    private ImageView iv_play;
    //暂停按钮
    private ImageView iv_pause;
    //暂停点击区域
    private RelativeLayout rl_pause;
    //是否显示
    private boolean isShow = true;
    //重置次数
    private int reLoadNum;
    //全屏按钮
    private RelativeLayout rl_full;
    //播放器父布局
    private ViewGroup mParentView;
    //播放过程接口
    private PlayListener playListener;

    private BaseActivity mActivity;

    public MyVideoView(Context context) {
        super(context);
        this.context = context;
        mActivity = (BaseActivity) context;
        init();
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        //初始化打气筒
        inflater = LayoutInflater.from(this.getContext());
        RelativeLayout mPlayerView = (RelativeLayout) inflater.inflate(R.layout.easy_video_view, this);
        //初始化控件
        rl_video_root = mPlayerView.findViewById(R.id.rl_video_root);
        texture = mPlayerView.findViewById(R.id.texture);
        iv_load = mPlayerView.findViewById(R.id.iv_load);
        ll_play_layout = mPlayerView.findViewById(R.id.ll_play_layout);
        seekbar = mPlayerView.findViewById(R.id.seekbar);
        iv_play = mPlayerView.findViewById(R.id.iv_play);
        iv_pause = mPlayerView.findViewById(R.id.iv_pause);
        rl_pause = mPlayerView.findViewById(R.id.rl_pause);
        rl_full = mPlayerView.findViewById(R.id.rl_full);
        //初始化监听
        texture.setSurfaceTextureListener(this);
        iv_play.setOnClickListener(this);
        rl_pause.setOnClickListener(this);
        texture.setOnClickListener(this);
        rl_full.setOnClickListener(this);
        //状态初始化
        currentStatus = IDLE;
        //设置一个定时器
        handler = new Handler();
        //每两秒执行一次
        runnable = new Runnable() {
            @Override
            public void run() {
                //更新进度条位置
                seekbar.setProgress((int) ((double) mediaPlayer.getCurrentPosition() / (double) mediaPlayer.getDuration() * 100));
                //每秒执行一次方法
                handler.postDelayed(runnable, 1000);
            }
        };
        //监听拖动条
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isDown) {
                    mediaPlayer.seekTo((int) ((double) mediaPlayer.getDuration() * ((double) progress / (double) 100)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDown = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDown = false;
            }
        });
        //注册锁屏广播
        ScreenEventReceiver screenEventReceiver = new ScreenEventReceiver();
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(Intent.ACTION_SCREEN_OFF);
        itFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(screenEventReceiver, itFilter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int withSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        width = withSize;
        height = heightSize;
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //如果宽度是包裹内容
            width = ScreenUtil.getScreenWidth(context);
            height = (int) ((double) width / (double) 16 * (double) 9);
            setMeasuredDimension(width, height);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //如果高度是包裹内容
            width = withSize;
            height = (int) ((double) width / (double) 16 * (double) 9);
            setMeasuredDimension(width, height);
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_video_root.getLayoutParams();
        params.width = width;
        params.height = height;
        rl_video_root.setLayoutParams(params);
    }

    /**
     * TextureView准备好了
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        videoSurface = new Surface(surface);
        synchronized (MyVideoView.class) {
            isSurfaceOk = true;
            if (isSurfaceOk && isUrlOk) {
                //启动加载动画
                startAnim();
                //开始load视频
                load();
            }
        }
    }

    /**
     * TextureView大小发生改变
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    /**
     * TextureView被销毁
     *
     * @param surface
     * @return
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    /**
     * TextureView更新
     *
     * @param surface
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * 加载视频
     */
    private void load() {
        try {
            //创建MediaPlayer
            createMediaPlayer();
            //设置资源
            mediaPlayer.setDataSource(mUrl);
            //开始准备
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            if (reLoadNum < 3) {
                reLoadNum++;
                load();
            }else{
                reLoadNum = 0;
                Log.i("MyVideoView","load error");
            }
        }
    }

    /**
     * 启动加载动画
     */
    private void startAnim() {
        Animation loadAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_wait);
        iv_load.startAnimation(loadAnim);
    }

    /**
     * 清除动画
     */
    private void stopAnim() {
        iv_load.clearAnimation();
        iv_load.setVisibility(GONE);
    }

    /**
     * 设置视频地址
     *
     * @param mUrl
     */
    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
        synchronized (MyVideoView.class) {
            isUrlOk = true;
            if (isSurfaceOk && isUrlOk) {
                //启动加载动画
                startAnim();
                //开始load视频
                load();
            }
        }
    }

    /**
     * 创建播放器
     */
    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setSurface(videoSurface);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        complete();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (reLoadNum < 3) {
            reLoadNum++;
            load();
        }else{
            reLoadNum = 0;
            Log.i("MyVideoView","load error");
        }
        return true;
    }

    /**
     * 准备完成
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        //停止加载动画并隐藏加载框
        stopAnim();
        //播放
        play();
        //开始定时器，开始更新播放进度
        handler.postDelayed(runnable, 100);
        //设置回调
        if (playListener!=null) {
            playListener.prepareOk();
        }
    }

    /**
     * 播放
     */
    public void play(){
        //显示播放布局
        showPlayView();
        //开始播放
        mediaPlayer.start();
        //状态修改
        currentStatus = PLAY;
    }

    /**
     * 初始化所有按钮
     */
    private void resetView(){
        iv_play.setVisibility(GONE);
        iv_load.setVisibility(GONE);
        ll_play_layout.setVisibility(GONE);
        iv_pause.setImageResource(R.mipmap.ic_easy_pause);
    }

    /**
     * 显示播放布局
     */
    private void showPlayView(){
        resetView();
        ll_play_layout.setVisibility(VISIBLE);
    }

    /**
     * 显示播放布局
     */
    private void showPauseView(){
        resetView();
        ll_play_layout.setVisibility(VISIBLE);
        iv_play.setVisibility(VISIBLE);
        iv_pause.setImageResource(R.mipmap.ic_esay_play_left);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_pause) {
            //点击暂停
            if (currentStatus == PLAY) {
                pause();
            }else{
                play();
            }
        }else if (v.getId() == R.id.iv_play){
            //点击屏幕中间播放
            play();
        }else if (v.getId() == R.id.texture){
            //点击图像
            if (currentStatus == PLAY){
               if (isShow){
                   isShow = false;
                   ll_play_layout.setVisibility(GONE);
               }else{
                   isShow = true;
                   ll_play_layout.setVisibility(VISIBLE);
               }
            }
        }else if (v.getId() == R.id.rl_full){
            //点击全屏
            //创建全屏dialog
            VideoFullDialog dialog = new VideoFullDialog(context,getCurrentPosition());
            //传入接口实现类
            dialog.setFullInter(this);
            //显示dialog
            dialog.show();
        }
    }

    /**
     * 暂停
     */
    public void pause(){
        //判断播放器是否为空，并且是否真的在播放
        if (currentStatus == PLAY) {
            //执行暂停
            mediaPlayer.pause();
            //修改状态
            currentStatus = PAUSE;
            //展示布局
            showPauseView();
        }
    }

    /**
     * 播放完成
     */
    private void complete(){
        pause();
        mediaPlayer.seekTo(0);
    }

    /**
     * 当全屏点击关闭时调用
     * @param position
     */
    @Override
    public void fullToSmall(int position) {
        showFull();
        mediaPlayer.seekTo(position);
    }

    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //主动锁屏时 paus,主动解锁屏幕时 resume
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    //解锁屏幕
                    play();
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    //锁屏
                    pause();
                    break;
            }
        }
    }

    /**
     * 获取当前的播放位置
     * @return
     */
    public int getCurrentPosition() {
        if (this.mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 设置播放器父布局
     * @param viewGroup
     */
    public void setParentViewGroup(ViewGroup viewGroup) {
        this.mParentView = viewGroup;
    }

    /**
     * 隐藏全屏按钮
     */
    public void hideFull(){
        rl_full.setVisibility(INVISIBLE);
    }

    /**
     * 显示全屏按钮
     */
    public void showFull(){
        rl_full.setVisibility(VISIBLE);
    }

    /**
     * 播放过程监听接口
     */
    public interface PlayListener{
        void prepareOk();
    }

    /**
     * 设置播放过程监听
     * @param playListener
     */
    public void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    /**
     * 跳转到指定位置
     * @param position
     */
    public void seekToPosition(int position){
        mediaPlayer.seekTo(position);
    }
}
