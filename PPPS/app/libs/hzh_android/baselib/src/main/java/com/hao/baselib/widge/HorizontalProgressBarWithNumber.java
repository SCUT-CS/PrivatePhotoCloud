package com.hao.baselib.widge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.hao.baselib.R;

/**
 * 自定义带数字的进度条
 * @author WaterWood
 */
public class HorizontalProgressBarWithNumber extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFF19BE9B;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFC8F0E8;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 11;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 11;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;

    /**
     * painter of all drawing things
     */
    protected Paint mPaint = new Paint();
    /**
     * color of progress number
     */
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    /**
     * size of text (sp)
     */
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

    /**
     * offset of draw progress
     */
    protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

    /**
     * height of reached progress bar
     */
    protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

    /**
     * color of reached bar
     */
    protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
    /**
     * color of unreached bar
     */
    protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
    /**
     * height of unreached progress bar
     */
    protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    /**
     * view width except padding
     */
    protected int mRealWidth;

    protected boolean mIfDrawText = true;

    protected static final int VISIBLE = 0;

    public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs,
                                           int defStyle) {
        super(context, attrs, defStyle);

        setHorizontalScrollBarEnabled(true);

        obtainStyledAttributes(attrs);

        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * get the styled attributes
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        // init values from custom attributes
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.HorizontalProgressBarWithNumber);

        mTextColor = attributes
                .getColor(
                        R.styleable.HorizontalProgressBarWithNumber_progress_text_color,
                        DEFAULT_TEXT_COLOR);
        mTextSize = (int) attributes.getDimension(
                R.styleable.HorizontalProgressBarWithNumber_progress_text_size,
                mTextSize);

        mReachedBarColor = attributes
                .getColor(
                        R.styleable.HorizontalProgressBarWithNumber_progress_reached_color,
                        mTextColor);
        mUnReachedBarColor = attributes
                .getColor(
                        R.styleable.HorizontalProgressBarWithNumber_progress_unreached_color,
                        DEFAULT_COLOR_UNREACHED_COLOR);
        mReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable.HorizontalProgressBarWithNumber_progress_reached_bar_height,
                        mReachedProgressBarHeight);
        mUnReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable.HorizontalProgressBarWithNumber_progress_unreached_bar_height,
                        mUnReachedProgressBarHeight);
        mTextOffset = (int) attributes
                .getDimension(
                        R.styleable.HorizontalProgressBarWithNumber_progress_text_offset,
                        mTextOffset);

        int textVisible = attributes
                .getInt(R.styleable.HorizontalProgressBarWithNumber_progress_text_visibility,
                        VISIBLE);
        if (textVisible != VISIBLE) {
            mIfDrawText = false;
        }
        attributes.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode != MeasureSpec.EXACTLY) {

            float textHeight = (mPaint.descent() + mPaint.ascent());
            int exceptHeight = (int) (getPaddingTop() + getPaddingBottom() + Math
                    .max(Math.max(mReachedProgressBarHeight,
                            mUnReachedProgressBarHeight), Math.abs(textHeight)));

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //画笔平移到指定paddingLeft， getHeight() / 2位置，注意以后坐标都为以此为0，0
        canvas.translate(getPaddingLeft()+20, getHeight() / 2);
        //当前进度和总值的比例
        float radio = getProgress() * 1.0f / getMax();
        //已到达的宽度
        float progressPosX = (int) ((mRealWidth - mTextOffset - 100) * radio);
        //绘制的文本
        String text = getProgress() + "%";

        //拿到字体的宽度和高度
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

        //如果到达最后，则未到达的进度条不需要绘制
        if (progressPosX > mRealWidth) {
            progressPosX = mRealWidth;
        }

        float endX = progressPosX;

        // 绘制背景线
        mPaint.setColor(mUnReachedBarColor);
        mPaint.setStrokeWidth(mReachedProgressBarHeight);
        canvas.drawLine(0, 0, mRealWidth - mTextOffset - 100, 0, mPaint);

        // 绘制到达线
        mPaint.setColor(mReachedBarColor);
        mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
        canvas.drawLine(0, 0, endX, 0, mPaint);

        // 绘制文本
        if (mIfDrawText) {
            mPaint.setColor(mTextColor);
            canvas.drawText(text, mRealWidth - mTextOffset - 65, -textHeight, mPaint);
        }

        canvas.restore();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRealWidth = w - getPaddingRight() - getPaddingLeft();

    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }
}
