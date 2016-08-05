package com.example.lfy.myapplication.FragmentMine.collection;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.FragmentCar.Shop_Car;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.BadgeView;
import com.example.lfy.myapplication.Variables;

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
 * Created by lfy on 2016/1/4.
 */
public class Collection extends AppCompatActivity {
    //    BadgeView badgeView4;
    Collection_Adapter adapter;
    ImageView all_return;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    View home_car_red;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_collection);
        adapter = new Collection_Adapter();
        initView();
        setBadgeView();
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        all_return = (ImageView) findViewById(R.id.left);
        all_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                xUtils();
            }
        });
        ImageView fab = (ImageView) findViewById(R.id.fab4);
        home_car_red = findViewById(R.id.home_car_red);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Collection.this, Shop_Car.class);
                startActivity(intent1);
            }
        });
    }

    public static BadgeView bv;

    private void setBadgeView() {
        bv = new BadgeView(this);
        bv.setTargetView(home_car_red);
        bv.setTextColor(Color.WHITE);
        bv.setGravity(Gravity.TOP | Gravity.RIGHT);
        bv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
    }

    @Override
    protected void onResume() {
        xUtils();
        super.onResume();
        bv.setBadgeCount(Variables.count);
    }

    private void xUtils() {

        RequestParams params = new RequestParams(Variables.http_select_collection);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
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
                    JSON(result);
                }
            }
        });

    }

    private void JSON(String str) {

        swipeRefreshLayout.setRefreshing(false);
        try {
            JSONObject object = new JSONObject(str);
            String Ret = object.getString("Ret");
            if (Ret.equals("0")) {
                Log.d("我没查到商品", "------------");
                Dialog dialog = new AlertDialog.Builder(Collection.this)
                        .setTitle("您没有收藏的商品！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create();
                dialog.show();

            } else {
                JSONArray data = object.getJSONArray("Data");
                List<GridPhoto> evers = new ArrayList<GridPhoto>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    GridPhoto ever = new GridPhoto();
                    String url = everyone.getString("Image1");
                    ever.setImage("http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8"));
                    ever.setCost(everyone.getDouble("Cost"));
                    ever.setProductID(everyone.getString("ProductID"));
                    ever.setTitle(everyone.getString("Title"));//名称
                    ever.setPrice(everyone.getDouble("Price"));
                    ever.setPromotionName(everyone.getString("PromotionName"));
                    ever.setPromotionPrice(everyone.getDouble("PromotionPrice"));//次日提
                    ever.setStandard(everyone.getString("Standard"));//规格
                    evers.add(ever);
                }
                setAdapter(evers);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void setAdapter(final List<GridPhoto> evers) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (adapter.date.size() != 0) {
            adapter.date.clear();
            adapter.addDatas(evers, "Collection");
            adapter.notifyDataSetChanged();
        } else {
            adapter.addDatas(evers, "Collection");
            recyclerView.setAdapter(adapter);
        }
        adapter.setOnItemClickListener(new Collection_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, GridPhoto data) {
                Intent intent = new Intent(Collection.this, Goods_Particular.class);
                intent.putExtra("productId", data.getProductID());
                startActivity(intent);
            }
        });

    }
}
