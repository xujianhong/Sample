package com.example.administrator.myapplication.Tab;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/6/30.
 */
public class MyViewPager extends ViewPager {
    private boolean enabled = true;
    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public boolean onTouchEvent(MotionEvent event) {
        return this.enabled && super.onTouchEvent(event);
    }
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.enabled && super.onInterceptTouchEvent(event);
    }

    public void setSlidingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
