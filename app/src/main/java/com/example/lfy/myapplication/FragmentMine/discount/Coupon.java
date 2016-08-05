package com.example.lfy.myapplication.FragmentMine.discount;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CouponBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lfy on 2016/3/21.
 */
public class Coupon extends AppCompatActivity {

    ListView discount;
    List<CouponBean> coupon_beans = null;
    Double order_price;
    Coupon_adapter adapter;
    ImageView all_return;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_discount);
        Intent intent = getIntent();
        order_price = intent.getDoubleExtra("from", 0);
        initView();
        TimeStamp();

    }

    private void initView() {
        discount = (ListView) findViewById(R.id.discount);
        all_return = (ImageView) findViewById(R.id.all_return);
        if (order_price > 0) {
            discount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    double price = Double.parseDouble(coupon_beans.get(position).getCouponMan());
                    if (price <= order_price) {
                        if (coupon_beans.get(position).getCouponState().equals("0")) {
                            String end = coupon_beans.get(position).getCouponEnd().substring(0, 19);
                            end = end.replaceAll("T", "-");
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                            String newday = sDateFormat.format(new Date());
                            Log.d("我是结束", end);
                            Log.d("我是现在", newday);
                            int a = compare_date(end, newday);
                            if (a == 1) {
                                Intent intent = new Intent();
                                intent.putExtra("couponId", coupon_beans.get(position).getCouponID());
                                intent.putExtra("couponPrice", coupon_beans.get(position).getCouponPrice());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    } else {
                        Dialog dialog = new android.support.v7.app.AlertDialog.Builder(Coupon.this)
                                .setMessage("订单未满" + price + "元")
                                .setPositiveButton("确定", null)
                                .setNegativeButton("取消", null).create();
                        dialog.show();
                    }
                }
            });
        }

        all_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public int compare_date(String DATE1, String DATE2) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        try {
            Date dt1 = sDateFormat.parse(DATE1);
            Date dt2 = sDateFormat.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private void TimeStamp() {
        RequestParams params = new RequestParams(Variables.http_time);
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
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String longTime = jsonObject.getString("Msg");
                        Coupon_xUtils(longTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void Coupon_xUtils(String timestame) {

        RequestParams params = new RequestParams(Variables.http_discount);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        params.addBodyParameter("timestamp", timestame);
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

    public void JSON(String str) {
        try {
            JSONObject json = new JSONObject(str);
            String Ret = json.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = json.getJSONArray("Data");
                coupon_beans = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject every = data.getJSONObject(i);
                    CouponBean coupon_bean = new CouponBean();
                    coupon_bean.setCouponID(every.getString("CouponID"));//优惠券ID-
                    coupon_bean.setCouponStart(every.getString("CouponStart"));//开始-
                    coupon_bean.setCouponEnd(every.getString("CouponEnd"));//结束-
                    coupon_bean.setCouponPrice(every.getInt("CouponPrice"));//价格-
                    coupon_bean.setCouponInfo(every.getString("CouponInfo"));//信息，满十元使用-
                    coupon_bean.setName(every.getString("name"));//新用户注册，老用户反馈-
                    coupon_bean.setCouponState(every.getString("CouponState"));//状态-
                    coupon_bean.setCouponMan(every.getString("CouponMan"));//描述-
                    coupon_bean.setCustomerid(every.getString("Customerid"));//用户ID
                    coupon_bean.setCouponUsed(every.getString("CouponUsed"));
                    coupon_beans.add(coupon_bean);
                }
                adapter = new Coupon_adapter(coupon_beans);
                discount.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
