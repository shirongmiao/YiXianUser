package com.example.lfy.myapplication.FragmentHome.coupon;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;

/**
 * Created by lfy on 2016/3/24.
 */
public class GetCoupon extends AppCompatActivity {

    ImageView button_back;

    TextView money;

    ImageView liquan_button;

    TextView bottom_text;

    TextView coupon_content;

    String content;
    FrameLayout coupon_dialog;
    View coupon_sure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.getcoupon);

        button_back = (ImageView) findViewById(R.id.button_back);
        money = (TextView) findViewById(R.id.money);
        liquan_button = (ImageView) findViewById(R.id.liquan_button);
        bottom_text = (TextView) findViewById(R.id.bottom_text);
        coupon_content = (TextView) findViewById(R.id.coupon_content);
        coupon_dialog = (FrameLayout) findViewById(R.id.coupon_dialog);
        coupon_sure = findViewById(R.id.coupon_sure);

        //查询是否领取过
        quan();

        liquan_button.setEnabled(false);

        liquan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lingqu();
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        coupon_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coupon_dialog.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void quan() {

        RequestParams params = new RequestParams(Variables.http_select_huoqu);
        params.addBodyParameter("customerid", Variables.my.getCustomerID());
        x.http().post(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                // 得到缓存数据, 缓存过期后不会进入这个方法.
                // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
                //
                // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
                //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
                //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
                //
                // * 如果信任该缓存返回 true, 将不再请求网络;
                //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
                //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
                //
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
                    try {
                        JSONObject object = new JSONObject(result);
                        String Ret = object.getString("Ret");
                        if (Ret.equals("1")) {
                            JSONArray data = object.getJSONArray("Data");
                            JSONObject everyone = data.getJSONObject(0);
                            content = everyone.getString("CouponPrice");
                            String CouponEnd = everyone.getString("CouponEnd").substring(0, 19);

                            CouponEnd = CouponEnd.replaceAll("T", "-");
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                            String newday = sDateFormat.format(new Date());
                            compare_date(newday, CouponEnd);
                        } else {
                            liquan_button.setEnabled(true);
                            bottom_text.setText("点击领取按钮，即可领取！");
                            liquan_button.setImageResource(R.mipmap.coupon_bottom);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void compare_date(String DATE1, String DATE2) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        try {
            Date dt1 = sDateFormat.parse(DATE1);
            Date dt2 = sDateFormat.parse(DATE2);
            if (dt1.getTime() < dt2.getTime()) {
                System.out.println(dt1.getTime() + "dt1 在dt2前,还差xxxx天" + dt2.getTime());

                bottom_text.setText("还有" + getDatePoor(dt2, dt1) + "可再次领取");
                liquan_button.setEnabled(false);
                liquan_button.setImageResource(R.mipmap.coupon_bottom_down);
            } else if (dt1.getTime() <= dt2.getTime()) {
                System.out.println("dt1在dt2后");
                bottom_text.setText("点击领取");
                liquan_button.setImageResource(R.mipmap.coupon_bottom);
                liquan_button.setEnabled(true);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }


    private void lingqu() {

        RequestParams params = new RequestParams(Variables.http_huoqu);
        params.addBodyParameter("customerid", Variables.my.getCustomerID());
        x.http().post(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                // 得到缓存数据, 缓存过期后不会进入这个方法.
                // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
                //
                // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
                //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
                //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
                //
                // * 如果信任该缓存返回 true, 将不再请求网络;
                //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
                //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
                //
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
                    try {
                        Log.d("我是领取point", result);
                        JSONObject object = new JSONObject(result);
                        String Ret = object.getString("Ret");
                        if (Ret.equals("1")) {
                            coupon_dialog.setVisibility(View.VISIBLE);
                            coupon_dialog.startAnimation(new MyScaler(1.0f, 1.0f, 0.0f, 1.0f, 700, coupon_dialog, false));
                            money.setText("￥" + content);
                            bottom_text.setText("领取成功");
                            liquan_button.setImageResource(R.mipmap.coupon_bottom_down);
                            String str = "￥" + content + "元现金已经存入到您的账户中,可在我的优惠券中查看";
                            SpannableStringBuilder builder = new SpannableStringBuilder(str);
                            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                            AbsoluteSizeSpan bigSpan = new AbsoluteSizeSpan(23, true);
                            int a = str.indexOf("我的优惠券");
                            builder.setSpan(redSpan, a, a + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            builder.setSpan(bigSpan, a, a + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            coupon_content.setText(builder);
                            quan();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public class MyScaler extends ScaleAnimation {
        private View mView;
        private FrameLayout.LayoutParams mLayoutParams;
        private int mMarginBottomFromY, mMarginBottomToY;
        private boolean mVanishAfter = false;

        public MyScaler(float fromX, float toX, float fromY, float toY, int duration, View view,
                        boolean vanishAfter) {
            super(fromX, toX, fromY, toY);
            setDuration(duration);
            mView = view;
            mVanishAfter = vanishAfter;
            mLayoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            int height = mView.getHeight();
            mMarginBottomFromY = (int) (height * fromY) + mLayoutParams.bottomMargin - height;
            mMarginBottomToY = (int) (0 - ((height * toY) + mLayoutParams.bottomMargin)) - height;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                int newMarginBottom = mMarginBottomFromY
                        + (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
                mLayoutParams.setMargins(mLayoutParams.leftMargin, mLayoutParams.topMargin,
                        mLayoutParams.rightMargin, newMarginBottom);
                mView.getParent().requestLayout();
            } else if (mVanishAfter) {
                mView.setVisibility(View.GONE);
            }
        }
    }

}
