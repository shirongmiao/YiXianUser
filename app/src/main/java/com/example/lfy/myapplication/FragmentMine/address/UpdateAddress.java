package com.example.lfy.myapplication.FragmentMine.address;

import android.content.Intent;
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

import com.example.lfy.myapplication.Bean.AddressBean;
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
public class UpdateAddress extends AppCompatActivity {

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


    AddressBean list_address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_setaddress);
        Intent intent = getIntent();
        list_address = (AddressBean) intent.getSerializableExtra("address");
        initView();
        if (list_address.getIsdefault().equals("1")) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        }
    }

    private void initView() {
        return_black = (ImageView) findViewById(R.id.return_black);
        save_but = (Button) findViewById(R.id.add_update);

        add_name = (EditText) findViewById(R.id.add_name);
        city = (TextView) findViewById(R.id.add_city);
        add_phone = (EditText) findViewById(R.id.add_phone);
        add_village = (EditText) findViewById(R.id.add_village);
        add_address = (EditText) findViewById(R.id.add_address);
        gentleman = (RadioButton) findViewById(R.id.gentleman);
        lady = (RadioButton) findViewById(R.id.lady);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        return_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add_name.setText(list_address.getName());

        if (list_address.getSex().equals("1")) {
            gentleman.setChecked(true);
        } else {
            lady.setChecked(true);
        }
        city.setText(list_address.getCity());

        add_phone.setText(list_address.getPhone());
        add_village.setText(list_address.getDistrict());
        add_address.setText(list_address.getAddress());

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeAddressDialog mChangeAddressDialog = new ChangeAddressDialog(UpdateAddress.this);
                mChangeAddressDialog.setAddress(Variables.my.getAddressP(), Variables.my.getAddressC(), Variables.my.getAddressD());
                mChangeAddressDialog.show();
                mChangeAddressDialog.setAddresskListener(new ChangeAddressDialog.OnAddressCListener() {
                    @Override
                    public void onClick(String province, String city_item, String dis) {
                        list_address.setCity(province + "-" + city_item + "-" + dis);
                        Variables.my.setAddressP(province);
                        Variables.my.setAddressC(city_item);
                        Variables.my.setAddressD(dis);
                        city.setText(province + "-" + city_item + "-" + dis);
                    }
                });
            }
        });

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

                if (name.equals("")) {
                    Toast.makeText(getApplicationContext(), "请填写姓名", Toast.LENGTH_SHORT).show();
                } else if (!isMobileNO(phone)) {
                    Toast.makeText(getApplicationContext(), "请填写正确电话号码", Toast.LENGTH_SHORT).show();
                } else if (district.equals("")) {
                    Toast.makeText(getApplicationContext(), "请填写小区/学校", Toast.LENGTH_SHORT).show();
                } else if (address.equals("")) {
                    Toast.makeText(getApplicationContext(), "请填写详细地址", Toast.LENGTH_SHORT).show();
                } else {
                    Update_xUtil(name, sex, phone, district, address);
                }
            }
        });

//        Default_address.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                default_xUtil();
//            }
//        });

    }

    private void Update_xUtil(String name, String sex, String phone, String district, String address) {

        RequestParams params = new RequestParams(Variables.http_update_address);
        params.addBodyParameter("addressID", list_address.getId());
        params.addBodyParameter("name", name);
        params.addBodyParameter("city", list_address.getCity());
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
                return false;
            }

            @Override
            public void onSuccess(String result) {
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
                    if (checkBox.isChecked()) {
                        Default_xUtil();
                    } else {
                        finish();
                    }
                }
            }
        });
    }

    private void Default_xUtil() {

        RequestParams params = new RequestParams(Variables.http_Default_address);
        params.addBodyParameter("addressID", list_address.getId());
        params.addBodyParameter("customerID", Variables.my.getCustomerID());
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false;
            }

            @Override
            public void onSuccess(String result) {
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

//    private void default_xUtil() {
//        HttpUtils http = new HttpUtils();
//        RequestParams params = new RequestParams();
//        params.addBodyParameter("addressID", list_address.getId());
//        params.addBodyParameter("customerID", Variables.my.get(0).getCustomerID());
//        http.send(HttpRequest.HttpMethod.POST, Variables.http_Default_address, params, new RequestCallBack<String>() {
//            @Override
//            public void onFailure(HttpException arg0, String arg1) {
//                Toast.makeText(UpdateAddress.this, "请检查网络", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSuccess(ResponseInfo<String> arg0) {
//                Dialog dialog = new android.support.v7.app.AlertDialog.Builder(UpdateAddress.this)
//                        .setMessage("默认地址已修改")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).create();
//                dialog.show();
//            }
//        });
//
//    }

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
}
