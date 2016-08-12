package com.example.lfy.myapplication.user_login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.MineBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.CustomProgressDialog;
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

/**
 * Created by lfy on 2015/12/10.
 */
public class Login extends AppCompatActivity implements View.OnClickListener {

    public static Login instance = null;

    private UserInfo userInfo;

    private static final String USER_NAME = "user_name";
    private static final String PASSWORD = "password";

    EditText login_name;
    EditText login_password;
    Button login_but;
    TextView login_register;
    TextView login_forget;
    ImageView login_return;

    FrameLayout login_all;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_every);
        instance = this;
        userInfo = new UserInfo(this);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        String phone = userInfo.getStringInfo(USER_NAME);
        String pass = userInfo.getStringInfo(PASSWORD);
        login_name.setText(phone);
        login_password.setText(pass);
        if (!phone.equals("") && !pass.equals("")) {
            Login_xUtils(phone, pass);
        }
        login_all.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        login_all.setVisibility(View.INVISIBLE);
    }

    private void initView() {
        login_name = (EditText) findViewById(R.id.login_name);
        login_name.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        login_password = (EditText) findViewById(R.id.login_password);
        login_but = (Button) findViewById(R.id.login_but);
        login_forget = (TextView) findViewById(R.id.login_forget);
        login_return = (ImageView) findViewById(R.id.login_return);
        login_register = (TextView) findViewById(R.id.login_register);
        login_all = (FrameLayout) findViewById(R.id.login_all);
        login_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                String phone = login_name.getText().toString();
//                Log.d("我失去了焦点", "执行我");
//                select_phone(phone);
            }
        });

        login_but.setOnClickListener(this);
        login_register.setOnClickListener(this);
        login_return.setOnClickListener(this);
        login_forget.setOnClickListener(this);
    }


    //登录
    private void Login_xUtils(final String name, final String password) {
        dialog = new CustomProgressDialog(this, "正在加载中", R.anim.frame2);
        dialog.show();

        String only_one = Send.getMD5(password);
        only_one = Send.getMD5(only_one);
        //获取设备信息
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        Variables.DEVICE_ID = DEVICE_ID;

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
                dialog.dismiss();
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
                    save(name, password);
                } else {
                    Toast.makeText(Login.this, "您的网络奔溃了！", Toast.LENGTH_SHORT).show();
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
                //获取购物车商品数量
                getCar_xUtils();
            } else {
                Toast.makeText(Login.this, json.getString("Msg"), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void save(String name, String password) {
        userInfo.setUserInfo(USER_NAME, name);
        userInfo.setUserInfo(PASSWORD, password);
    }

    CustomProgressDialog dialog = null;

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_but:
                String name = login_name.getText().toString();
                String password = login_password.getText().toString();
                Login_xUtils(name, password);
                break;
            case R.id.login_register:
                intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                break;
            case R.id.login_forget:
                intent = new Intent(Login.this, ForgetLoginPwd.class);
                startActivity(intent);
                break;
            case R.id.login_return:
                finish();
                break;
        }
    }

    private void Select_xUtils(String mobile) {
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
                    Select_JSON(result);
                }
            }
        });

    }

    private void Select_JSON(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String Ret = json.getString("Ret");
            if (Ret.equals("1")) {
                Toast.makeText(getApplicationContext(), "密码不正确！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "用户不存在！", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //获取购物车数据
    private void getCar_xUtils() {
        RequestParams params = new RequestParams(Variables.http_getCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("point", Variables.point.getID());
        params.setCacheMaxAge(1000 * 60);
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
                    Get_JSON(result);
                    dialog.dismiss();
                } else {
                    finish();
                }
            }
        });

    }

    private void Get_JSON(String json) {
        int num = 0;
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    num = num + everyone.getInt("ProductCount");
                }
            }
            Variables.count = num;
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
