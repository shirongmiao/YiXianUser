package com.example.lfy.myapplication.FragmentOrder;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.OrderBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import java.util.List;

/**
 * Created by Android on 2016/1/13.
 */
public class Order_Particular extends AppCompatActivity {

    List<CarDbBean> goods;
    OrderBean orders;

    ImageView imageView1;
    TextView particular_zhuantai;
    TextView particular_xiangqing;

    private FragmentManager fm;
    private Order_Particular_fragment1 fg1;
    private Order_Particular_fragment2 fg2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.order_particular);
        fm = getSupportFragmentManager();

        Intent intent = getIntent();
        orders = (OrderBean) intent.getSerializableExtra("orders");
        goods = (List<CarDbBean>) intent.getSerializableExtra("order");
        init();
        chick("2");
    }

    private void chick(String num) {
        FragmentTransaction ft1 = fm.beginTransaction();
        hideFragments(ft1);
        if (num.endsWith("1")) {
            if (fg1 == null) {
                fg1 = new Order_Particular_fragment1();// 如果fg1为空，则创建一个并添加到界面上
                fg1.setGoods(goods);
                fg1.setOrders(orders);
                ft1.add(R.id.particular_fragment, fg1);
            } else {
                ft1.show(fg1);// 如果MessageFragment不为空，则直接将它显示出来
            }
        } else {
            if (fg2 == null) {
                fg2 = new Order_Particular_fragment2();// 如果fg1为空，则创建一个并添加到界面上
                fg2.setOrderId(orders.getOrderID());
                ft1.add(R.id.particular_fragment, fg2);
            } else {
                ft1.show(fg2);// 如果MessageFragment不为空，则直接将它显示出来
            }
        }
        ft1.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fg1 != null) {
            transaction.hide(fg1);
        }
        if (fg2 != null) {
            transaction.hide(fg2);
        }
    }

    private void init() {
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        particular_zhuantai = (TextView) findViewById(R.id.particular_zhuantai);
        particular_xiangqing = (TextView) findViewById(R.id.particular_xiangqing);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        particular_zhuantai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chick("2");
            }
        });
        particular_xiangqing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chick("1");
            }
        });

    }
}
