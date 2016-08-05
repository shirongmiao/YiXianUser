package com.example.lfy.myapplication.FragmentMine.balance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.BalanceBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.dialog_widget.ActionSheetDialog;
import com.example.lfy.myapplication.Variables;
import com.pingplusplus.android.Pingpp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/4/11.
 */
public class Balance extends AppCompatActivity {

    ImageView new_break;
    ImageView history;
    TextView my_money;
    TextView password;
    RecyclerView recyclerView;

    Balance_adapter balance_adapter;

    public static boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_balance);

        balance_adapter = new Balance_adapter();

        initView();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Balance.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        select_xUtils();//充值面额
        pwd_xUtils();//检查密码

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        my_money = (TextView) findViewById(R.id.my_money);
        history = (ImageView) findViewById(R.id.history);
        new_break = (ImageView) findViewById(R.id.new_break);
        password = (TextView) findViewById(R.id.password);

        balance_adapter.SetOnItemClick(new Balance_adapter.OnItemClick() {
            @Override
            public void onItemClick(final BalanceBean balance_bean) {
                if (flag) {
                    fang(balance_bean);
                } else {
                    Dialog dialog = new android.support.v7.app.AlertDialog.Builder(Balance.this)
                            .setMessage("请先设置密码！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Balance.this, SetPassWord.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", null).create();
                    dialog.show();
                }
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Balance.this, Balance_history.class);
                startActivity(intent);
            }
        });


        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Balance.this, ForgetBalancePwd.class);
                startActivity(intent);
            }
        });

        new_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        money_xUtils();//查询余额
    }

    private void select_xUtils() {

        RequestParams params = new RequestParams(Variables.http_money);
        params.addBodyParameter("pointid", Variables.point.getID());
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
                    List<BalanceBean> balances = new ArrayList<BalanceBean>();
                    try {
                        JSONObject object = new JSONObject(result);
                        JSONArray data = object.getJSONArray("Data");
                        for (int i = 0; i < data.length(); i++) {
                            BalanceBean balance = new BalanceBean();
                            JSONObject everyone = data.getJSONObject(i);
                            balance.setGetPrice(everyone.getString("getPrice"));
                            balance.setDetail(everyone.getString("detail"));
                            balance.setNum(everyone.getString("num"));
                            balance.setPayPrice(everyone.getString("payPrice"));
                            balance.setPointID(everyone.getString("pointID"));
                            balance.setTopUpInfoId(everyone.getString("topUpInfoId"));
                            balances.add(balance);
                        }
                        //----------------------------------------
                        balance_adapter.addData(balances);
                        recyclerView.setAdapter(balance_adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void fang(final BalanceBean balance_bean) {
        new ActionSheetDialog(Balance.this)
                .builder()
                .setTitle("选择支付方式")
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .addSheetItem("支付宝", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Insert_xUtils("alipay", balance_bean, getOrderNO());
                            }
                        })
                .addSheetItem("微信支付", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Insert_xUtils("wx", balance_bean, getOrderNO());
                            }
                        }).show();
    }

    private void Insert_xUtils(final String pay, final BalanceBean balance_bean, final String orderno) {

        RequestParams params = new RequestParams(Variables.http_InsertTopUpRecord);
        params.addBodyParameter("customerId", Variables.my.getCustomerID());
        params.addBodyParameter("topUpInfoId", balance_bean.getTopUpInfoId());
        params.addBodyParameter("topUpOrderNo", orderno);
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
                        JSONObject jsonObject = new JSONObject(result);
                        String Ret = jsonObject.getString("Ret");
                        if (Ret.equals("1")) {
                            double price = Double.parseDouble(balance_bean.getPayPrice());
                            price = (Math.round(price * 1000000) / 10000.00);
                            charge_xUtil(price + "", orderno, pay);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
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

    private String getOrderNO() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        String sub = date.replaceAll("-|:", "") + ((int) (Math.random() * (1000000 - 100000)) + 100000);
        Log.d("我是订单的号码", sub + "");
        return sub;

    }

    private void charge_xUtil(String payPrice, String orderno, String pay) {
        RequestParams params = new RequestParams(Variables.hava_change);
        params.addBodyParameter("orderno", orderno);
        params.addBodyParameter("paytype", pay);
        params.addBodyParameter("amount", payPrice);
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
                    Log.d("返回的CHANGE======", result);
                    String Ret = null;
                    try {
                        JSONObject object = new JSONObject(result);
                        Ret = object.getString("Ret");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (Ret.equals("1")) {
                        String bb = str.substring(str.indexOf("{\"id\":"), str.indexOf("}]}"));
                        String charge = bb + "}";
                        //调用付款的控件；
                        if (null == charge) {
                            showMsg("请求出错", "请检查URL", "URL无法获取charge");
                        }
                        //TOADD
                        Pingpp.createPayment(Balance.this, charge);

                    } else {
                        Log.d("请求错误", "你看着办");
                    }
                }

            }
        });
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = "充值成功";
        if (null != msg1 && msg1.length() != 0) {
            str = "取消支付";
            Log.d("我是取消支付", msg1);
        }
        if (null != msg2 && msg2.length() != 0) {
            str = "支付失败";
            Log.d("我是支付失败", msg2);
        }
        money_xUtils();
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(str)
                .setTitle("提示")
                .setPositiveButton("确认", null).create().show();
    }

    private void money_xUtils() {
        RequestParams params = new RequestParams(Variables.http_Select_yue);
        params.addBodyParameter("customerId", Variables.my.getCustomerID());
        Log.d("customerId=====", Variables.my.getCustomerID());

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
                        Log.d("我是余额", result);
                        if (Ret.equals("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Data");
                            String money = jsonArray.getJSONObject(0).getString("TopUpPrice");
                            double yue = Double.parseDouble(money);
                            yue = (Math.round(yue * 10000) / 10000.00);
                            my_money.setText("￥" + yue);
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
                            flag = true;
                        } else {
                            new android.support.v7.app.AlertDialog.Builder(Balance.this)
                                    .setMessage(Msg + ",是否去设置？")
                                    .setTitle("提示")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Balance.this, SetPassWord.class);
                                            startActivity(intent);
                                        }
                                    }).create().show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }
}
