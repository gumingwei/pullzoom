package com.ming.pullzoom.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.ming.pullzoom.PullZoomListView;
import com.ming.pullzoom.R;

/**
 * Created by mingwei on 1/14/16.
 */
public class PullZoomListviewActivity extends AppCompatActivity {
    PullZoomListView mListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("PullZoomListView");
        setContentView(R.layout.pullzoom_listview);
        mListview = (PullZoomListView) findViewById(R.id.listview);
        String[] adapterData = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "21"};
        mListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, adapterData));
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        mListview.setHeaderLayoutParams(localObject);
    }
}
