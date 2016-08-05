package com.example.lfy.myapplication.FragmentOrder.child_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.OrderBean;
import com.example.lfy.myapplication.FragmentMine.balance.Balance;
import com.example.lfy.myapplication.FragmentMine.balance.SetPassWord;
import com.example.lfy.myapplication.FragmentOrder.FragmentOrder;
import com.example.lfy.myapplication.FragmentOrder.Order_Particular;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.Send;
import com.example.lfy.myapplication.Util.dialog_widget.ActionSheetDialog;
import com.example.lfy.myapplication.Util.pay_dialog.DialogWidget;
import com.example.lfy.myapplication.Util.pay_dialog.PayPasswordView;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.Pingpp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/3/16.
 */
public class Non_payment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
//    private ProgressDialog progressDialog = null;

    //获取change
    String charge;
    int posi;

    String order_no;
    String order_price;
    ExpandListViewAdapter order_adapter;
    ExpandableListView order_View;
    SwipeRefreshLayout swipe_refresh;
    private DialogWidget mDialogWidget;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_all, container, false);
        order_View = (ExpandableListView) view.findViewById(R.id.order_View);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        order_adapter = new ExpandListViewAdapter();
        swipe_refresh.setOnRefreshListener(this);

        return view;
    }

    public void setUpdate() {
        if (Variables.my != null) {
            request_xUtil();
        } else {
            Dialog dialog = new android.support.v7.app.AlertDialog.Builder(getActivity())
                    .setMessage("您还未登陆，是否去登陆？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(getActivity(), LoginBg.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null).create();
            dialog.show();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            setUpdate();

        } else {
            //相当于Fragment的onPause
        }
    }

    private void MyAdapter(List<OrderBean> date, List<List<CarDbBean>> ProductStr) {

        if (null != order_adapter.group_list && null != order_adapter.child_list) {
            order_adapter.Clear();
            order_adapter.addData(date, ProductStr);
            order_adapter.notifyDataSetChanged();
        } else {
            order_adapter.addData(date, ProductStr);
            order_View.setAdapter(order_adapter);
        }
        for (int i = 0; i < date.size(); i++) {
            order_View.expandGroup(i);
        }
        order_View.setGroupIndicator(null);
        order_adapter.setOnItemClickListenr(new ExpandListViewAdapter.OnItemClickListenr() {
            @Override
            public void ItemOnClick(List<CarDbBean> my_goodses, OrderBean my_orders) {
                Intent intent = new Intent(getContext(), Order_Particular.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("orders", (Serializable) my_orders);
                bundle.putSerializable("order", (Serializable) my_goodses);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }

            @Override
            public void OnClick(List<CarDbBean> my_goodses, final OrderBean my_orders, String ispay, int position) {
                posi = position;

                if (!my_orders.getIsNextDay().equals("1")) {
                    new ActionSheetDialog(getActivity())
                            .builder()
                            .setTitle("选择支付方式")
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .addSheetItem("支付宝", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            charge_xUtil(my_orders.getOrderNO(), "alipay", my_orders.getPayedPrice());
                                        }
                                    })
                            .addSheetItem("微信支付", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            charge_xUtil(my_orders.getOrderNO(), "wx", my_orders.getPayedPrice());
                                        }
                                    })
                            .addSheetItem("余额支付", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            order_no = my_orders.getOrderNO();
                                            order_price = my_orders.getPayedPrice();
                                            double pr = Double.parseDouble(my_orders.getPayedPrice());
                                            money_xUtils(pr);
                                        }
                                    }).show();
                } else {
                    new ActionSheetDialog(getActivity())
                            .builder()
                            .setTitle("选择支付方式")
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .addSheetItem("余额支付", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            order_no = my_orders.getOrderNO();
                                            double pr = Double.parseDouble(my_orders.getPayedPrice());
                                            money_xUtils(pr);
                                        }
                                    }).show();
                }
            }
        });

    }

    //请求所有订单
    private void request_xUtil() {
//        progressDialog = ProgressDialog.show(getActivity(), "请稍等...", "加载中...", true);
//        progressDialog.setCancelable(true);

        RequestParams params = new RequestParams(Variables.http_select_order);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        params.setCacheMaxAge(10000 * 5);
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
//                progressDialog.dismiss();
                swipe_refresh.setRefreshing(false);
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                swipe_refresh.setRefreshing(false);
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
                }
            }
        });
    }

    private void JSON(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String Ret = object.getString("Ret");
            List<OrderBean> date = new ArrayList<OrderBean>();
            List<List<CarDbBean>> ProductStr = new ArrayList<List<CarDbBean>>();
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject point = data.getJSONObject(i);
                    if (point.getString("OrderType").equals("0")) {
                        OrderBean order = new OrderBean();
                        order.setOrderNO(point.getString("OrderNO"));
                        order.setOrderPrice(point.getString("OrderPrice"));
                        order.setDiscount(point.getString("Discount"));
                        order.setPayedPrice(point.getString("PayedPrice"));
                        order.setOrderType(point.getString("OrderType"));//种类
                        order.setDistribution(point.getString("Distribution"));//自提点
                        order.setProductID(point.getString("ProductID"));
                        order.setPayed(point.getString("Payed"));
                        order.setCreateTime(point.getString("CreateTime"));//订单创建时间
                        order.setPayTime(point.getString("PayTime"));
                        order.setCustomerID(point.getString("CustomerID"));
                        order.setIfMakeUp(point.getString("IfMakeUp"));//组成
                        order.setCustomerSay(point.getString("CustomerSay"));
                        order.setSwiftNumber(point.getString("SwiftNumber"));
                        order.setProductStr(point.getString("ProductStr"));//订单详情
                        order.setCouponID(point.getString("CouponID"));
                        order.setIsNextDay(point.getString("isNextDay"));
                        order.setPoint(point.getString("point"));
                        order.setDelivery(point.getString("Delivery"));

                        date.add(order);//全部订单
                        ProductStr.add(json(order.getProductStr()));
                    }
                }
                MyAdapter(date, ProductStr);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<CarDbBean> json(String order_str) {
        Log.d("我是未付款", "订单页面的json" + order_str);
        List<CarDbBean> Product = new ArrayList<>();
        try {
            JSONArray jsonArr = new JSONArray(order_str);
            for (int j = 0; j < jsonArr.length(); j++) {
                CarDbBean carDbBean = new CarDbBean();
                JSONObject everyone = jsonArr.getJSONObject(j);
                String url = everyone.getString("Image");
                url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
                carDbBean.setProductID(everyone.getString("ProductID"));
//                carDbBean.setCustomerID(everyone.getString("CustomerID"));
                carDbBean.setImage(url);
                carDbBean.setPrice(everyone.getDouble("Price"));
                carDbBean.setProductCount(everyone.getInt("ProductCount"));
                try {
                    carDbBean.setPromotionName(everyone.getString("Promotion"));
                } catch (Exception e) {
                    carDbBean.setPromotionName(everyone.getString("PromotionName"));
                }
                carDbBean.setPromotionPrice(everyone.getDouble("PromotionPrice"));
                carDbBean.setStandard(everyone.getString("Standard"));
                carDbBean.setTitle(everyone.getString("Title"));
                carDbBean.setType1(everyone.getString("Type1"));
//                carDbBean.setTypeName1(everyone.getString("TypeName1"));
                carDbBean.setPoint(everyone.getString("point"));
                carDbBean.setCost(everyone.getDouble("Cost"));

                Product.add(carDbBean);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Product;
    }

    private void charge_xUtil(String orderno, String pay, String PayPrice) {

        double payprice = Double.parseDouble(PayPrice) * 100;
        payprice = (Math.round(payprice * 10000) / 10000.00);
        RequestParams params = new RequestParams(Variables.hava_change);
        params.addBodyParameter("orderno", orderno);
        params.addBodyParameter("paytype", pay);
        params.addBodyParameter("amount", payprice + "");
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
                //调用支付控件
                try {
                    JSONObject var2 = new JSONObject(charge);
                    String var3 = var2.optString("channel");
                    if ("wx".equals(var3)) {
                        String var4 = getActivity().getPackageName();
                        Intent var5 = new Intent();
                        ComponentName var6 = new ComponentName(var4, var4 + ".wxapi.WXPayEntryActivity");
                        var5.setComponent(var6);
                        var5.putExtra("com.pingplusplus.android.PaymentActivity.CHARGE", charge);
                        startActivityForResult(var5, Pingpp.REQUEST_CODE_PAYMENT);
                        return;
                    }
                } catch (JSONException var7) {
                    var7.printStackTrace();
                }
                Intent var8 = new Intent(getActivity(), PaymentActivity.class);
                var8.putExtra("com.pingplusplus.android.PaymentActivity.CHARGE", charge);
                startActivityForResult(var8, Pingpp.REQUEST_CODE_PAYMENT);

            } else {
                Log.d("请求错误", "你看着办");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //支付页面返回处理All.REQUEST_CODE_PAYMENT
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
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
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setMessage(str)
                .setTitle("提示")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        order_adapter.notifyDataSetChanged();
                    }
                }).create().show();
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
                    Money_JSON(result, price);
                }
            }
        });

    }

    private void Money_JSON(String result, final double price) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String Ret = jsonObject.getString("Ret");
            Log.d("我是余额", result);
            if (Ret.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                String money = jsonArray.getJSONObject(0).getString("TopUpPrice");
                double my_money = Double.parseDouble(money);
                if (my_money >= price) {
                    pwd_xUtils();
                } else {
                    new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setMessage("余额不足，是否去充值？")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getActivity(), Balance.class);
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
                    pwd_JSON(result);
                }
            }
        });

    }

    private void pwd_JSON(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            Log.d("wo=shi=jian=cha=mi=ma", str);
            String Msg = jsonObject.getString("Msg");
            if (Msg.equals("已设置支付密码")) {
                mDialogWidget = new DialogWidget(getActivity(), getDecorViewDialog());
                mDialogWidget.show();
            } else {
                new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .setMessage("未设置密码，是否去设置？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getContext(), SetPassWord.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null).create().show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected View getDecorViewDialog() {
        return PayPasswordView.getInstance(order_price, getActivity(), new PayPasswordView.OnPayListener() {
            @Override
            public void onSurePay(String password) {
                mDialogWidget.dismiss();
                mDialogWidget = null;
                String only_one = Send.getMD5(password);
                only_one = Send.getMD5(only_one);
                pwd2_xUtils(only_one);
            }

            @Override
            public void onCancelPay() {
                // TODO Auto-generated method stub
                mDialogWidget.dismiss();
                mDialogWidget = null;
                Toast.makeText(getActivity(), "交易已取消", Toast.LENGTH_SHORT).show();

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
                    pwd2_JSON(result);
                }
            }
        });
    }

    private void pwd2_JSON(String result) {
        String str = result;
        try {
            JSONObject jsonObject = new JSONObject(str);
            String Msg = jsonObject.getString("Ret");
            if (Msg.equals("1")) {
                yue_xUtils();
            } else {
                mDialogWidget = new DialogWidget(getActivity(), getDecorViewDialog());
                mDialogWidget.show();
                Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //余额支付
    private void yue_xUtils() {
        RequestParams params = new RequestParams(Variables.http_TopUpPay);
        params.addBodyParameter("orderNo", order_no);
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
                    yue_JSON(result);
                }
            }
        });
    }

    private void yue_JSON(String result) {
        String Msg = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            Msg = jsonObject.getString("Msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //支付完成 刷新
        setUpdate();
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setMessage(Msg)
                .setTitle("提示")
                .setPositiveButton("确认", null).create().show();
    }

    @Override
    public void onRefresh() {
        if (Variables.my != null) {
            request_xUtil();
        } else {
            swipe_refresh.setRefreshing(false);
        }
    }
}
