package com.example.lfy.myapplication.Group;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

public class GroupOrderDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.activity_group_order_detail);
    }
}
