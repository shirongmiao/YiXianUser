package com.example.lfy.myapplication.FragmentMine.address;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.FragmentMine.address.widget.ChangeAddressDialog;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by lfy on 2016/4/1.
 */
public class InsertAddress extends AppCompatActivity {

    EditText add_name;
    RadioButton gentleman;
    RadioButton lady;
    EditText add_phone;
    TextView city;
    EditText add_village;
    EditText add_address;

    ImageView return_black;
    CheckBox checkBox;

    Button save_but;

    String city_str = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_setaddress);
        initView();

        save_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = add_name.getText().toString();
                String sex;
                if (gentleman.isChecked()) {
                    sex = "1";
                } else {
                    sex = "0";
                }
                String phone = add_phone.getText().toString();
                String district = add_village.getText().toString();
                String address = add_address.getText().toString();

                if (!isChineseNO(name)) {
                    Toast.makeText(getApplicationContext(), "不能输入特殊字符和数字", Toast.LENGTH_SHORT).show();
                } else if (!isMobileNO(phone)) {
                    Toast.makeText(getApplicationContext(), "请填写正确电话号码", Toast.LENGTH_SHORT).show();
                } else if (district.equals("")) {
                    Toast.makeText(getApplicationContext(), "请填写小区/学校", Toast.LENGTH_SHORT).show();
                } else if (address.equals("")) {
                    Toast.makeText(getApplicationContext(), "请填写详细地址", Toast.LENGTH_SHORT).show();
                } else if (city_str == null) {
                    Toast.makeText(getApplicationContext(), "请选择地址", Toast.LENGTH_SHORT).show();
                } else {
                    Insert_xUtil(name, sex, phone, district, address);
                }
            }
        });
    }

    private void initView() {


        add_name = (EditText) findViewById(R.id.add_name);
        city = (TextView) findViewById(R.id.add_city);
        add_phone = (EditText) findViewById(R.id.add_phone);
        add_village = (EditText) findViewById(R.id.add_village);
        add_address = (EditText) findViewById(R.id.add_address);
        gentleman = (RadioButton) findViewById(R.id.gentleman);
        lady = (RadioButton) findViewById(R.id.lady);
        return_black = (ImageView) findViewById(R.id.return_black);
        save_but = (Button) findViewById(R.id.add_update);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setVisibility(View.GONE);
        return_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeAddressDialog mChangeAddressDialog = new ChangeAddressDialog(InsertAddress.this);
                mChangeAddressDialog.setAddress(Variables.my.getAddressP(), Variables.my.getAddressC(), Variables.my.getAddressD());
                mChangeAddressDialog.show();
                mChangeAddressDialog.setAddresskListener(new ChangeAddressDialog.OnAddressCListener() {
                    @Override
                    public void onClick(String province, String city_item, String dis) {
                        Toast.makeText(getApplication(), province + "-" + city_item + "-" + dis, Toast.LENGTH_LONG).show();
                        city.setText(province + "-" + city_item + "-" + dis);
                        city_str = province + "-" + city_item + "-" + dis;
                    }
                });
            }
        });

    }

    private void Insert_xUtil(String name, String sex, String phone, String district, String address) {
        RequestParams params = new RequestParams(Variables.http_insert_address);
        params.addBodyParameter("customerID", Variables.my.getCustomerID());
        params.addBodyParameter("name", name);
        params.addBodyParameter("pointname", Variables.point.getID());
        params.addBodyParameter("city", city_str);
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("district", district);
        params.addBodyParameter("address", address);
        params.addBodyParameter("sex", sex);
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
                    finish();
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
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static boolean isChineseNO(String Chinese) {
        ///^[0-9a-zA-a]*$/"^[A-Za-z0-9\\u4e00-\u9fa5]+$"
        String telRegex = "^[A-Za-z\\u4e00-\u9fa5]+$";
        if (TextUtils.isEmpty(Chinese)) return false;
        else return Chinese.matches(telRegex);
    }

}
