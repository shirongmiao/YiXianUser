package com.example.lfy.myapplication.FragmentMine.feedback;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * Created by Android on 2016/1/20.
 */
public class Feedback extends AppCompatActivity {
    EditText suggest, weixin;
    TextView submit;
    ImageView back;
    RadioButton goods_suggest, system_suggest, other_suggest;
    RadioGroup all_RadioButton;

    String radio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_feedback);
        initView();
    }

    private void initView() {
        weixin = (EditText) findViewById(R.id.my_phone);
        suggest = (EditText) findViewById(R.id.my_suggest);
        submit = (TextView) findViewById(R.id.feedback_submit);
        back = (ImageView) findViewById(R.id.imageView1);
        goods_suggest = (RadioButton) findViewById(R.id.goods_suggest);
        system_suggest = (RadioButton) findViewById(R.id.system_suggest);
        other_suggest = (RadioButton) findViewById(R.id.other_suggest);
        all_RadioButton = (RadioGroup) findViewById(R.id.all_RadioButton);
        all_RadioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) Feedback.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                radio = rb.getText().toString();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = suggest.getText().toString();
                String we = weixin.getText().toString();
                if (content.equals("")) {
                    Toast.makeText(getApplication(), "内容不能为空", Toast.LENGTH_SHORT).show();
                } else if (we.equals("")) {
                    Toast.makeText(getApplication(), "微信不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    feedback(content, we);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setReadiButton();
    }

    private void setReadiButton() {
        int width = Variables.PhoneWidth / 16;     // 屏幕宽度（像素）
//        int height = metric.heightPixels;   // 屏幕高度（像素）
//        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        Drawable drawable = getResources().getDrawable(R.drawable.mine_feedback_bg);
        Drawable drawable2 = getResources().getDrawable(R.drawable.mine_feedback_bg);
        Drawable drawable3 = getResources().getDrawable(R.drawable.mine_feedback_bg);
        drawable.setBounds(0, 0, width, width);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        drawable2.setBounds(0, 0, width, width);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        drawable3.setBounds(0, 0, width, width);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        goods_suggest.setCompoundDrawables(drawable, null, null, null);
        system_suggest.setCompoundDrawables(drawable2, null, null, null);
        other_suggest.setCompoundDrawables(drawable3, null, null, null);
    }

    private void feedback(String content, String we) {

        RequestParams params = new RequestParams(Variables.http_feedback);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        params.addBodyParameter("content", content);
        params.addBodyParameter("type", radio);
        params.addBodyParameter("wechat", we);

        Log.d("我是商品反馈", content + "--" + radio + "--" + we);
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
                        JSONObject json = new JSONObject(result);
                        String Msg = json.getString("Msg");
                        Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
