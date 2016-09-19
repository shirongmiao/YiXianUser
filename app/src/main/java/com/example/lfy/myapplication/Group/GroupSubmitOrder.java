package com.example.lfy.myapplication.Group;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.Bean.GroupOrder;
import com.example.lfy.myapplication.FragmentMine.address.ManageAddress;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.SubmitOrder.PayType;
import com.example.lfy.myapplication.Util.pay_dialog.DialogWidget;
import com.example.lfy.myapplication.Variables;
import com.pingplusplus.android.Pingpp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;

public class GroupSubmitOrder extends AppCompatActivity implements View.OnClickListener {
    GroupOrder groupGoods;
    TextView group_submit_price, group_submit_Default, group_submit_name, group_submit_phone, group_submit_address;
    AddressBean Delivery = null;
    LinearLayout group_submit_choose, group_submit_alipay, group_submit_wx;
    ImageView group_submit_return;
    //防止多次点击
    boolean flag = true;
    boolean submit_goods = false;
    //弹出密码输入框
    DialogWidget mDialogWidget;
    //获取change
    String charge;
    //IsSingleBuy 2:用户参团 1：单独购  0:开团
    int IsSingleBuy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.activity_group_submit_order);
        Intent intent = getIntent();
        groupGoods = (GroupOrder) intent.getSerializableExtra("groupgood");
        IsSingleBuy = intent.getIntExtra("IsSingleBuy", 0);
        initView();
        address_xUtil();
    }

    public void initView() {
        group_submit_choose = (LinearLayout) findViewById(R.id.group_submit_choose);
        group_submit_choose.setOnClickListener(this);
        group_submit_price = (TextView) findViewById(R.id.group_submit_price);
        if (IsSingleBuy == 1) {
            group_submit_price.setText("￥" + groupGoods.getSinglePrice() + "元");
        } else {
            group_submit_price.setText("￥" + groupGoods.getTuanPrice() + "元");
        }
        group_submit_Default = (TextView) findViewById(R.id.group_submit_Default);
        group_submit_phone = (TextView) findViewById(R.id.group_submit_phone);
        group_submit_name = (TextView) findViewById(R.id.group_submit_name);
        group_submit_address = (TextView) findViewById(R.id.group_submit_address);
        group_submit_return = (ImageView) findViewById(R.id.group_submit_return);
        group_submit_return.setOnClickListener(this);
        group_submit_alipay = (LinearLayout) findViewById(R.id.group_submit_alipay);
        group_submit_alipay.setOnClickListener(this);
        group_submit_wx = (LinearLayout) findViewById(R.id.group_submit_wx);
        group_submit_wx.setOnClickListener(this);
    }

    private void address_xUtil() {
        RequestParams params = new RequestParams(Variables.http_all_address);
        params.addBodyParameter("customerID", Variables.my.getCustomerID());
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
                    DefaultAddress(result);
                }
            }
        });
    }

    private void DefaultAddress(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.getString("Isdefault").equals("1")) {
                        AddressBean address = new AddressBean();
                        address.setId(object.getString("id"));
                        address.setCustomerID(object.getString("customerID"));
                        address.setName(object.getString("name"));
                        address.setPointname(object.getString("pointname"));
                        address.setCity(object.getString("city"));
                        address.setPhone(object.getString("phone"));
                        address.setDistrict(object.getString("district"));
                        address.setAddress(object.getString("address"));
                        address.setSex(object.getString("sex"));
                        address.setIsdefault(object.getString("Isdefault"));
                        Delivery = address;
                        setAddress(Delivery);
                        return;
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAddress(AddressBean address) {
        if (address != null) {
            group_submit_address.setText(address.getDistrict() + " " + address.getAddress());
            group_submit_phone.setText(address.getPhone());
            group_submit_name.setText(address.getName());
            group_submit_Default.setVisibility(View.INVISIBLE);
            submit_goods = true;
        } else {
            group_submit_Default.setVisibility(View.VISIBLE);
        }
    }

    public static final int ADDRESS = 5;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择地址
        if (requestCode == GroupSubmitOrder.ADDRESS) {
            if (resultCode == Activity.RESULT_OK && resultCode == RESULT_OK) {
                Delivery = (AddressBean) data.getSerializableExtra("address");
                setAddress(Delivery);
            }
        }

        //
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("我是支付返回的信息", data.toString());
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (flag) {
            Intent intent;
            switch (v.getId()) {
                case R.id.group_submit_return:
                    finish();
                    break;
                case R.id.group_submit_choose:
                    intent = new Intent(GroupSubmitOrder.this, ManageAddress.class);
                    intent.putExtra("from", "order");
                    if (Delivery != null) {
                        intent.putExtra("addressId", Delivery.getId());
                    }
                    startActivityForResult(intent, ADDRESS);
                    break;
                case R.id.group_submit_alipay:
                    if (submit_goods) {
                        if (Delivery != null) {
                            //IsSingleBuy 2:用户参团 1：单独购  0:开团
                            if (IsSingleBuy == 2) {
                                joinTuan(groupGoods.getTuanPrice(), "alipay");
                            } else if (IsSingleBuy == 1) {
                                sure(groupGoods.getSinglePrice(), "alipay");
                            } else {
                                sure(groupGoods.getTuanPrice(), "alipay");
                            }
                            flag = false;
                        } else {
                            Toast.makeText(GroupSubmitOrder.this, "请选择地址", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.group_submit_wx:
                    if (submit_goods) {
                        if (Delivery != null) {
                            if (IsSingleBuy == 2) {
                                joinTuan(groupGoods.getSinglePrice(), "alipay");
                            } else if (IsSingleBuy == 1) {
                                sure(groupGoods.getSinglePrice(), "alipay");
                            } else {
                                sure(groupGoods.getTuanPrice(), "alipay");
                            }

                            flag = false;
                        } else {
                            Toast.makeText(GroupSubmitOrder.this, "请选择地址", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }

    String orderno;

    /**
     * 获取订单ID
     *
     * @return
     */
    private String getOrderNO() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        String sub = date.replaceAll("-|:", "") + ((int) (Math.random() * (1000000 - 100000)) + 100000);
        Log.d("我是订单的号码", sub + "");
        return sub;
    }

    private String keep(double price) {
        return String.format("%.2f", price);
    }

    //删除团购订单
    public void DeleteTuanOrder() {

        RequestParams params = new RequestParams(Variables.DeleteTuanOrder);
        params.addBodyParameter("OrderNo", orderno);
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

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
                } else {
//                    success();
                }
            }

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    //用户参团
    private void joinTuan(final double price, final String pay) {
        String addressId = Delivery == null ? "0" : Delivery.getId();
        orderno = getOrderNO();

        JSONObject js_request = new JSONObject();
        try {
            js_request.put("TuanID", groupGoods.getOrderID());
            js_request.put("CustomerID", Variables.my.getCustomerID());
            js_request.put("CustomerOrderNo", orderno);
            Log.d("用户参团orderno", orderno);
            js_request.put("address", addressId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(Variables.JoinTuan);
        params.setAsJsonContent(true);
        params.setBodyContent(js_request.toString());

        Log.d("我是接口", params.toString());

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
                        JSONObject jsonObject = new JSONObject(result);
                        String Ret = jsonObject.getString("Ret");
                        if (Ret.equals("1")) {
                            charge_xUtil(orderno, pay, price);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sure(final double price, final String pay) {

        String addressId = Delivery == null ? "0" : Delivery.getId();
        orderno = getOrderNO();

        JSONObject js_request = new JSONObject();
        try {
            js_request.put("OrderNo", orderno);
            js_request.put("OrderPrice", keep(groupGoods.getTuanPrice()));
            js_request.put("OrderType", 0);
            js_request.put("point", Variables.point.getID());
            js_request.put("ProductID", groupGoods.getTuanid());
            js_request.put("CustomerStr", Variables.my.getCustomerID());
            js_request.put("CustomerNum", 1);
            js_request.put("TuanNumber", groupGoods.getPersonNum());
            js_request.put("Cost", groupGoods.getCost());
            js_request.put("command", "");
            js_request.put("IsSingleBuy", IsSingleBuy);
            js_request.put("address", addressId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(Variables.http_InsertTuanOrder);
        params.setAsJsonContent(true);
        params.setBodyContent(js_request.toString());

        Log.d("我是接口", params.toString());

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
                        JSONObject jsonObject = new JSONObject(result);
                        String Ret = jsonObject.getString("Ret");
                        if (Ret.equals("1")) {
                            charge_xUtil(orderno, pay, price);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void charge_xUtil(String orderno, String pay, double PayPrice) {

        double payprice = PayPrice * 100;

        RequestParams params = new RequestParams(Variables.hava_change);
        params.addBodyParameter("orderno", orderno);
        params.addBodyParameter("paytype", pay);
        params.addBodyParameter("amount", keep(payprice));
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
                    Charge_JSON(result);
                }
            }
        });

    }

    private void Charge_JSON(String result) {
        String str = result;
        String Ret = null;
        try {
            JSONObject object = new JSONObject(result);
            Ret = object.getString("Ret");

            if (Ret.equals("1")) {
                String bb = str.substring(str.indexOf("{\"id\":"), str.indexOf("}]}"));
                charge = bb + "}";
                //调用付款的控件；
                if (null == charge) {
                    showMsg("请求出错", "请检查URL", "URL无法获取charge");
                }

                Pingpp.createPayment(this, charge);

            } else {
                Log.d("请求错误", "你看着办");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = "支付成功";
        if (null != msg1 && msg1.length() != 0) {
            str = "取消支付";
            DeleteTuanOrder();
            Toast.makeText(GroupSubmitOrder.this, str, Toast.LENGTH_SHORT).show();
        } else if (null != msg2 && msg2.length() != 0) {
            str = "支付失败";
            DeleteTuanOrder();
            Toast.makeText(GroupSubmitOrder.this, str, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(GroupSubmitOrder.this, str, Toast.LENGTH_SHORT).show();
            if (IsSingleBuy != 1) {
                Intent intent = new Intent(GroupSubmitOrder.this, GroupDetail.class);
                if (IsSingleBuy == 0) {
                    //开团的订单号是本地生成，所以要手动加入到groupGoods中，不然传过去的是null
                    groupGoods.setOrderNO(orderno);
                }
                intent.putExtra("groupOrder", groupGoods);
                intent.putExtra("IsSingleBuy", IsSingleBuy);
                intent.putExtra("from", "GroupSubmitOrder");
                GroupGoodParticular.groupGoodParticular.finish();
                startActivity(intent);
            }
        }
        finish();
    }
}

