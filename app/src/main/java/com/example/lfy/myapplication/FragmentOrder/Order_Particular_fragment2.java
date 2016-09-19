package com.example.lfy.myapplication.FragmentOrder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.OrderBean;
import com.example.lfy.myapplication.Bean.ZhuangTaiBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lfy on 2016/8/15.
 */
public class Order_Particular_fragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    String orderId;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView order_particular2_recycler;
    ZhuangTaiAdapter zhuangTaiAdapter;
    Button order_particular2_pinglun;

    List<CarDbBean> goods;
    OrderBean orders;

    public void setOrders(OrderBean orders) {
        this.orders = orders;
    }

    public void setGoods(List<CarDbBean> goods) {
        this.goods = goods;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_particular2, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.order_particular2_swi);
        order_particular2_recycler = (RecyclerView) view.findViewById(R.id.order_particular2_recycler);
        order_particular2_pinglun = (Button) view.findViewById(R.id.order_particular2_pinglun);
        zhuangTaiAdapter = new ZhuangTaiAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        order_particular2_recycler.setLayoutManager(linearLayoutManager);
        order_particular2_recycler.setAdapter(zhuangTaiAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
//        Order_xUtils();


        order_particular2_pinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order_particular2_pinglun.getText().equals("确认收货")) {
                    OrderConfirm_xUtils("4");
                } else {
                    Intent intent = new Intent(getActivity(), PingLun.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("goods", (Serializable) goods);
                    bundle.putSerializable("orders", orders);
                    if (order_particular2_pinglun.getText().equals("查看评价")) {
                        bundle.putString("type", "5");
                    } else {
                        bundle.putString("type", "");
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
        return view;
    }

    //确认收货
    private void OrderConfirm_xUtils(String orderType) {
        RequestParams params = new RequestParams(Variables.UpdateOrderType2);
        params.addBodyParameter("orderID", orderId);
        params.addBodyParameter("orderType", orderType);
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
                swipeRefreshLayout.setRefreshing(false);
                if (!hasError && result != null) {
                    // 成功获取数据
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        String Ret = jsonObject.getString("Ret");
                        if (Ret.equals("1")) {
                            onRefresh();
                        } else {
                            Toast.makeText(getContext(), "确认收货失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void Order_xUtils() {
        RequestParams params = new RequestParams(Variables.SelectProcess);
        params.addBodyParameter("orderId", orderId);
        params.setCacheMaxAge(3*1000);
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
                swipeRefreshLayout.setRefreshing(false);
                if (!hasError && result != null) {
                    // 成功获取数据
                    JSON(result);
                }
            }
        });
    }

    private void JSON(String result) {
        try {
            Log.d("订单详情", result);
            JSONObject jsonObject = new JSONObject(result);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                List<ZhuangTaiBean> all = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ZhuangTaiBean zhuangTaiBean = new ZhuangTaiBean();
                    zhuangTaiBean.setOrderId(object.getString("orderId"));
                    zhuangTaiBean.setOrderNo(object.getLong("orderNo"));
                    zhuangTaiBean.setProcessId(object.getString("processId"));
                    zhuangTaiBean.setTime(object.getString("time"));
                    zhuangTaiBean.setType(object.getInt("type"));
                    if (object.getInt("type") >= 3) {
                        order_particular2_pinglun.setVisibility(View.VISIBLE);
                        order_particular2_pinglun.setText("确认收货");
                        if (object.getInt("type") == 4) {
                            order_particular2_pinglun.setText("立即评价");
                        } else if (object.getInt("type") == 5) {
                            order_particular2_pinglun.setText("查看评价");
                        }
                    }
                    all.add(zhuangTaiBean);
                }
                zhuangTaiAdapter.addDate(all);
            } else {
                Toast.makeText(getActivity(), "未查到状态", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        Order_xUtils();
    }

    @Override
    public void onResume() {
        super.onResume();
        Order_xUtils();
    }
}
