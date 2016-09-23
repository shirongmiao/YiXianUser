package com.example.lfy.myapplication.SubmitOrder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.FragmentMine.address.ManageAddress;
import com.example.lfy.myapplication.FragmentMine.balance.Balance;
import com.example.lfy.myapplication.FragmentMine.balance.SetPassWord;
import com.example.lfy.myapplication.FragmentMine.discount.Coupon;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.Send;
import com.example.lfy.myapplication.Util.pay_dialog.DialogWidget;
import com.example.lfy.myapplication.Util.pay_dialog.PayPasswordView;
import com.example.lfy.myapplication.Variables;
import com.pingplusplus.android.Pingpp;
import com.pingplusplus.android.PingppLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by lfy on 2016/6/14.
 */
public class SubmitOrder extends SwipeBackActivity implements View.OnClickListener {

    ImageView submit_return;
    //地址
    LinearLayout submit_choose;
    TextView submit_name;
    TextView submit_phone;
    TextView submit_address;
    TextView submit_Default;
    //优惠券
    FrameLayout submit_coupon;
    TextView submit_coupon_price;
    //价格
    TextView submit_price;
    TextView submit_PromotionPrice;
    LinearLayout submit_my_money;
    LinearLayout submit_alipay;
    LinearLayout submit_wx;
    TextView submit_balance;
    //手动选择配送时间
    LinearLayout submit_time;
    TextView submit_time_text;
    EditText submit_say;

    String time1 = null;
    String time_all = null;

    AddressBean Delivery = null;
    //防止多次点击
    boolean flag = true;
    //弹出密码输入框
    DialogWidget mDialogWidget;

    double OrderPrice;//商品价格+配送费//总价格
    //    double PayPrice;//商品价格+配送费-优惠券
    double GoodPrice;//商品价格
    double VipPrice;//商品会员价格+配送费
    double AllCost;//商品成本

    double CouponPrice = 0;
    String CouponID = "";
    String productStr;
    StringBuilder productIdStr;

    //获取change
    String charge;
    //判断来源
    String from;

