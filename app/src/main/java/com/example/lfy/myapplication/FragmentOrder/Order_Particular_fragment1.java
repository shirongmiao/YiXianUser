package com.example.lfy.myapplication.FragmentOrder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.OrderBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.Listview;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/8/15.
 */
public class Order_Particular_fragment1 extends Fragment {
    TextView OrderNO;
    TextView SwiftNumber;
    TextView CreateTime;
    TextView SendTime;
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
    TextView order_particular_delivery;
    Listview order_particular_listview;


    List<CarDbBean> goods;
    OrderBean orders;

    public void setOrders(OrderBean orders) {
        this.orders = orders;
    }

    public void setGoods(List<CarDbBean> goods) {
        this.goods = goods;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_particular1, container, false);
        findView(view);

        indate();
        return view;

    }

    private void findView(View view) {
        OrderNO = (TextView) view.findViewById(R.id.order_particular_OrderNO);
        SwiftNumber = (TextView) view.findViewById(R.id.order_particular_SwiftNumber);
        CreateTime = (TextView) view.findViewById(R.id.order_particular_CreateTime);
        SendTime = (TextView) view.findViewById(R.id.order_particular_SendTime);
        CustomerSay = (TextView) view.findViewById(R.id.order_particular_CustomerSay);

        line_my_address = (LinearLayout) view.findViewById(R.id.line_my_address);
        order_particular_myname = (TextView) view.findViewById(R.id.order_particular_myname);
        order_particular_myaddress = (TextView) view.findViewById(R.id.order_particular_myaddress);
        order_particular_myphone = (TextView) view.findViewById(R.id.order_particular_myphone);
        point_name = (TextView) view.findViewById(R.id.point_name);
        point_phone = (TextView) view.findViewById(R.id.point_phone);
        call = (TextView) view.findViewById(R.id.call);
        point_address = (TextView) view.findViewById(R.id.point_address);
        send_type = (TextView) view.findViewById(R.id.send_type);

        order_particular_disprice = (TextView) view.findViewById(R.id.order_particular_disprice);
        order_particular_next = (TextView) view.findViewById(R.id.order_particular_next);
        order_particular_payprice = (TextView) view.findViewById(R.id.order_particular_payprice);
        order_particular_price = (TextView) view.findViewById(R.id.order_particular_price);
        order_particular_dis_all = (TextView) view.findViewById(R.id.order_particular_dis_all);
        order_particular_delivery = (TextView) view.findViewById(R.id.order_particular_delivery);
        order_particular_listview = (Listview) view.findViewById(R.id.order_particular_listview);

    }

    private void indate() {

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + Variables.point.getPhone());
                intent.setData(data);
                startActivity(intent);
            }
        });

        Order_item_adapter adapter = new Order_item_adapter(goods);
        order_particular_listview.setAdapter(adapter);


        OrderNO.setText(orders.getOrderNO());
        SwiftNumber.setText(orders.getSwiftNumber());
        CreateTime.setText(orders.getCreateTime().substring(0, 19).replaceAll("T", " "));
        SendTime.setText(orders.getDeliveryTime());
        CustomerSay.setText(orders.getCustomerSay());


        //目前只有配送到家
        if (orders.getDelivery().equals("0")) {
            send_type.setText("服务站自提");
            line_my_address.setVisibility(View.GONE);
            order_particular_myname.setText(Variables.my.getCustomerName() + "");
            order_particular_myphone.setText(Variables.my.getPhoneNameber() + "");

        } else {
            send_type.setText("配送到家");
            line_my_address.setVisibility(View.VISIBLE);
            addressID_xUtils(orders.getAddress());
        }
        point_address.setText(Variables.point.getAddress());
        point_phone.setText(Variables.point.getPhone());
        point_name.setText(Variables.point.getName());

        double discoupon = Double.parseDouble(orders.getDiscount());//优惠券价格

        if (orders.getIsNextDay().equals("1")) {
            order_particular_next.setText("￥" + AllNextDay());
            order_particular_disprice.setText((Math.round((discoupon) * 10000) / 10000.00) + "");
        } else {
            order_particular_next.setText("未选择会员");
            order_particular_disprice.setText((Math.round((discoupon) * 10000) / 10000.00) + "");
        }
        order_particular_price.setText("￥" + orders.getOrderPrice());
        order_particular_payprice.setText("实付:￥" + orders.getPayedPrice());
        order_particular_dis_all.setText("￥" + (Math.round((discoupon + AllNextDay()) * 10000) / 10000.00) + "");
        order_particular_delivery.setText("￥" + orders.getDelivery());
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
        try {
            Log.d("我是领取point", result);
            JSONObject jsonObject = new JSONObject(result);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                List<AddressBean> all = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    AddressBean address = new AddressBean();
                    address.setId(object.getString("id"));
                    address.setCustomerID(object.getString("customerID"));
                    address.setName(object.getString("name"));
                    address.setPointname(object.getString("pointname"));
                    address.setCity(object.getString("city"));
                    address.setPhone(object.getString("phone"));
                    address.setDistrict(object.getString("district"));
                    address.setAddress(object.getString("address"));
                    address.setSex(object.getString("sex"));
                    address.setIsdefault(object.getString("Isdefault"));
                    all.add(address);
                }

                order_particular_myname.setText(all.get(0).getName());
                order_particular_myaddress.setText(all.get(0).getAddress());
                order_particular_myphone.setText(all.get(0).getPhone());


            } else {
                Toast.makeText(getActivity(), "地址已被删除", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
