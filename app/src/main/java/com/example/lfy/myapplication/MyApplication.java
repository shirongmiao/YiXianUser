package com.example.lfy.myapplication;

import android.app.Application;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.Util.UserInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wyouflf on 15/10/28.
 */
public class MyApplication extends Application {

    private static MyApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        x.Ext.init(this);//Xutils初始化
    }

    //返回
    public static MyApplication getContext() {
        return context;
    }
}
