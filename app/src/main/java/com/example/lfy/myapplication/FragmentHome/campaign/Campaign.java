package com.example.lfy.myapplication.FragmentHome.campaign;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.example.lfy.myapplication.Bean.FootPhoto;
import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.FragmentCar.Shop_Car;
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
 * Created by lfy on 2016/7/15.
 */
public class Campaign extends AppCompatActivity {
    ImageView return_but;
    RecyclerView gridView;
    ImageView car;
    CampaignAdapter adapter;
    FootPhoto footPhoto;
    TextView title;
    ImageView return_all;
    View home_car_red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.campaign);
        Intent intent = getIntent();
        footPhoto = (FootPhoto) intent.getSerializableExtra("campaign");

        search_xUtil(footPhoto.getTypename(), Variables.point.getID());

        adapter = new CampaignAdapter();

        initView();
        title.setText(footPhoto.getTypename());
        setBadgeView();
    }

    private void initView() {
        home_car_red = findViewById(R.id.home_car_red);
        return_but = (ImageView) findViewById(R.id.return_but);
        title = (TextView) findViewById(R.id.campaign_title);
        gridView = (RecyclerView) findViewById(R.id.recyclerview);
        car = (ImageView) findViewById(R.id.car);
        return_all = (ImageView) findViewById(R.id.return_all);
        return_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Campaign.this, Shop_Car.class);
                startActivity(intent);
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
        super.onResume();
        bv.setBadgeCount(Variables.count);
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
        List<GridPhoto> sear = new ArrayList<GridPhoto>();
        try {
            JSONObject object = new JSONObject(result);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
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
            }

            setAdapter(sear);

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

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (0 == position) {
                    return 2;
                }
                return 1;
            }
        });
        Log.d("我是遍数", "=====================");
        adapter.addDate(evers, footPhoto.getActivityImg());
        gridView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CampaignAdapter.OnClickListen() {
            @Override
            public void setOnClick(GridPhoto goods) {
                if (Variables.my != null) {
                    Insert_xUtils(goods.getProductID());
                } else {
                    Intent intent = new Intent(Campaign.this, LoginBg.class);
                    startActivity(intent);
                }
            }

            @Override
            public void setOnItemClick(GridPhoto gridPhoto) {
                Intent intent = new Intent(Campaign.this, Goods_Particular.class);
                intent.putExtra("productId", gridPhoto.getProductID());
                startActivity(intent);
            }
        });
    }

    private void Insert_xUtils(String productId) {
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
                    Toast.makeText(x.app(), "success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int Ret = jsonObject.getInt("Ret");
                        if (Ret == 0) {
                            Toast.makeText(x.app(), jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
                        } else {
                            Variables.count = Variables.count + 1;
                            bv.setBadgeCount(Variables.count);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
