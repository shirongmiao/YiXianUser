package com.example.lfy.myapplication.FragmentOrder.child_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.OrderBean;
import com.example.lfy.myapplication.FragmentOrder.Order_Particular;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/3/16.
 */
public class Already extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ExpandListViewAdapter order_adapter;
    ExpandableListView order_View;
//    private ProgressDialog progressDialog = null;
    SwipeRefreshLayout swipe_refresh;

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
            Toast.makeText(getActivity(), "您还未登陆", Toast.LENGTH_LONG).show();
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
        for (int i = 0; i < ProductStr.size(); i++) {
            order_View.expandGroup(i);
        }
        order_View.setGroupIndicator(null);
//        order_View.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id) {
//                // TODO Auto-generated method stub
//                return true;
//            }
//        });

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
            public void OnClick(List<CarDbBean> my_goodses, OrderBean my_orders, String ispay, int position) {
//                Intent intent = new Intent(getContext(), Order_Once.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("orders", (Serializable) my_orders);
//                bundle.putSerializable("order", (Serializable) my_goodses);
//                intent.putExtras(bundle);
//                getActivity().startActivity(intent);
            }
        });

    }

    //请求所有订单
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
                swipe_refresh.setRefreshing(false);
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
            JSONObject object = new JSONObject(result);
            String Ret = object.getString("Ret");
            List<OrderBean> date = new ArrayList<>();
            List<List<CarDbBean>> ProductStr = new ArrayList<>();
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject point = data.getJSONObject(i);

                    if (point.getString("OrderType").equals("2")) {
                        OrderBean order = new OrderBean();
                        order.setOrderNO(point.getString("OrderNO"));
                        order.setOrderID(point.getString("OrderID"));
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
                        order.setAddress(point.getString("Address"));
                        order.setDeliveryTime(point.getString("DeliveryTime"));

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
        List<CarDbBean> Product = new ArrayList<>();
        try {
            JSONArray jsonArr = new JSONArray(order_str);
            for (int j = 0; j < jsonArr.length(); j++) {
                CarDbBean carDbBean = new CarDbBean();
                JSONObject everyone = jsonArr.getJSONObject(j);
                String url = everyone.getString("Image1");
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
                try {
                    carDbBean.setCost(everyone.getDouble("Cost"));
                } catch (Exception e) {
                    carDbBean.setCost(1.0);
                }


                Product.add(carDbBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Product;
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
