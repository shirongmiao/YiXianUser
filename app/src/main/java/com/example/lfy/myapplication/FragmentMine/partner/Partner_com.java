package com.example.lfy.myapplication.FragmentMine.partner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Android on 2016/1/20.
 */
public class Partner_com extends AppCompatActivity {

    EditText apply_mobile, apply_name, apply_email, apply_school;
    ImageView all_return;
    Button apply_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_partner_com);

        apply_submit = (Button) findViewById(R.id.apply_submit);
        all_return = (ImageView) findViewById(R.id.all_return);
        apply_mobile = (EditText) findViewById(R.id.apply_mobile);
        apply_name = (EditText) findViewById(R.id.apply_name);
        apply_email = (EditText) findViewById(R.id.apply_email);
        apply_school = (EditText) findViewById(R.id.apply_school);


        all_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        apply_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = apply_name.getText().toString();
                String weixin = apply_email.getText().toString();
                String mobile = apply_mobile.getText().toString();
                String school = apply_school.getText().toString();

                if (!isChineseNO(name)) {
                    Toast.makeText(getApplicationContext(), "姓名不能是特殊字符或数字", Toast.LENGTH_SHORT).show();
                } else if (weixin.equals("")) {
                    Toast.makeText(getApplicationContext(), "QQ/微信/邮箱 不能为空", Toast.LENGTH_SHORT).show();
                } else if (!isMobileNO(mobile)) {
                    Toast.makeText(getApplicationContext(), "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
                } else if (school.equals("")) {
                    Toast.makeText(getApplicationContext(), "地址 不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    apply(name, weixin, mobile, school);
                }
            }
        });
    }

    private void apply(String name, String weixin, String phone, String address) {


        RequestParams params = new RequestParams(Variables.http_applay);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        params.addBodyParameter("name", name);
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("wechat", weixin);
        params.addBodyParameter("address", address);
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
                    try {
                        JSONObject json = new JSONObject(result);
                        String Msg = json.getString("Msg");
                        Toast.makeText(Partner_com.this, Msg, Toast.LENGTH_SHORT).show();

                        finish();
                        Partner.instance.finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
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
        ///^[0-9a-zA-a]*$/
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static boolean isChineseNO(String Chinese) {
        ///^[0-9a-zA-a]*$/
        String telRegex = "^[A-Za-z\\u4e00-\u9fa5]+$";
        if (TextUtils.isEmpty(Chinese)) return false;
        else return Chinese.matches(telRegex);
    }

}
