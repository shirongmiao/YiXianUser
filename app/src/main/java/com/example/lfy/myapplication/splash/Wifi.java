package com.example.lfy.myapplication.splash;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.lfy.myapplication.FragmentHome.ChoosePoint.MyLocation;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.CustomProgressDialog;
import com.example.lfy.myapplication.Variables;

/**
 * Created by lfy on 2016/7/19.
 */
public class Wifi extends AppCompatActivity implements View.OnClickListener {

    RadioButton home;
    RadioButton classify;
    RadioButton car;
    RadioButton order;
    RadioButton mine;
    LinearLayout home_fruit_liner;

    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.splash_wifi);
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        initView();
        Button home_fruit = (Button) findViewById(R.id.home_fruit);
        home_fruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                into();
            }
        });
    }

    private void into(){
        if (isNetworkAvailable(Wifi.this)) {
            if (from.equals("first")) {
                Intent intent = new Intent(Wifi.this, MyLocation.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent1 = new Intent(Wifi.this, MainActivity.class);
                startActivity(intent1);
                finish();
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        //execute the task
//                    }
//                }, 1000);
            }
        } else {
            dialog = new CustomProgressDialog(this, "正在加载中", R.anim.frame2);
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    dialog.dismiss();
                    Toast.makeText(Wifi.this, "请检查网络", Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        }
    }

    private void initView() {
        car = (RadioButton) findViewById(R.id.main_car);
        home = (RadioButton) findViewById(R.id.main_home);
        classify = (RadioButton) findViewById(R.id.main_classify);
        order = (RadioButton) findViewById(R.id.main_order);
        mine = (RadioButton) findViewById(R.id.main_mine);
        home_fruit_liner = (LinearLayout) findViewById(R.id.home_fruit_liner);
    }

    public boolean isNetworkAvailable(Context context) {
        boolean flag = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            //去进行判断网络是否连接
            if (cm.getActiveNetworkInfo() != null) {
                flag = cm.getActiveNetworkInfo().isAvailable();
            }
            return flag;
        }
        return false;
    }

    CustomProgressDialog dialog = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_home:
                into();
                break;
            case R.id.main_classify:
                into();
                break;
            case R.id.main_order:
                into();
                break;
            case R.id.main_car:
                into();
                break;
            case R.id.main_mine:
                into();
                break;
        }
    }
}
