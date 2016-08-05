package com.example.lfy.myapplication.FragmentMine.balance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.BalanceBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/4/13.
 */
public class Balance_history extends AppCompatActivity {

    Balance_history_adapter balance_history_adapter;
    RecyclerView history_balance;
    ImageView new_return;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_balance_history);
        history_balance = (RecyclerView) findViewById(R.id.history_balance);
        new_return = (ImageView) findViewById(R.id.new_return);
        new_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setAdapter();
        TimeStamp();
    }

    //分类适配器
    private void setAdapter() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        history_balance.setLayoutManager(linearLayoutManager);
        balance_history_adapter = new Balance_history_adapter();

        balance_history_adapter.SetOnItemClick(new Balance_history_adapter.OnItemClick() {
            @Override
            public void onItemClick(int position, BalanceBean balance_bean) {

            }
        });

    }

    //获取时间戳
    private void TimeStamp() {
        RequestParams params = new RequestParams(Variables.http_time);
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
                        String longTime = jsonObject.getString("Msg");
                        text_xUtil(longTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void text_xUtil(String timestame) {

        RequestParams params = new RequestParams(Variables.http_SelectTopUpRecord);
        params.addBodyParameter("customerId", Variables.my.getCustomerID());
        params.addBodyParameter("timestamp", timestame);
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
                    List<BalanceBean> balances = new ArrayList<BalanceBean>();
                    try {
                        JSONObject object = new JSONObject(str);
                        JSONArray data = object.getJSONArray("Data");
                        for (int i = 0; i < data.length(); i++) {
                            BalanceBean balance = new BalanceBean();
                            JSONObject everyone = data.getJSONObject(i);
                            balance.setCustomerId(everyone.getString("customerId"));
                            balance.setGetPrice(everyone.getString("getPrice"));
                            balance.setId(everyone.getString("id"));
                            balance.setPayPrice(everyone.getString("payPrice"));
                            balance.setPayed(everyone.getString("payed"));
                            balance.setTimestamp(everyone.getString("timestamp"));
                            balance.setTopUpInfoId(everyone.getString("topUpInfoId"));
                            balance.setTopUpOrderNo(everyone.getString("topUpOrderNo"));
                            balance.setTopUpTime(everyone.getString("topUpTime"));
                            balances.add(balance);
                        }
                        //----------------------------------------
                        balance_history_adapter.addData(balances);
                        history_balance.setAdapter(balance_history_adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }
}
