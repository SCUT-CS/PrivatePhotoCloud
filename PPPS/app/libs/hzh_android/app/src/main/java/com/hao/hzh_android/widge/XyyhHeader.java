package com.hao.hzh_android.widge;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hao.baselib.utils.ScreenUtil;
import java.util.List;

/**
 * 仿兴业银行切换效果
 * @author WaterWood
 */
public class XyyhHeader extends FrameLayout {

    private Handler handler;
    private int numBig = 60;
    private int numSmall = 30;
    private RelativeLayout one;
    private RelativeLayout two;
    private Runnable runnable;
    private Context context;
    private List<View> listAll;
    private int site;
    private boolean isStart;

    public XyyhHeader(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public XyyhHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public XyyhHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    /**
     * 初始化两个壳子
     */
    private void init() {
        //初始化两个壳子
        one = new RelativeLayout(context);
        two = new RelativeLayout(context);
        //定义两个壳子尺寸
        LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params1.leftMargin = numBig;
        params1.rightMargin = numBig;
        params1.topMargin = numSmall;
        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params2.leftMargin = numSmall;
        params2.rightMargin = numSmall;
        params2.topMargin = numBig;
        //设置两个壳子叠放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            one.setZ(1);
            two.setZ(2);
        }
        //把壳子放入自定义控件
        addView(one, params1);
        addView(two, params2);
        anim(context);
    }

    /**
     * 设置数据
     * @param listView
     */
    public void setView(List<View> listView) {
        listAll = listView;
        if (isStart){

        }else {
            View viewOne = listView.get(0);
            View viewTwo = listView.get(1);
            //给这两个赋值是重点
            site = 1;
            one.removeAllViews();
            two.removeAllViews();
            one.addView(viewOne);
            two.addView(viewTwo);
            isStart = true;
        }
    }

    /**
     * 启动动画
     */
    private void anim(final Context context) {
        handler = new Handler();
        //每两秒执行一次
        runnable = new Runnable() {
            @Override
            public void run() {
                //1.定义一个组合动画，前面图片下去，后面图片拉宽，同时进行
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(200);
                //首先定义一个向下消失的动画
                final ObjectAnimator mObjectAnimator1 = ObjectAnimator.ofFloat(two, "translationY", 500);
                //再定义一个横向变大的动画
                ObjectAnimator mObjectAnimator2 = ObjectAnimator.ofFloat(one, "scaleX", 1.0f, (float) (ScreenUtil.getScreenWidth(context) - numBig) / (float) (ScreenUtil.getScreenWidth(context) - numBig * 2));
                animatorSet.play(mObjectAnimator1).with(mObjectAnimator2);
                animatorSet.start();
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //动画执行结束
                        //1:计算site
                        site++;
                        if (site >= listAll.size()) {
                            site = 0;
                        }
                        //2.修改two中的内容
                        two.removeAllViews();
                        two.addView(listAll.get(site));
                        //1.先改变层叠关系
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            one.setZ(2);
                            two.setZ(1);
                        }
                        //3.修改2图的左右边距
                        FrameLayout.LayoutParams params = (LayoutParams) two.getLayoutParams();
                        params.leftMargin = numBig;
                        params.rightMargin = numBig;
                        params.topMargin = numSmall;
                        two.setLayoutParams(params);
                        //4.把2图提上来
                        ObjectAnimator mObjectAnimator3 = ObjectAnimator.ofFloat(two, "translationY", 0);
                        mObjectAnimator3.setDuration(1);
                        mObjectAnimator3.start();
                        //5.把2图修改缩放
                        ObjectAnimator mObjectAnimator4 = ObjectAnimator.ofFloat(two, "scaleX", (float) (ScreenUtil.getScreenWidth(context) - numBig) / (float) (ScreenUtil.getScreenWidth(context) - numBig * 2), 1.0f);
                        mObjectAnimator4.setDuration(1);
                        mObjectAnimator4.start();
                        //6.把1图拉下去
                        ObjectAnimator mObjectAnimator5 = ObjectAnimator.ofFloat(one, "translationY", numSmall);
                        mObjectAnimator5.setDuration(200);
                        mObjectAnimator5.start();
                        //7.控件交换
                        RelativeLayout middle = one;
                        one = two;
                        two = middle;
                        //7.再次启动
                        handler.postDelayed(runnable, 2000);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        };
        handler.postDelayed(runnable, 2000);
    }
}
