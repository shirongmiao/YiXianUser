package com.example.lfy.myapplication.user_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by lfy on 2015/12/10.
 */
public class SetLoginPwd extends AppCompatActivity {

    EditText user_password, user_password_two, user_name, reference;

    String mobile;
    Button ok_but;
    ImageView return_but;
    String pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setloginpwd);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("phone");

        init();

        return_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ok_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = user_password.getText().toString();
                String password_two = user_password_two.getText().toString();
                if (!password.equals("")) {
                    if (password.equals(password_two)) {
                        pass = Send.getMD5(password);
                        pass = Send.getMD5(pass);

                        request(mobile, pass);

                    } else {
                        Toast.makeText(getApplication(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplication(), "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void init() {

        return_but = (ImageView) findViewById(R.id.return_but);
        ok_but = (Button) findViewById(R.id.ok_but);
        user_password = (EditText) findViewById(R.id.user_password);
        user_password_two = (EditText) findViewById(R.id.user_password_two);
    }


    private void request(String mobile, String password) {
        RequestParams params = new RequestParams(Variables.http_update_password);
        params.addBodyParameter("phone", mobile);
        params.addBodyParameter("password", password);
        x.http().post(params, new Callback.CacheCallback<String>() {
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
                    String msg = null;
                    try {
                        JSONObject json = new JSONObject(str);
                        msg = json.getString("Msg");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    if (msg.equals("密码更新成功")) {
                        finish();
                        ForgetLoginPwd.instance.finish();
                    } else {
                        Toast.makeText(getApplication(), msg + "请重新填写", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
