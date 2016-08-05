package com.example.lfy.myapplication.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lfy on 2015/12/14.
 */
public class UserInfo {

    private String USER_INFO = "userInfo";
    private Context context;

    public UserInfo() {
    }

    public UserInfo(String USER_INFO, Context context) {
        this.USER_INFO = USER_INFO;
        this.context = context;
    }

    public UserInfo(Context context) {
        this.context = context;
    }

    public void setUserInfo(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putString(key, value);
        editor.commit();
    }

    public void setUserInfo(String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void clear() {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public String getStringInfo(String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context.MODE_APPEND);
        return sp.getString(key, "");
    }

    public boolean getBooleanInfo(String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO, Context.MODE_APPEND);
        return sp.getBoolean(key, false);
    }
}
