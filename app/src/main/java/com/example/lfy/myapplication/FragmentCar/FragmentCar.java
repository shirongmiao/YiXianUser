package com.example.lfy.myapplication.FragmentCar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.FragmentClassify.SecondViewpager;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.SubmitOrder.SubmitOrder;
import com.example.lfy.myapplication.Util.EmptyRecyclerView;
import com.example.lfy.myapplication.Util.ToastUtils;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lfy on 2016/5/31.
 */
public class FragmentCar extends Fragment implements View.OnClickListener {

    GridLayoutManager gridLayoutManager;
    CarAdapter carAdapter;
    EmptyRecyclerView car_recyclerView;
    View car_none;

    ImageView car_delete;
    TextView car_submit;
    TextView car_money;
    TextView vip_money;
    TextView car_sendPrice;

    LinearLayout car_bottom_line;//低栏


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentcar, container, false);

        FindView(view);
        initView();
        getCar_xUtils();
        getRecommendProduct_xUtils();
        return view;
    }

    private void FindView(View view) {
        car_recyclerView = (EmptyRecyclerView) view.findViewById(R.id.car_recyclerView);
        car_delete = (ImageView) view.findViewById(R.id.car_delete);
        car_submit = (TextView) view.findViewById(R.id.car_submit);
        car_money = (TextView) view.findViewById(R.id.car_money);
        vip_money = (TextView) view.findViewById(R.id.vip_money);
        car_sendPrice = (TextView) view.findViewById(R.id.car_sendPrice);

        car_none = view.findViewById(R.id.car_none);
        car_bottom_line = (LinearLayout) view.findViewById(R.id.car_bottom_line);
    }

    private void initView() {
        carAdapter = new CarAdapter();
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        car_recyclerView.setLayoutManager(gridLayoutManager);
        car_recyclerView.setItemAnimator(new DefaultItemAnimator());
        car_recyclerView.setAdapter(carAdapter);
        car_recyclerView.setEmptyView(car_none);

        carAdapter.SetOnClickListen(new CarAdapter.OnClickListen() {
            @Override
            public void setOnClick(int position, CarDbBean carDbBean, String type) {
                if (type.equals("1")) {
                    carAdapter.titles.get(position).setProductCount(carAdapter.titles.get(position).getProductCount() + 1);
                    carAdapter.notifyItemChanged(position);
                } else {
                    carAdapter.titles.get(position).setProductCount(carAdapter.titles.get(position).getProductCount() - 1);
                    carAdapter.notifyItemChanged(position);

                }
                Insert_xUtils(position, carDbBean.getProductID(), type);

            }
        });

        carAdapter.setOnItemClickListen(new CarAdapter.OnItemClickListen() {
            @Override
            public void SetOnItemClick() {
                MainActivity.classify.performClick();
            }

            @Override
            public void SetOnItemClick(String productId) {
                Intent intent = new Intent(getActivity(), Goods_Particular.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }

            @Override
            public void SerOnClick(String productId) {
                addRecommendProduct_xUtils(productId);
            }
        });
        car_sendPrice.setText("配送费:￥" + Variables.point.getDeliveryPrice() + "元");
        car_submit.setOnClickListener(this);
        car_delete.setOnClickListener(this);
        car_none.setOnClickListener(this);
    }

    public void setUpdate() {
        getCar_xUtils();
        getRecommendProduct_xUtils();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.car_submit:
                if (Variables.point.getState().equals("1")) {
                    car_submit.setEnabled(true);
                    car_submit.setBackgroundResource(R.color.actionsheet_red);
                    if (price > Variables.point.deliveryPrice) {
                        intent = new Intent(getActivity(), SubmitOrder.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "未达到配送金额", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    car_submit.setEnabled(false);
                    car_submit.setBackgroundResource(R.color.line_grey);
                }

                break;
            case R.id.car_delete:
                Dialog dialog = new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .setMessage("确认清空购物车？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DeleteCar_xUtils();
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.show();
                break;
            case R.id.car_none:
                MainActivity.classify.performClick();
                break;
        }
    }

    //获取购物车数据
    private void getCar_xUtils() {
        RequestParams params = new RequestParams(Variables.http_getCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("point", Variables.point.getID());
        params.setCacheMaxAge(1000 * 60);
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

        final List<CarDbBean> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                Variables.count = 0;
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    CarDbBean carDbBean = new CarDbBean();
                    JSONObject everyone = data.getJSONObject(i);
                    String url = everyone.getString("Image1");
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
                    Variables.count = Variables.count + everyone.getInt("ProductCount");
                    list.add(carDbBean);
                }

                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return position <= list.size() + 1 ? 2 : 1;
                    }
                });
                carAdapter.addDate(list);
                carAdapter.notifyDataSetChanged();
                Money(list);
                MainActivity.bv.setBadgeCount(Variables.count);
                car_bottom_line.setVisibility(View.VISIBLE);
            } else {
                carAdapter.addDate(null);
                carAdapter.notifyDataSetChanged();
                car_submit.setEnabled(false);
                car_bottom_line.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                    carAdapter.addDate(null);
                    carAdapter.notifyDataSetChanged();
                    carAdapter.addMore(null);
                    car_submit.setEnabled(false);
                    car_submit.setBackgroundResource(R.color.line_grey);
                    car_money.setText("合计:￥0元");
                    car_bottom_line.setVisibility(View.INVISIBLE);
                    Variables.count = 0;
                    MainActivity.bv.setBadgeCount(Variables.count);
                }
            }
        });

    }

    double price = 0;

    private void Money(List<CarDbBean> persons) {
        price = 0;
        double PromotionPrice = 0;
        for (int a = 0; a < persons.size(); a++) {
            price = price + persons.get(a).getPrice() * persons.get(a).getProductCount();
            PromotionPrice = PromotionPrice + persons.get(a).getPromotionPrice() * persons.get(a).getProductCount();
        }
        double free = Variables.point.getFreePrice() - price;
        carAdapter.FreedPrice(free);
        car_sendPrice.setText("配送费:￥" + Variables.point.getDeliveryPrice() + "元");
        if (free > 0) {
//            price = price + Variables.point.sendPrice;
//            PromotionPrice = PromotionPrice + Variables.point.sendPrice;
            car_sendPrice.setText("配送费:￥" + Variables.point.getDeliveryPrice() + "元");
        } else {
            car_sendPrice.setText("配送费：￥0.0元");
        }
        //配送费
        double c = Variables.point.getSendPrice() - price;
        if (c < 0) {
            car_submit.setEnabled(true);
            car_submit.setBackgroundResource(R.color.actionsheet_red);
            car_submit.setText("结算");
        } else {
            String cha = String.format("%.2f", c);
            car_submit.setEnabled(false);
            car_submit.setText("还差" + cha + "元");
            car_submit.setBackgroundResource(R.color.line_grey);
        }
        String result = String.format("%.2f", price);
        String vip = String.format("%.2f", PromotionPrice);
        car_money.setText("合计:￥" + result);
        vip_money.setText("会员:￥" + vip);

        if (Variables.point.getState().equals("0")) {
            car_submit.setEnabled(false);
            car_submit.setBackgroundResource(R.color.line_grey);
        }
    }

    private void Insert_xUtils(final int position, String productId, final String type) {

        RequestParams params = new RequestParams(Variables.http_InsertCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("ProductID", productId);
        params.addBodyParameter("point", Variables.point.getID());
        params.addBodyParameter("type", type);
        params.addBodyParameter("num", "1");

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
                Toast.makeText(x.app(), "服务器奔溃啦！", Toast.LENGTH_LONG).show();

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
                            if (jsonObject.getString("Msg").equals("删除成功")) {
                                carAdapter.notifyItemRemoved(position);
                                carAdapter.titles.remove(position);
                                carAdapter.notifyDataSetChanged();
                                Money(carAdapter.titles);
                                if (carAdapter.titles.size() == 0) {
                                    car_bottom_line.setVisibility(View.INVISIBLE);
                                    car_none.setVisibility(View.VISIBLE);
                                    car_recyclerView.setVisibility(View.GONE);
                                    Variables.count = 0;
                                } else {
                                    Variables.count = Variables.count - 1;
                                }
                            } else {
                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                carAdapter.titles.get(position).setProductCount(jsonArray.getJSONObject(0).getInt("ProductCount"));
                                carAdapter.notifyDataSetChanged();
                                Money(carAdapter.titles);
                                if (type.equals("1")) {
                                    Variables.count = Variables.count + 1;
                                } else {
                                    Variables.count = Variables.count - 1;
                                }
                            }
                        } else {
                            if (type.equals("1")) {
                                carAdapter.titles.get(position).setProductCount(carAdapter.titles.get(position).getProductCount() - 1);
                                carAdapter.notifyDataSetChanged();
                                Variables.count = Variables.count - 1;
                            } else {
                                carAdapter.titles.get(position).setProductCount(carAdapter.titles.get(position).getProductCount() + 1);
                                carAdapter.notifyDataSetChanged();
                                Variables.count = Variables.count + 1;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (type.equals("1")) {
                        carAdapter.titles.get(position).setProductCount(carAdapter.titles.get(position).getProductCount() - 1);
                        carAdapter.notifyItemChanged(position);
                    } else {
                        carAdapter.titles.get(position).setProductCount(carAdapter.titles.get(position).getProductCount() + 1);
                        carAdapter.notifyItemChanged(position);

                    }
                }
//                car_submit.setText("结算（" + Variables.count + "）");
                MainActivity.bv.setBadgeCount(Variables.count);
            }

        });

    }


    //获取推荐商品
    private void getRecommendProduct_xUtils() {
        RequestParams params = new RequestParams(Variables.RecommendProduct);
        params.addBodyParameter("customerId", Variables.my.getCustomerID());
        params.setCacheMaxAge(1000 * 60);
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
                    RecommendProduct_JSON(result);
                }
            }
        });

    }

    //购物车加入推荐商品
    private void addRecommendProduct_xUtils(String productId) {
        RequestParams params = new RequestParams(Variables.http_InsertCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("ProductID", productId);
        params.addBodyParameter("point", Variables.point.getID());
        params.addBodyParameter("type", "1");
        params.addBodyParameter("num", "1");
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
                Toast.makeText(x.app(), "服务器奔溃啦！", Toast.LENGTH_LONG).show();
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
                    ToastUtils.showToast("加入成功", true);
                    setUpdate();
                }
            }
        });
    }

    private void RecommendProduct_JSON(String json) {

        List<GridPhoto> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    GridPhoto carDbBean = new GridPhoto();
                    JSONObject everyone = data.getJSONObject(i);
                    String url = everyone.getString("Image1");
                    url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
                    carDbBean.setProductID(everyone.getString("ProductID"));
                    carDbBean.setImage(url);
                    carDbBean.setPrice(everyone.getDouble("Price"));
                    carDbBean.setPromotionName(everyone.getString("PromotionName"));
                    carDbBean.setPromotionPrice(everyone.getDouble("PromotionPrice"));
                    carDbBean.setStandard(everyone.getString("Standard"));
                    carDbBean.setTitle(everyone.getString("Title"));
                    carDbBean.setType1(everyone.getString("Type1"));
                    carDbBean.setTypeName1(everyone.getString("TypeName1"));
                    carDbBean.setPoint(everyone.getString("point"));
                    carDbBean.setCost(everyone.getDouble("Cost"));
                    list.add(carDbBean);
                }
                carAdapter.addMore(list);
                carAdapter.notifyDataSetChanged();
            } else {
                carAdapter.addMore(null);
                carAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}