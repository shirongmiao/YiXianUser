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

        Order_xUtils();


        order_particular2_pinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PingLun.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void Order_xUtils() {
        RequestParams params = new RequestParams(Variables.SelectProcess);
        params.addBodyParameter("orderId", orderId);
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
                    JSON(result);
                }
            }
        });
    }

    private void JSON(String result) {
        try {
            Log.d("我是领取point", result);
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
                    }
                    all.add(zhuangTaiBean);
                }
                zhuangTaiAdapter.addDate(all);
                zhuangTaiAdapter.notifyDataSetChanged();
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
}
