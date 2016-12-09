package com.chengtech.chengtechmt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 作者: LiuFuYingWang on 2016/11/15 11:33.
 */

public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获得listview的个数
        int count=getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            try {
                child.dispatchTouchEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
