package com.example.lfy.myapplication.Util;

import android.view.View;

import java.util.Calendar;

/**
 * Created by lfy on 2016/9/21.
 */
public abstract class OnClickEvent implements View.OnClickListener {

    public abstract void singleClick(View v);
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            singleClick(v);
        }
    }
}