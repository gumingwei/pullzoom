package com.ming.pullzoom;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by mingwei on 1/14/16.
 */
public class PullZoomScrollVIew extends PullZoomBase<ScrollView> {

    /**
     * header's parent layout
     */
    private FrameLayout mHeaderContainer;
    /**
     * ScroollView's one of chaild view
     */
    private LinearLayout mRootContainer;
    /**
     */
    private View mContentView;
    /**
     * hsader's height
     */
    private int mHeaderHeight;

    private SmoothRestore mSmoothRestore;

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };


    public PullZoomScrollVIew(Context context) {
        this(context, null);
    }

    public PullZoomScrollVIew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullZoomScrollVIew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSmoothRestore = new SmoothRestore();
    }

    @Override
    public ScrollView initRootView(Context context, AttributeSet set) {
        ScrollView scrollview = new InnerScrollView(context, set);
        return scrollview;
    }

    @Override
    public void initHeader(TypedArray a) {
        mRootContainer = new LinearLayout(getContext());
        mRootContainer.setOrientation(LinearLayout.VERTICAL);
        mHeaderContainer = new FrameLayout(getContext());
        if (mZoomView != null) {
            mHeaderContainer.addView(mZoomView);
        }
        if (mHeadView != null) {
            mHeaderContainer.addView(mHeadView);
        }
        int contentResId = a.getResourceId(R.styleable.PullZoomView_contentview, 0);
        if (contentResId > 0) {
            mContentView = LayoutInflater.from(getContext()).inflate(contentResId, null, false);
        }
        mRootContainer.addView(mHeaderContainer);
        if (mContentView != null) {
            mRootContainer.addView(mContentView);
        }
        mRootContainer.setClipChildren(false);
        mHeaderContainer.setClipChildren(false);
        mRootView.addView(mRootContainer);
    }

    public void setHeaderLayoutParams(LinearLayout.LayoutParams params) {
        if (mHeaderContainer != null) {
            mHeaderContainer.setLayoutParams(params);
            mHeaderHeight = params.height;
        }
    }

    @Override
    public boolean allowStart() {
        return mRootView.getScrollY() == 0;
    }

    @Override
    public void pull(int value) {
        if (mSmoothRestore != null && !mSmoothRestore.isFinished()) {
            mSmoothRestore.abort();
        }
        ViewGroup.LayoutParams params = mHeaderContainer.getLayoutParams();
        params.height = Math.abs(value) + mHeaderHeight;
        mHeaderContainer.setLayoutParams(params);
    }

    @Override
    public void smoothRestore() {
        mSmoothRestore.start(200L);
    }

    class InnerScrollView extends ScrollView {
        OnScrollListener scrollListener;

        public InnerScrollView(Context context) {
            this(context, null);
        }

        public InnerScrollView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public InnerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setOnScrollListener(OnScrollListener scrollListener) {
            this.scrollListener = scrollListener;
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            if (this.scrollListener != null) {
                this.scrollListener.scroll(l, t, oldl, oldt);
            }
        }
    }

    interface OnScrollListener {
        void scroll(int l, int t, int ol, int ot);
    }

    /*class SmoothRestore implements Runnable {
        protected long duration;
        protected boolean isFinished;
        protected float scale;
        protected long starttime;

        SmoothRestore() {
        }

        public void abort() {
            isFinished = true;
        }

        public boolean isFinised() {
            return isFinished;
        }

        public void start(long d) {
            if (mZoomView != null) {
                starttime = SystemClock.currentThreadTimeMillis();
                duration = d;
                scale = (float) mHeaderContainer.getBottom() / mHeaderHeight;
                isFinished = false;
                post(this);
            }
        }

        @Override
        public void run() {
            Log.i("Gmw", "run");
            if (mZoomView != null) {
                float f2;
                ViewGroup.LayoutParams params;
                Log.i("Gmw", "scale=" + scale);
                if (!isFinished && scale > 1.0D) {
                    float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) starttime) / (float) duration;
                    f2 = scale - (scale - 1.0F) * PullZoomListView.mInterpolator.getInterpolation(f1);
                    params = mHeaderContainer.getLayoutParams();
                    if (f2 > 1.0F) {
                        params.height = ((int) f2 * mHeaderHeight);
                        mHeaderContainer.setLayoutParams(params);
                        post(this);
                        return;
                    }
                    isFinished = true;
                }
            }
        }
    }*/

    class SmoothRestore implements Runnable {
        protected long duration;
        protected boolean isFinished = true;
        protected float scale;
        protected long startTime;

        SmoothRestore() {
        }

        public void abort() {
            isFinished = true;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public void run() {
            if (mZoomView != null) {
                float f2;
                ViewGroup.LayoutParams localLayoutParams;
                if ((!isFinished) && (scale > 1.0D)) {
                    float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) startTime) / (float) duration;
                    f2 = scale - (scale - 1.0F) * PullZoomScrollVIew.sInterpolator.getInterpolation(f1);
                    localLayoutParams = mHeaderContainer.getLayoutParams();
                    if (f2 > 1.0F) {
                        localLayoutParams.height = ((int) (f2 * mHeaderHeight));
                        mHeaderContainer.setLayoutParams(localLayoutParams);
                        /*if (isCustomHeaderHeight) {
                            ViewGroup.LayoutParams zoomLayoutParams;
                            zoomLayoutParams = mZoomView.getLayoutParams();
                            zoomLayoutParams.height = ((int) (f2 * mHeaderHeight));
                            mZoomView.setLayoutParams(zoomLayoutParams);
                        }*/
                        post(this);
                        return;
                    }
                    isFinished = true;
                }
            }
        }

        public void start(long paramLong) {
            if (mZoomView != null) {
                startTime = SystemClock.currentThreadTimeMillis();
                duration = paramLong;
                scale = ((float) (mHeaderContainer.getBottom()) / mHeaderHeight);
                isFinished = false;
                post(this);
            }
        }
    }
}
