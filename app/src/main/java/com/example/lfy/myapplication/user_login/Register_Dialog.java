package com.example.lfy.myapplication.user_login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.lfy.myapplication.FragmentMine.discount.Coupon;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

/**
 * Created by lfy on 2016/8/5.
 */
public class Register_Dialog extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.register_coupon);

        View view = findViewById(R.id.register_dialog_look);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register_Dialog.this, Coupon.class);
                startActivity(intent);
                finish();
            }
        });
        ImageButton imageButton = (ImageButton) findViewById(R.id.register_dialog_close);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
