package com.example.lfy.myapplication.Group;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.Bean.GroupOrder;
import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.Bean.PointUser;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.UnsupportedEncodingException;

public class GroupOrderDetail extends AppCompatActivity {
    GroupOrder groupOrder;
    ImageView group_order_image, group_order_detail_return, group_order_detail_phoneimg;
    TextView group_order_title, group_order_content,
            group_order_customernameandphone, group_order_orderno,
            group_order_createtime, group_order_address,
            group_order_point, group_order_point_phone, group_order_price;
    AddressBean Delivery = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.activity_group_order_detail);
        Intent intent = getIntent();
        groupOrder = (GroupOrder) intent.getSerializableExtra("groupOrder");
        initView();
        setDate();
        OrderDetail_xUtils();
    }

    private void initView() {
        group_order_image = (ImageView) findViewById(R.id.group_order_image);
        group_order_title = (TextView) findViewById(R.id.group_order_title);
        group_order_content = (TextView) findViewById(R.id.group_order_content);
        group_order_customernameandphone = (TextView) findViewById(R.id.group_order_customernameandphone);
        group_order_orderno = (TextView) findViewById(R.id.group_order_orderno);
        group_order_createtime = (TextView) findViewById(R.id.group_order_createtime);
        group_order_address = (TextView) findViewById(R.id.group_order_address);
        group_order_point = (TextView) findViewById(R.id.group_order_point);
        group_order_point_phone = (TextView) findViewById(R.id.group_order_point_phone);
        group_order_price = (TextView) findViewById(R.id.group_order_price);
        group_order_detail_return = (ImageView) findViewById(R.id.group_order_detail_return);
        group_order_detail_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        group_order_detail_phoneimg = (ImageView) findViewById(R.id.group_order_detail_phoneimg);
    }

    private void setDate() {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setFailureDrawableId(R.mipmap.all_longding)
                //设置使用缓存
                .setUseMemCache(true)
                .setLoadingDrawableId(R.mipmap.all_longding)
                .build();
        x.image().bind(group_order_image, groupOrder.getImg(), imageOptions);
        group_order_title.setText(groupOrder.getTitle());
        group_order_content.setText(groupOrder.getPlace() + "-" + groupOrder.getStandard());
        group_order_orderno.setText(groupOrder.getOrderNO());
        String str = groupOrder.getCreateTime();
        str = str.replace("T", " ");
        str = str.substring(0, str.lastIndexOf("."));
        group_order_createtime.setText(str);
        if (groupOrder.getIsSingleBuy() == 1) {
            group_order_price.setText(groupOrder.getSinglePrice() + "");
        } else {
            group_order_price.setText(groupOrder.getTuanPrice() + "");
        }
    }

    private void OrderDetail_xUtils() {
        RequestParams params = new RequestParams(Variables.GetTuanOrder);
        params.addBodyParameter("timestamp", "0");
        params.addBodyParameter("payed", "1");
        params.addBodyParameter("ordertype", "0");
        params.addBodyParameter("search", groupOrder.getCustomerOrderNo());
        Log.d("获取用户团购订单详情", params.toString());
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

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
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
                    Order_JSON(result);
                } else {
//                    success();
                }
            }

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    private void Order_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                JSONObject everyone = data.getJSONObject(0);
                GetAddressInfo(everyone.getInt("address"));
                GetPointInfo(everyone.getString("point"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GetPointInfo(String pointid) {
        RequestParams params = new RequestParams(Variables.http_getPoint);
        params.addBodyParameter("pointid", pointid);
        Log.d("根据id获取服务站信息", params.toString());
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

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
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
                    Point_JSON(result);
                } else {
//                    success();
                }
            }

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    private void Point_JSON(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                JSONObject object = jsonArray.getJSONObject(0);
                PointUser point = new PointUser();
                point.setUserName(object.getString("UserName"));
                point.setPhoneNameber(object.getString("PhoneNameber"));
                point.setPoint(object.getString("name"));
                setPoint(point);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GetAddressInfo(int addressID) {
        RequestParams params = new RequestParams(Variables.http_address_particular);
        params.addBodyParameter("addressID", addressID + "");
        Log.d("根据id获取地址信息", params.toString());
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

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
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
                    Address_JSON(result);
                } else {
//                    success();
                }
            }

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    private void Address_JSON(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                JSONObject object = jsonArray.getJSONObject(0);
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
                Delivery = address;
                setAddress();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAddress() {
        group_order_customernameandphone.setText(Delivery.getName() + "\t" + Delivery.getPhone());
        group_order_address.setText(Delivery.getCity() + Delivery.getDistrict() + Delivery.getAddress());
    }

    private void setPoint(final PointUser point) {
        group_order_point.setText(point.getPoint());
        group_order_point_phone.setText(point.getUserName() + "\t" + point.getPhoneNameber());
        group_order_detail_phoneimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + point.getPhoneNameber());
                intent.setData(data);
                startActivity(intent);
            }
        });
    }
}
