package com.example.lfy.myapplication.FragmentOrder;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.OrderBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.Listview;
import com.example.lfy.myapplication.Variables;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by Android on 2016/1/13.
 */
public class Order_Particular extends AppCompatActivity {


    List<CarDbBean> goods;
    OrderBean orders;
    Button once_again;
    TextView OrderNO;
    TextView SwiftNumber;
    TextView CreateTime;
    TextView CustomerSay;

    TextView order_particular_myname;
    TextView order_particular_myaddress;
    TextView order_particular_myphone;
    TextView point_name;
    TextView point_phone;
    TextView call;
    TextView point_address;
    TextView send_type;
    LinearLayout line_my_address;

    TextView order_particular_disprice;
    TextView order_particular_next;
    TextView order_particular_dis_all;
    TextView order_particular_price;

    TextView order_particular_payprice;
    Listview order_particular_listview;
    ImageView imageView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.order_particular);
        Intent intent = getIntent();
        orders = (OrderBean) intent.getSerializableExtra("orders");
        goods = (List<CarDbBean>) intent.getSerializableExtra("order");

        init();
        indate();

        Order_item_adapter adapter = new Order_item_adapter(goods);
        order_particular_listview.setAdapter(adapter);
    }

    private void init() {
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        OrderNO = (TextView) findViewById(R.id.order_particular_OrderNO);
        once_again = (Button) findViewById(R.id.once_again);
        SwiftNumber = (TextView) findViewById(R.id.order_particular_SwiftNumber);
        CreateTime = (TextView) findViewById(R.id.order_particular_CreateTime);
        CustomerSay = (TextView) findViewById(R.id.order_particular_CustomerSay);

        line_my_address = (LinearLayout) findViewById(R.id.line_my_address);
        order_particular_myname = (TextView) findViewById(R.id.order_particular_myname);
        order_particular_myaddress = (TextView) findViewById(R.id.order_particular_myaddress);
        order_particular_myphone = (TextView) findViewById(R.id.order_particular_myphone);
        point_name = (TextView) findViewById(R.id.point_name);
        point_phone = (TextView) findViewById(R.id.point_phone);
        call = (TextView) findViewById(R.id.call);
        point_address = (TextView) findViewById(R.id.point_address);
        send_type = (TextView) findViewById(R.id.send_type);

        order_particular_disprice = (TextView) findViewById(R.id.order_particular_disprice);
        order_particular_next = (TextView) findViewById(R.id.order_particular_next);
        order_particular_payprice = (TextView) findViewById(R.id.order_particular_payprice);
        order_particular_price = (TextView) findViewById(R.id.order_particular_price);
        order_particular_dis_all = (TextView) findViewById(R.id.order_particular_dis_all);
        order_particular_listview = (Listview) findViewById(R.id.order_particular_listview);

    }

    private void indate() {
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        once_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Order_Particular.this, Order_Once.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("orders", (Serializable) orders);
//                bundle.putSerializable("order", (Serializable) goods);
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + Variables.point.getPhone());
                intent.setData(data);
                startActivity(intent);
            }
        });

        if (orders.getOrderType().equals("0")) {//未付款
            once_again.setVisibility(View.GONE);
        }
        try {
            if (goods.get(0).getTitle().contains("新用户"))
                once_again.setVisibility(View.GONE);

        } catch (Exception e) {
            once_again.setVisibility(View.VISIBLE);
        }


        OrderNO.setText(orders.getOrderNO());
        SwiftNumber.setText(orders.getSwiftNumber());
        CreateTime.setText(orders.getCreateTime().substring(0, 19).replaceAll("T", " "));
        CustomerSay.setText(orders.getCustomerSay());

        if (orders.getDelivery().equals("0")) {
            send_type.setText("服务站自提");
            line_my_address.setVisibility(View.GONE);
            order_particular_myname.setText(Variables.my.getCustomerName() + "");
            order_particular_myphone.setText(Variables.my.getPhoneNameber() + "");

        } else {
            send_type.setText("配送到家 ");
            line_my_address.setVisibility(View.VISIBLE);
            addressID_xUtils(orders.getDelivery());
        }
        point_address.setText(Variables.point.getAddress());
        point_phone.setText(Variables.point.getPhone());
        point_name.setText(Variables.point.getName());

        double discoupon = Double.parseDouble(orders.getDiscount());//优惠券价格

        if (orders.getIsNextDay().equals("1")) {
            order_particular_next.setText("￥" + AllNextDay());
            order_particular_disprice.setText((Math.round((discoupon - AllNextDay()) * 10000) / 10000.00) + "");
        } else {
            order_particular_next.setText("未选择会员");
            order_particular_disprice.setText((Math.round((discoupon) * 10000) / 10000.00) + "");
        }
        order_particular_price.setText("￥" + orders.getOrderPrice());
        order_particular_payprice.setText("实付:￥" + orders.getPayedPrice());
        order_particular_dis_all.setText("￥" + (Math.round((discoupon) * 10000) / 10000.00) + "");
    }

    public double AllNextDay() {
        double all = 0;
        if (orders != null) {
            for (int i = 0; i < goods.size(); i++) {
                double p = goods.get(i).getPromotionPrice();
                double d = goods.get(i).getPrice();
                double one = goods.get(i).getProductCount() * (d - p);
                all = all + one;
            }
            all = (Math.round(all * 10000) / 10000.00);
        }
        return all;
    }

    private void addressID_xUtils(String id) {

        RequestParams params = new RequestParams(Variables.http_address_particular);
        params.addBodyParameter("addressID", id);
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }

            @Override
            public void onSuccess(String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    JSON(result);
                }
            }
        });
    }

    private void JSON(String result) {
//        try {
//            Log.d("我是领取point", result);
//            JSONObject jsonObject = new JSONObject(result);
//            String Ret = jsonObject.getString("Ret");
//            if (Ret.equals("1")) {
//                List<Address_bean> all = new ArrayList<Address_bean>();
//                JSONArray jsonArray = jsonObject.getJSONArray("Data");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject object = jsonArray.getJSONObject(i);
//                    Address_bean address = new Address_bean();
//                    address.setId(object.getString("id"));
//                    address.setCustomerID(object.getString("customerID"));
//                    address.setName(object.getString("name"));
//                    address.setPointname(object.getString("pointname"));
//                    address.setCity(object.getString("city"));
//                    address.setPhone(object.getString("phone"));
//                    address.setDistrict(object.getString("district"));
//                    address.setAddress(object.getString("address"));
//                    address.setSex(object.getString("sex"));
//                    address.setIsdefault(object.getString("Isdefault"));
//                    all.add(address);
//                }
//
//                order_particular_myname.setText(all.get(0).getName());
//                order_particular_myaddress.setText(all.get(0).getAddress());
//                order_particular_myphone.setText(all.get(0).getPhone());
//
//
//            } else {
//                Toast.makeText(getApplicationContext(), "地址已被删除", Toast.LENGTH_SHORT).show();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


}