    boolean submit_goods = false;
    ScrollView submit_order_scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_order);
        Variables.setTranslucentStatus(this);
        initView();
        getCar_xUtils();
        address_xUtil();//请求地址
        select_money();
        PingppLog.DEBUG = true;
    }

    private void initView() {
        submit_return = (ImageView) findViewById(R.id.submit_return);

        submit_choose = (LinearLayout) findViewById(R.id.submit_choose);
        submit_name = (TextView) findViewById(R.id.submit_name);
        submit_phone = (TextView) findViewById(R.id.submit_phone);
        submit_address = (TextView) findViewById(R.id.submit_address);
        submit_Default = (TextView) findViewById(R.id.submit_Default);

        submit_coupon = (FrameLayout) findViewById(R.id.submit_coupon);
        submit_coupon_price = (TextView) findViewById(R.id.submit_coupon_price);

        submit_price = (TextView) findViewById(R.id.submit_price);
        submit_PromotionPrice = (TextView) findViewById(R.id.submit_PromotionPrice);
        submit_balance = (TextView) findViewById(R.id.submit_balance);

        submit_my_money = (LinearLayout) findViewById(R.id.submit_my_money);
        submit_alipay = (LinearLayout) findViewById(R.id.submit_alipay);
        submit_wx = (LinearLayout) findViewById(R.id.submit_wx);

        submit_time = (LinearLayout) findViewById(R.id.submit_time);
        submit_time_text = (TextView) findViewById(R.id.submit_time_text);
        submit_say = (EditText) findViewById(R.id.submit_say);
        //返回
        submit_return.setOnClickListener(this);
        //选择地址
        submit_choose.setOnClickListener(this);
        //选择优惠券
        submit_coupon.setOnClickListener(this);
        //付款方式
        submit_my_money.setOnClickListener(this);
        submit_alipay.setOnClickListener(this);
        submit_wx.setOnClickListener(this);

        submit_time.setOnClickListener(this);
        String str = Variables.point.getTime();
        int a = Integer.parseInt(str.substring(0, str.indexOf(":"))) + 2;
        int b = Integer.parseInt(str.substring(str.indexOf("-") + 1, str.lastIndexOf(":")));
        time_all = a + ":00";
        Log.d("我是截取的时间", a + "时-" + b + "时");
        submit_time_text.setText("期望明天" + time_all + "送达");
        //透明状态栏下，让输入法不遮挡edittext控件
        submit_order_scrollview = (ScrollView) findViewById(R.id.submit_order_scrollview);
        View decorView = getWindow().getDecorView();
        Variables.ScrollViewUtils(submit_order_scrollview, decorView);
    }

    @Override
    public void onClick(View v) {
        if (flag) {
            Intent intent;
            switch (v.getId()) {
                case R.id.submit_return:
                    finish();
                    break;
                case R.id.submit_choose:
                    intent = new Intent(SubmitOrder.this, ManageAddress.class);
                    intent.putExtra("from", "order");
                    if (Delivery != null) {
                        intent.putExtra("addressId", Delivery.getId());
                    }
                    startActivityForResult(intent, ADDRESS);
                    break;
                case R.id.submit_coupon:
                    intent = new Intent(SubmitOrder.this, Coupon.class);
                    intent.putExtra("from", GoodPrice);
                    startActivityForResult(intent, COUPON);
                    break;
                case R.id.submit_my_money:
                    if (submit_goods) {
                        if (Delivery != null) {
                            money_xUtils(VipPrice - CouponPrice);
                            flag = false;
                        } else {
                            Toast.makeText(SubmitOrder.this, "请选择地址", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.submit_alipay:
                    if (submit_goods) {
                        if (Delivery != null) {
                            sure(OrderPrice - CouponPrice, "alipay");
                            flag = false;
                        } else {
                            Toast.makeText(SubmitOrder.this, "请选择地址", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.submit_wx:
                    if (Delivery != null) {
                        sure(OrderPrice - CouponPrice, "wx");
                        flag = false;
                    } else {
                        Toast.makeText(SubmitOrder.this, "请选择地址", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.submit_time:

                    View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
                    PickerView minute_pv = (PickerView) outerView.findViewById(R.id.minute_pv);
                    List<String> data = new ArrayList<String>();

                    String str = Variables.point.getTime();
                    int a = Integer.parseInt(str.substring(0, str.indexOf(":"))) + 2;
                    int b = Integer.parseInt(str.substring(str.indexOf("-") + 1, str.lastIndexOf(":")));

                    time1 = a + "";

                    for (int i = a; i < b; i++) {
                        data.add(i < 10 ? "0" + i : "" + i);
                    }

                    minute_pv.setData(data);
                    minute_pv.setOnSelectListener(new PickerView.onSelectListener() {

                        @Override
                        public void onSelect(String text) {
                            time1 = text;
                        }
                    });


                    new AlertDialog.Builder(this)
                            .setTitle("选择配送时间")
                            .setView(outerView)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    time_all = time1 + ":00";
                                    submit_time_text.setText("期望明天" + time_all + "送达");
                                }
                            })
                            .show();
                    break;
            }
        }
    }

    //获取购物车数据
    private void getCar_xUtils() {
        RequestParams params = new RequestParams(Variables.http_getCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("point", Variables.point.getID());
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
                    Get_JSON(result);
                }
            }
        });

    }

    private void Get_JSON(String json) {

        List<CarDbBean> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                productStr = data.toString();
                productIdStr = new StringBuilder();

                for (int i = 0; i < data.length(); i++) {
                    CarDbBean carDbBean = new CarDbBean();
                    JSONObject everyone = data.getJSONObject(i);
                    String url = everyone.getString("Image");
                    url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
                    carDbBean.setProductID(everyone.getString("ProductID"));
                    carDbBean.setCustomerID(everyone.getString("CustomerID"));
                    carDbBean.setImage(url);
                    carDbBean.setPrice(everyone.getDouble("Price"));
                    carDbBean.setProductCount(everyone.getInt("ProductCount"));
                    carDbBean.setPromotionName(everyone.getString("PromotionName"));
                    carDbBean.setPromotionPrice(everyone.getDouble("PromotionPrice"));
                    carDbBean.setStandard(everyone.getString("Standard"));
                    carDbBean.setTitle(everyone.getString("Title"));
                    carDbBean.setType1(everyone.getString("Type1"));
                    carDbBean.setTypeName1(everyone.getString("TypeName1"));
                    carDbBean.setPoint(everyone.getString("point"));
                    carDbBean.setCost(everyone.getDouble("Cost"));

                    list.add(carDbBean);
                    if (i == 0) {
                        productIdStr.append(everyone.getString("ProductID"));
                    } else {
                        productIdStr.append("," + everyone.getString("ProductID"));
                    }
                }

                submit_goods = true;
                Money(list);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void Money(List<CarDbBean> persons) {
        double price = 0;
        double PromotionPrice = 0;
        double Cost = 0;
        for (int a = 0; a < persons.size(); a++) {
            price = price + persons.get(a).getPrice() * persons.get(a).getProductCount();
            PromotionPrice = PromotionPrice + persons.get(a).getPromotionPrice() * persons.get(a).getProductCount();
            Cost = Cost + persons.get(a).getCost() * persons.get(a).getProductCount();
        }

        AllCost = Cost;
        GoodPrice = price;
        double free = Variables.point.getFreePrice() - price;
        if (free > 0) {
            price = price + Variables.point.getDeliveryPrice();
            PromotionPrice = PromotionPrice + Variables.point.getDeliveryPrice();
        }
        OrderPrice = price;//普通用户总价
        VipPrice = PromotionPrice;//会员总价

        setMoney(price, PromotionPrice);
    }

    private void setMoney(double price, double PromotionPrice) {

        submit_price.setText("￥" + keep(price) + "元");
        submit_PromotionPrice.setText("只需：￥" + keep(PromotionPrice) + "元");
    }

    private void setAddress(AddressBean address) {
        if (address != null) {
            submit_address.setText(address.getDistrict() + " " + address.getAddress());
            submit_phone.setText(address.getPhone());
            submit_name.setText(address.getName());

            submit_Default.setVisibility(View.INVISIBLE);
        } else {
            submit_Default.setVisibility(View.VISIBLE);
        }
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

    String orderno;

    private void sure(final double price, final String pay) {

        String addressId = Delivery == null ? "0" : Delivery.getId();
        orderno = getOrderNO();

        JSONObject js_request = new JSONObject();
        try {
            js_request.put("OrderNo", orderno);
            js_request.put("OrderPrice", keep(OrderPrice));
            js_request.put("Discount", keep(CouponPrice));//优惠金额
            js_request.put("PayedPrice", keep(price));
            js_request.put("Distribution", Variables.point.getID());
            js_request.put("ProductID", productIdStr.toString());
            js_request.put("CustomerID", Variables.my.getCustomerID());
            js_request.put("CustomerSay", submit_say.getText().toString() + " ");
            js_request.put("CouponID", CouponID);//优惠券ID
            js_request.put("Cost", keep(AllCost));
            js_request.put("productStr", productStr);

            if (OrderPrice >= Variables.point.getFreePrice()) {
                js_request.put("Delivery", "0");
            } else {
                js_request.put("Delivery", Variables.point.getDeliveryPrice() + "");
            }

            js_request.put("Address", addressId);

            if (pay.equals("money")) {
                js_request.put("isNextDay", "1");
                js_request.put("VipDiscount", keep(OrderPrice - VipPrice));
            } else {
                js_request.put("isNextDay", "0");
                js_request.put("VipDiscount", "0");
            }
            js_request.put("DeliveryTime", time_all);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(Variables.submit_order);
        params.setAsJsonContent(true);
        params.setBodyContent(js_request.toString());

        Log.d("我是借口", params.toString());

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
                            Variables.count = 0;
                            if (!pay.equals("money")) {
                                charge_xUtil(orderno, pay, price);
                            } else {
                                mDialogWidget = new DialogWidget(SubmitOrder.this, getDecorViewDialog());
                                mDialogWidget.show();

                                mDialogWidget.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            //操作
                                            Intent intent = new Intent(SubmitOrder.this, PayType.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("payType", "付款失败");
                                            bundle.putString("money", OrderPrice - CouponPrice + "");
                                            bundle.putSerializable("address", Delivery);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            finish();
                                        }
                                        return false;
                                    }
                                });
                            }

//                            DeleteCar_xUtils();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String keep(double price) {
        return String.format("%.2f", price);
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


    /**
     * 返回优惠券的ID和price
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    public static final int ADDRESS = 5;
    public static final int COUPON = 3;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SubmitOrder.COUPON && resultCode == RESULT_OK) {
            CouponID = data.getStringExtra("couponId");
            CouponPrice = data.getDoubleExtra("couponPrice", 0);

            if (CouponPrice != 0) {
                submit_coupon_price.setText(CouponPrice + "元");
                setMoney(OrderPrice - CouponPrice, VipPrice - CouponPrice);
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

        //选择地址
        if (requestCode == SubmitOrder.ADDRESS) {
            if (resultCode == Activity.RESULT_OK && resultCode == RESULT_OK) {
                Delivery = (AddressBean) data.getSerializableExtra("address");
                setAddress(Delivery);
            }
        }
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = "支付成功";
        if (null != msg1 && msg1.length() != 0) {
            str = "取消支付";
            Log.d("我是取消支付", msg1);
        }
        if (null != msg2 && msg2.length() != 0) {
            str = "支付失败";
            Log.d("我是支付失败", msg2);
        }
        Variables.count = 0;

        Intent intent = new Intent(SubmitOrder.this, PayType.class);
        Bundle bundle = new Bundle();
        bundle.putString("payType", str);
        bundle.putString("money", keep(OrderPrice - CouponPrice));
        bundle.putSerializable("address", Delivery);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    //查询余额
    private void money_xUtils(final double price) {

        RequestParams params = new RequestParams(Variables.http_Select_yue);
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
                flag = true;
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
                            JSONArray jsonArray = jsonObject.getJSONArray("Data");
                            double money = jsonArray.getJSONObject(0).getDouble("TopUpPrice");
                            if (money >= price) {
                                pwd_xUtils();
                            } else {
                                flag = true;
                                new AlertDialog.Builder(SubmitOrder.this)
                                        .setMessage("余额不足，是否去充值？")
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(SubmitOrder.this, Balance.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("取消", null).create().show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    //检查密码
    private void pwd_xUtils() {

        RequestParams params = new RequestParams(Variables.http_select_password);
        params.addBodyParameter("TopUpPwd", "");
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
                flag = true;
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
                        Log.d("wo=shi=jian=cha=mi=ma", str);
                        String Msg = jsonObject.getString("Msg");
                        if (Msg.equals("已设置支付密码")) {

                            sure(VipPrice - CouponPrice, "money");

                        } else {
                            new AlertDialog.Builder(SubmitOrder.this)
                                    .setMessage("未设置密码，是否去设置？")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(SubmitOrder.this, SetPassWord.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", null).create().show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    protected View getDecorViewDialog() {

        return PayPasswordView.getInstance(keep(VipPrice - CouponPrice), this, new PayPasswordView.OnPayListener() {
            @Override
            public void onSurePay(String password) {
                mDialogWidget.dismiss();
                mDialogWidget = null;
                String only_one = Send.getMD5(password);
                only_one = Send.getMD5(only_one);
                pwd2_xUtils(only_one);//调用接口支付
            }

            @Override
            public void onCancelPay() {
                mDialogWidget.dismiss();
                mDialogWidget = null;

                Toast.makeText(SubmitOrder.this, "交易取消", Toast.LENGTH_SHORT).show();
                MainActivity.jump(1);
                Intent intent = new Intent(SubmitOrder.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).getView();
    }


    //验证密码是否正确
    private void pwd2_xUtils(String pwd) {

        RequestParams params = new RequestParams(Variables.http_select_password);
        params.addBodyParameter("customerId", Variables.my.getCustomerID());
        params.addBodyParameter("topUpPwd", pwd);
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
                flag = true;
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
                        String Msg = jsonObject.getString("Ret");
                        if (Msg.equals("1")) {
                            yue_xUtils();
                        } else {
                            mDialogWidget = new DialogWidget(SubmitOrder.this, getDecorViewDialog());
                            mDialogWidget.show();
                            Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    //余额支付
    private void yue_xUtils() {

        RequestParams params = new RequestParams(Variables.http_TopUpPay);
        params.addBodyParameter("orderNo", orderno);
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
                    Intent intent = new Intent(SubmitOrder.this, PayType.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("payType", "支付成功");
                    bundle.putString("money", VipPrice - CouponPrice + "");
                    bundle.putSerializable("address", Delivery);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    //查询余额
    private void select_money() {

        RequestParams params = new RequestParams(Variables.http_Select_yue);
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
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String Ret = jsonObject.getString("Ret");
                        if (Ret.equals("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Data");
                            String money = jsonArray.getJSONObject(0).getString("TopUpPrice");
                            double price = Double.parseDouble(money);
                            price = (Math.round(price * 10000) / 10000.00);
                            submit_balance.setText("我的余额：￥" + price);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void DeleteCar_xUtils() {
        RequestParams params = new RequestParams(Variables.http_DeleteCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("point", Variables.point.getID());
        params.setCacheMaxAge(10000 * 10);
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
                    // 成功删除购物车数据

                }
            }
        });

    }


}
