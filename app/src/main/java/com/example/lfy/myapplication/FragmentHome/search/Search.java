package com.example.lfy.myapplication.FragmentHome.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.FragmentCar.Shop_Car;
import com.example.lfy.myapplication.FragmentMine.collection.Collection_Adapter;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.BadgeView;
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
 * Created by lfy on 2016/1/4.
 */
public class Search extends AppCompatActivity {

    List<GridPhoto> sear;
    RecyclerView gridView;
    ImageView black_all;
    TextView fragment_baokuan;
    Collection_Adapter adapter;
    View home_car_red;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.home_search_gradeview);
        gridView = (RecyclerView) findViewById(R.id.gridlistview);
        black_all = (ImageView) findViewById(R.id.black_all);
        fragment_baokuan = (TextView) findViewById(R.id.fragment_baokuan);
        home_car_red = findViewById(R.id.home_car_red);
        ImageView shopCar = (ImageView) findViewById(R.id.fab_new);//小红点

        final Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        String title = intent.getStringExtra("title");
        String point = intent.getStringExtra("point");
        fragment_baokuan.setText(title);

        if (Variables.point.getID() != null) {
            search_xUtil(content, point);
        } else {
            Toast.makeText(this, "您还未选择自提点", Toast.LENGTH_SHORT).show();
        }
        adapter = new Collection_Adapter();
        black_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shopCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.my != null) {
                    Intent intent1 = new Intent(Search.this, Shop_Car.class);
                    startActivity(intent1);
                } else {
                    Intent intent1 = new Intent(Search.this, LoginBg.class);
                    startActivity(intent1);
                }
            }
        });

        setBadgeView();
    }
    public static BadgeView bv;
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter.getItemCount() != 0)
            adapter.notifyDataSetChanged();

        bv.setBadgeCount(Variables.count);
    }
    private void setBadgeView() {
        bv = new BadgeView(this);
        bv.setTargetView(home_car_red);
        bv.setTextColor(Color.WHITE);
        bv.setGravity(Gravity.TOP | Gravity.RIGHT);
        bv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
    }

    private void search_xUtil(String content, String point) {

        RequestParams params = new RequestParams(Variables.http_search);
        params.addBodyParameter("search", content);
        params.addBodyParameter("point", point);
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
                    Get_JSON(result);
                }
            }
        });

    }

    private void Get_JSON(String result) {

        try {
            JSONObject object = new JSONObject(result);
            String Ret = object.getString("Ret");
            if (Ret.equals("0")) {
                Log.d("我没查到商品", "------------");

                Dialog dialog = new AlertDialog.Builder(Search.this)
                        .setMessage("本店没有您要的商品，我们会继续努力的")
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
                sear = new ArrayList<GridPhoto>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject gradOne = data.getJSONObject(i);

                    GridPhoto ever = new GridPhoto();
                    String imag = gradOne.getString("Image1");
                    ever.setImage("http://www.baifenxian.com/" + java.net.URLEncoder.encode(imag, "UTF-8"));
                    ever.setOrderCount(gradOne.getInt("OrderCount"));
                    ever.setPrice(gradOne.getDouble("Price"));//市场价格
                    ever.setProductID(gradOne.getString("ProductID"));//
                    ever.setPromotionPrice(gradOne.getDouble("PromotionPrice"));
                    ever.setShelfState(gradOne.getString("ShelfState"));
                    ever.setStandard(gradOne.getString("Standard"));
                    ever.setType1(gradOne.getString("Type1"));
                    ever.setTitle(gradOne.getString("Title"));
                    ever.setTypeName1(gradOne.getString("TypeName1"));
                    ever.setTypeName2(gradOne.getString("TypeName2"));
                    ever.setPromotionName(gradOne.getString("PromotionName"));
                    ever.setPoint(gradOne.getString("point"));
                    sear.add(ever);
                }

                setAdapter(sear);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(final List<GridPhoto> evers) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());

        Log.d("我是遍数", "=====================");
        adapter.addDatas(evers,"Search");
        gridView.setAdapter(adapter);

        adapter.setOnItemClickListener(new Collection_Adapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position, GridPhoto data) {
                Intent intent = new Intent(getApplicationContext(), Goods_Particular.class);
                intent.putExtra("productId", data.getProductID());
                startActivity(intent);
            }
        });
    }
}
