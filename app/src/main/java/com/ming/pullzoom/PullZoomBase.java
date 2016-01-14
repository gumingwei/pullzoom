package com.ming.pullzoom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by mingwei on 1/14/16.
 */
public abstract class PullZoomBase<T extends View> extends LinearLayout implements IPullZoom {
    /**
     * 根布局,用来装所有内容
     */
    protected T mRootView;
    /**
     * 定义的显示伸缩效果的View
     */
    protected View mZoomView;
    /**
     * 伸缩效果上展示的内容
     */
    protected View mHeadView;
    /**
     * 是否允许下拉
     */
    private boolean isPullEnable = true;

    private boolean isZooming;

    private boolean isHeadHide;

    private boolean isDragging;

    private float mLastX;

    private float mLastY;

    private float mInitX;

    private float mInitY;

    private int mTouchSlop;

    public PullZoomBase(Context context) {
        this(context, null);
    }

    public PullZoomBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullZoomBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mRootView = initRootView(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullZoomView);
        int zoomResId = a.getResourceId(R.styleable.PullZoomView_zoomview, 0);
        if (zoomResId > 0) {
            mZoomView = inflater.inflate(zoomResId, null, false);
        }
        int headResId = a.getResourceId(R.styleable.PullZoomView_headview, 0);
        if (headResId > 0) {
            mHeadView = inflater.inflate(headResId, null, false);
        }
        initHeader(a);
        a.recycle();
        addView(mRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isPullEnable() || isHeadHide()) {
            return false;
        }
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isDragging = false;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && isDragging) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (allowStart()) {
                    mLastX = mInitX = ev.getX();
                    mLastY = mInitY = ev.getY();
                    isDragging = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (allowStart()) {
                    float x = ev.getX();
                    float y = ev.getY();
                    float diffX = x - mLastX;
                    float diffY = y - mLastY;
                    float diffYAds = Math.abs(diffY);
                    if (diffYAds > mTouchSlop && diffYAds > Math.abs(diffX)) {
                        if (diffY >= 1 && allowStart()) {
                            mLastX = x;
                            mLastY = y;
                            isDragging = true;
                        }
                    }
                }
                break;
        }
        return isDragging;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPullEnable || isHeadHide()) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (allowStart()) {
                    mLastX = mInitX = event.getX();
                    mLastY = mInitY = event.getY();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (allowStart()) {
                    mLastX = event.getX();
                    mLastY = event.getY();
                    final int newScrollValue = Math.round(Math.min(mInitY - mLastY, 0) / 2.0f);
                    pull(newScrollValue);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    isDragging = false;
                    smoothRestore();
                }
                break;
        }
        return false;
    }

    public boolean isPullEnable() {
        return isPullEnable;
    }

    public void setIsPullEnable(boolean isPullEnable) {
        this.isPullEnable = isPullEnable;
    }

    public boolean isHeadHide() {
        return isHeadHide;
    }

    public void setIsHeadHide(boolean isHeadHide) {
        this.isHeadHide = isHeadHide;
    }


    /**
     * 创建根布局,例如ListView,GridView,RecycleView,ScrollView等等
     *
     * @param context
     * @param set
     * @return
     */
    public abstract T initRootView(Context context, AttributeSet set);

    /**
     * 判定是否允许开始滚动
     *
     * @return
     */
    public abstract boolean allowStart();

    /**
     * 传入一个计算值,用来对Header做放大缩小操作
     *
     * @param value
     */
    public abstract void pull(int value);

    /**
     *
     */
    public abstract void smoothRestore();


}
