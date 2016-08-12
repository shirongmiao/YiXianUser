package com.example.lfy.myapplication.user_login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.MineBean;
import com.example.lfy.myapplication.FragmentMine.discount.Coupon;
import com.example.lfy.myapplication.FragmentMine.help.Help;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.Send;
import com.example.lfy.myapplication.Util.UserInfo;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.xml.sax.InputSource;

/**
 * Created by lfy on 2015/12/10.
 */
public class Register extends AppCompatActivity {

    private static final String USER_NAME = "user_name";
    private static final String PASSWORD = "password";

    private TimeCount time;

    EditText register_test;
    EditText register_phone;
    EditText register_password;

    Button register_but;
    Button register_send;
    CheckBox checkbos_sure;
    FrameLayout register_all;

    int test;

    String new_phone;

    ImageView register_return;

    private UserInfo userInfo;

    private TextView yixian_web;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register);
        userInfo = new UserInfo(this);
        initView();
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象

    }

    private void initView() {
        register_return = (ImageView) findViewById(R.id.register_return);
        register_all = (FrameLayout) findViewById(R.id.register_all);
        register_send = (Button) findViewById(R.id.register_send);

        register_send.setBackgroundResource(R.drawable.round_hollow);
        register_send.setTextColor(0xff000000);

        register_test = (EditText) findViewById(R.id.register_test);
        register_phone = (EditText) findViewById(R.id.register_phone);
        register_password = (EditText) findViewById(R.id.register_password);

        register_but = (Button) findViewById(R.id.register_but);
        checkbos_sure = (CheckBox) findViewById(R.id.checkbos_sure);
        yixian_web = (TextView) findViewById(R.id.yixian_web);
        register_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        yixian_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Help.class);
                intent.putExtra("url", "www.baifenxian.com/xieyi/UserProtocol.html");
                startActivity(intent);
            }
        });
        register_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile = register_phone.getText().toString();

                //设置发送短信按钮不能再次点击
                register_send.setClickable(false);
                //设置短信验证码获得焦点
                register_test.setFocusable(true);
                register_test.setFocusableInTouchMode(true);
                register_test.requestFocus();

                register_send.setBackgroundResource(R.drawable.round_greay);
                register_send.setTextColor(0xff8e8e93);

                xUtils(mobile);
                register_send.setText("稍等...");
            }
        });

        register_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String va = register_test.getText().toString();
                if (va.equals(test + "")) {
                    if (checkbos_sure.isChecked()) {
                        Submit(register_phone.getText().toString(), register_password.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "请确认勾选用户协议", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        register_all.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        register_all.setVisibility(View.INVISIBLE);
    }

    private void Send_test(String mobile) {
        //向用户发送你个短信
        RequestParams params = new RequestParams(Variables.http_duanxin);
        params.addBodyParameter("phone", mobile);
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
                register_send.setClickable(true);
                register_send.setBackgroundResource(R.drawable.round_hollow);
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
                        int msg = jsonObject.getInt("Msg");
                        test = msg;
                        time.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            register_send.setText("重新验证");
            register_send.setClickable(true);
            register_send.setBackgroundResource(R.drawable.round_hollow);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            register_send.setClickable(false);

            register_send.setBackgroundResource(R.drawable.round_greay);
            register_send.setTextColor(0xff8e8e93);

            register_send.setText(millisUntilFinished / 1000 + "秒");
        }
    }


    private void xUtils(String mobile) {
        //查询用户是否存在
        RequestParams params = new RequestParams(Variables.http_phone);
        params.addBodyParameter("username", mobile);
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
                register_send.setClickable(true);
                register_send.setBackgroundResource(R.drawable.round_hollow);
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
            JSONObject json = new JSONObject(result);
            String Ret = json.getString("Ret");
            if (Ret.equals("1")) {
                Toast.makeText(getApplicationContext(), "该用户已经存在", Toast.LENGTH_SHORT).show();
                register_send.setText("获取验证码");
                register_send.setClickable(true);
                register_send.setBackgroundResource(R.drawable.round_hollow);
            } else {
                new_phone = register_phone.getText().toString();
                Send_test(new_phone);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void Submit(final String mobile, final String password) {

        String only_one = Send.getMD5(password);
        only_one = Send.getMD5(only_one);

        //注册成功
        RequestParams params = new RequestParams(Variables.http_register);
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("pwd", only_one);
        params.addBodyParameter("name", mobile);
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
                register_send.setClickable(true);
                register_send.setBackgroundResource(R.drawable.round_hollow);
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
                    request(result, mobile, password);
                }
            }
        });

    }

    private void request(String result, String mobile, String password) {
        try {
            JSONObject json = new JSONObject(result);
            String ret = json.getString("Ret");
            if (ret.equals("1")) {
                userInfo.setUserInfo(USER_NAME, mobile);
                userInfo.setUserInfo(PASSWORD, password);
                login_user(mobile, password);
            } else {
                Toast.makeText(getApplication(), "请重新填写", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void login_user(final String name, final String password) {

        String only_one = Send.getMD5(password);
        only_one = Send.getMD5(only_one);

        RequestParams params = new RequestParams(Variables.http_login);
        params.addBodyParameter("username", name);
        params.addBodyParameter("pwd", only_one);
        params.addBodyParameter("devicetoken", Variables.DEVICE_ID);
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
                    login_JSON(result);
                }
            }
        });

    }

    public void login_JSON(String str) {
        try {
            JSONObject json = new JSONObject(str);
            String msg = json.getString("Ret");
            if (msg.equals("1")) {
                JSONArray data = json.getJSONArray("Data");
                JSONObject Customer = data.getJSONObject(0);
                MineBean communication = new MineBean();
                communication.setCustomerID(Customer.getString("CustomerID"));//传值给全局
                communication.setUserName(Customer.getString("UserName"));
                communication.setPassword(Customer.getString("Password"));
                communication.setCustomerName(Customer.getString("CustomerName"));
                communication.setSex(Customer.getString("Sex"));
                communication.setBirthday(Customer.getString("Birthday"));
                communication.setPhoneNameber(Customer.getString("PhoneNameber"));
                communication.setEmailAddress(Customer.getString("EmailAddress"));
                communication.setIsChecked(Customer.getString("IsChecked"));
                communication.setCheckedType(Customer.getString("CheckedType"));
                communication.setRecommendNo(Customer.getString("RecommendNo"));
                communication.setRecommendName(Customer.getString("RecommendName"));
                communication.setCustomerLevel(Customer.getString("CustomerLevel"));
                communication.setCustomerIntegral(Customer.getString("CustomerIntegral"));
                // communication.setMyCoupon(Customer.getString("MyCoupon"));
                communication.setAccountState(Customer.getString("AccountState"));
                communication.setCreateTime(Customer.getString("CreateTime"));
                communication.setLastLoginTime(Customer.getString("LastLoginTime"));
                communication.setImage(Customer.getString("image"));
                communication.setAuthority(Customer.getString("authority"));
                communication.setPoint(Customer.getString("point"));
                communication.setPartner(Customer.getString("partner"));
                communication.setWeixinNo(Customer.getString("weixinNo"));
                communication.setPartnerName(Customer.getString("partnerName"));

                Variables.my = communication;

                Intent intent = new Intent(Register.this, Register_Dialog.class);
                startActivity(intent);

                Login.instance.finish();
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
