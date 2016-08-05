package com.example.lfy.myapplication.SubmitOrder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

/**
 * Created by lfy on 2016/6/29.
 */
public class PayType extends AppCompatActivity {

    TextView payType;
    TextView user;
    TextView address;
    TextView money;
    Button look_order;
    String type;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.submit_paytype);
        initView();
        Intent intent = getIntent();
        AddressBean list = (AddressBean) intent.getSerializableExtra("address");
        String price = intent.getStringExtra("money");
        type = intent.getStringExtra("payType");

        payType.setText(type);
        user.setText("收件人：" + list.getName() + "  " + list.getPhone());
        address.setText(list.getDistrict() + list.getAddress());
        money.setText("实付款：￥" + price);

    }

    private void initView() {
        payType = (TextView) findViewById(R.id.payType);
        user = (TextView) findViewById(R.id.user);
        address = (TextView) findViewById(R.id.address);
        money = (TextView) findViewById(R.id.money);
        imageView = (ImageView) findViewById(R.id.left);
        look_order = (Button) findViewById(R.id.look_order);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayType.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        look_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("支付成功")) {
                    MainActivity.jump(0);

                } else {
                    MainActivity.jump(1);
                }
                Intent intent = new Intent(PayType.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.classify.performClick();
        Intent intent = new Intent(PayType.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
