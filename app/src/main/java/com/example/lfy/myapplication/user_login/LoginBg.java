package com.example.lfy.myapplication.user_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

/**
 * Created by lfy on 2016/8/2.
 */
public class LoginBg extends AppCompatActivity {
    public static LoginBg instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.login_bg);
        instance = this;
        Intent intent = new Intent(LoginBg.this, Login.class);
        startActivity(intent);

    }
}
