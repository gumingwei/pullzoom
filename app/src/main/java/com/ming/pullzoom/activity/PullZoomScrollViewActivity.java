package com.ming.pullzoom.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.ming.pullzoom.PullZoomScrollVIew;
import com.ming.pullzoom.R;

/**
 * Created by mingwei on 1/14/16.
 */
public class PullZoomScrollViewActivity extends AppCompatActivity {
    PullZoomScrollVIew scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pullzoom_scrollview);
        setTitle("PullZoomScrollView");
        scrollview = (PullZoomScrollVIew) findViewById(R.id.scrollview);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollview.setHeaderLayoutParams(localObject);

    }
}
