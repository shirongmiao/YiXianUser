package com.example.lfy.myapplication.FragmentMine.balance;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.Send;
import com.example.lfy.myapplication.Variables;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by lfy on 2016/4/14.
 */
public class SetPassWord extends AppCompatActivity {
    ImageView new_return;
    PasswordInputView password2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_setpassword);
        new_return = (ImageView) findViewById(R.id.new_return);
        final PasswordInputView password1 = (PasswordInputView) findViewById(R.id.my_password);
        password2 = (PasswordInputView) findViewById(R.id.my_password2);
        password1.addTextChangedListener(mTextWatcher);

        new_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        password1.setOnFocusChangeListener(new View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    password1.setBackgroundColor(Color.WHITE);
                    password1.setBorderColor(Color.RED);
                } else {
                    // 此处为失去焦点时的处理内容
                    password1.setBackgroundColor(Color.WHITE);
                    password1.setBorderColor(Color.GRAY);
                }
            }
        });

        password2.setOnFocusChangeListener(new View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    password2.setBackgroundColor(Color.WHITE);
                    password2.setBorderColor(Color.RED);
                } else {
                    // 此处为失去焦点时的处理内容
                    password2.setBackgroundColor(Color.WHITE);
                    password2.setBorderColor(Color.GRAY);
                }
            }
        });

        Button sure = (Button) findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd1 = password1.getText().toString();
                String pwd2 = password2.getText().toString();
                if (pwd1.equals(pwd2)) {
                    pwd_xUtils(pwd2);
                } else {
                    password1.setText("");
                    password2.setText("");
                    Toast.makeText(SetPassWord.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
//			mTextView.setText(s);//将输入的内容实时显示
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() == 6) {
                // 获取编辑框焦点
                password2.setFocusable(true);
                password2.setFocusableInTouchMode(true);
                password2.requestFocus();
            }
        }
    };


    //检查密码
    private void pwd_xUtils(String pwd2) {

        String only_one = Send.getMD5(pwd2);
        only_one = Send.getMD5(only_one);
        Log.d("我是密码", only_one);

        RequestParams params = new RequestParams(Variables.http_setting_password);
        params.addBodyParameter("TopUpPwd", only_one);
        params.addBodyParameter("customerId", Variables.my.getCustomerID());
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
                Toast.makeText(x.app(), "请检查网络", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    String str = result;
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        Log.d("设置密码success", str);
                        String Ret = jsonObject.getString("Ret");
                        if (Ret.equals("1")) {
                            Balance.flag = true;
                            finish();
                            ForgetBalancePwd.instance.finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}
