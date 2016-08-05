package com.example.lfy.myapplication.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by lfy on 2015/12/29.
 */
public class Listview extends ListView {
    public Listview(Context context) {
        super(context);
    }

    public Listview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Listview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
