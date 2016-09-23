package com.example.lfy.myapplication.Util;

import android.app.Application;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.MyApplication;
import com.example.lfy.myapplication.R;


/**
 * author:杭州短趣网络传媒技术有限公司
 * date:2016/6/27
 * description:DemoActivity
 */
public class ToastUtils {

    public static Toast toast;

    public ToastUtils() {
    }

    public static void showToast(String text) {
        View toastRoot = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.toast_default_layout, null, false);
        TextView message = (TextView) toastRoot.findViewById(R.id.toast_info);
        message.setText(text);
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = new Toast(MyApplication.getContext());
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showToast(String text, boolean flag) {
        View toastRoot = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.toast_default_layout, null, false);
        TextView message = (TextView) toastRoot.findViewById(R.id.toast_info);
        ImageView image = (ImageView) toastRoot.findViewById(R.id.toast_image);
        if (flag) {
            image.setVisibility(View.VISIBLE);
        }

        message.setText(text);
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = new Toast(MyApplication.getContext());
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showToast(int resID) {
        showToast(MyApplication.getContext().getString(resID));
    }

}
