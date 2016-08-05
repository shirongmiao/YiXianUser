package com.example.lfy.myapplication.FragmentHome.ChoosePoint;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.Bean.PointUser;
import com.example.lfy.myapplication.FragmentHome.FragmentHome;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.UserInfo;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;

/**
 * Created by lfy on 2015/12/22.
 */
public class Point_Particular extends AppCompatActivity {

    HomePoint point;
    String pointid;
    TextView tv_name;
    TextView point_time;
    TextView point_address;
    ImageView point_photo;
    TextView point_prompt;
    TextView point_phone;
    LinearLayout call;
    Button ok_but;
    ImageView imageView1;
    UserInfo userInfo;

    ImageView user_phone;
    TextView user_name;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.all_point_particular);
        userInfo = new UserInfo(getApplication());
        initView();

        Intent intent = getIntent();
        pointid = intent.getStringExtra("point_url");

        xUtils(pointid);
        GetPhoto_xUtils(pointid);
    }

    private void initView() {
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_name = (TextView) findViewById(R.id.point_name);
        point_time = (TextView) findViewById(R.id.point_time);
        point_address = (TextView) findViewById(R.id.point_address);
        point_photo = (ImageView) findViewById(R.id.point_photo);
        point_phone = (TextView) findViewById(R.id.point_phone);
        call = (LinearLayout) findViewById(R.id.call);
        point_prompt = (TextView) findViewById(R.id.point_prompt);
        ok_but = (Button) findViewById(R.id.ok_but);

        user_phone = (ImageView) findViewById(R.id.user_phone);
        user_name = (TextView) findViewById(R.id.user_name);


        ok_but.setEnabled(false);
        ok_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_but();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);

                Uri data = Uri.parse("tel:" + point.getPhone());

                intent.setData(data);

                startActivity(intent);

            }
        });
    }

    private void click_but() {
        Variables.count = 0;
        Variables.point = point;
        FragmentHome.flag = true;

        //下次进入app时候application类会用到
        UserInfo userInfo = new UserInfo(getApplication());
        userInfo.setUserInfo("PARTID", pointid);
        if (!userInfo.getBooleanInfo("first")) {
            userInfo.setUserInfo("first", true);
            MyLocation.instant.finish();
            try {
                AllPoint.instant.finish();
            } catch (Exception e) {
            }
        }
        Intent intent = new Intent(Point_Particular.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    private void xUtils(String point) {
        RequestParams params = new RequestParams(Variables.http_getPoint);
        params.addBodyParameter("pointid", point);
        params.setCacheMaxAge(10000 * 60);
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
                    JSON(result);
                }
            }
        });

    }

    private void JSON(String json) {

        ok_but.setEnabled(true);
        point = new HomePoint();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray data = object.getJSONArray("Data");
            JSONObject everyone = data.getJSONObject(0);

            point.setID(everyone.getString("id"));
            String url = everyone.getString("img");
            url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
            point.setImages(url);
            point.setName(everyone.getString("name"));
            point.setDistrict(everyone.getString("district"));
            point.setAddress(everyone.getString("address"));
            point.setCity(everyone.getString("city"));
            point.setPhone(everyone.getString("phone"));
            point.setTime(everyone.getString("time"));
            point.setPrompt(everyone.getString("prompt"));
            point.setDeliveryPrice(everyone.getDouble("deliveryPrice"));
            point.setSendPrice(everyone.getDouble("sendPrice"));
            point.setFreePrice(everyone.getDouble("freePrice"));
            point.setState(everyone.getString("state"));
            tv_name.setText(everyone.getString("name"));
            point_address.setText(everyone.getString("address"));
            point_phone.setText(everyone.getString("phone"));
            point_prompt.setText(everyone.getString("prompt"));
            point_time.setText(everyone.getString("time"));

            x.image().bind(point_photo, url);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void GetPhoto_xUtils(String point) {
        RequestParams params = new RequestParams(Variables.http_getPointPhoto);
        params.addBodyParameter("point", point);
        params.setCacheMaxAge(10000 * 60);
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
                    GetPhoto_JSON(result);
                }
            }
        });

    }

    private void GetPhoto_JSON(String json) {
        PointUser pointUser = null;
        try {
            JSONObject object = new JSONObject(json);
            JSONArray data = object.getJSONArray("Data");
            JSONObject everyone = data.getJSONObject(0);
            pointUser = new PointUser();

            pointUser.setPhoneNameber(everyone.getString("PhoneNameber"));
            pointUser.setImage(everyone.getString("image"));
            pointUser.setUserName(everyone.getString("UserName"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String liu = pointUser.getImage();
            liu = liu.substring(liu.indexOf(",") + 1);
            Bitmap bitmap = Variables.base64ToBitmap(liu);
            user_phone.setImageBitmap(bitmap);
            user_name.setText("站长姓名：" + pointUser.getUserName());
            point_phone.setText(pointUser.getPhoneNameber());
        } catch (Exception e) {
        }
    }

}
