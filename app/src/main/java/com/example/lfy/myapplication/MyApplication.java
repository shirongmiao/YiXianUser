package com.example.lfy.myapplication;

import android.app.Application;
import android.util.Log;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import org.xutils.x;

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

        umeng();
    }

    private void umeng() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.d("我是友盟", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    //返回
    public static MyApplication getContext() {
        return context;
    }
}
