package com.example.lfy.myapplication.FragmentMine.balance;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by lfy on 2015/12/10.
 */
public class ForgetBalancePwd extends AppCompatActivity {

    public static ForgetBalancePwd instance = null;

    private TimeCount time;
    EditText user_phone;
    Button ok_but;
    Button text_ma;
    int yan;
    EditText validation;
    String duanxin;

    ImageView imageView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.forget_pwd);
        instance = this;
        TextView tubiao = (TextView) findViewById(R.id.tubiao);
        tubiao.setText("忘记密码");
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        init();
    }

    private void init() {
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        text_ma = (Button) findViewById(R.id.text_ma);
        validation = (EditText) findViewById(R.id.validation);
        validation.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        user_phone = (EditText) findViewById(R.id.user_phone);
        user_phone.setText(Variables.my.getPhoneNameber());
        user_phone.setKeyListener(null);
        ok_but = (Button) findViewById(R.id.ok_but);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text_ma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile = Variables.my.getPhoneNameber();
                duanxin = mobile;
                boolean flag2 = isMobileNO(mobile);
                if (flag2) {
                    text_ma.setClickable(false);
                    text_ma.setBackgroundResource(R.drawable.round_greay);
                    duanxin(mobile);

                    //验证码输入框获得焦点
                    validation.setFocusable(true);
                    validation.setFocusableInTouchMode(true);
                    validation.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) validation.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(validation, 0);

                } else {
                    Toast.makeText(getApplicationContext(), "请输入正确号码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ok_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String va = validation.getText().toString();
                if (va.equals(yan + "")) {
                    Intent intent = new Intent(ForgetBalancePwd.this, SetPassWord.class);
                    intent.putExtra("phone", duanxin);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void duanxin(String mobile) {

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
                text_ma.setClickable(true);
                text_ma.setBackgroundResource(R.drawable.round_hollow);
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
                    try {
                        JSONObject jsonObject  = new JSONObject(result);
                        int  msg = jsonObject.getInt("Msg");
                        yan = msg;

                        time.start();
                        text_ma.setText("稍等...");

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
            text_ma.setText("重新验证");
            text_ma.setClickable(true);
            text_ma.setBackgroundResource(R.drawable.round_green);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            text_ma.setClickable(false);
            text_ma.setBackgroundResource(R.drawable.round_greay);
            text_ma.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    //正则表达式判断电话号码

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

}
