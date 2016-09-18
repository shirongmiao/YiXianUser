package com.example.lfy.myapplication.SubmitOrder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
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
 * Created by lfy on 2016/6/29.
 */
public class PayType extends AppCompatActivity {


    ImageView imageView;
    RecyclerView rv;
    PayTypeAdapter adapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.submit_paytype);
        initView();
        intent = getIntent();
        adapter = new PayTypeAdapter();
        adapter.addDate(intent);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position <= 0 ? 2 : 1;
            }
        });
        rv.setAdapter(adapter);
        getRecommendProduct_xUtils();
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.submit_paytype_rv);
        imageView = (ImageView) findViewById(R.id.left);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayType.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        look_order.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (type.equals("支付成功")) {
//                    MainActivity.jump(0);
//                } else {
//                    MainActivity.jump(1);
//                }
//                Intent intent = new Intent(PayType.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.classify.performClick();
        Intent intent = new Intent(PayType.this, MainActivity.class);
        startActivity(intent);
        finish();
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
                adapter.addMore(list);
            } else {
                adapter.addMore(null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                    Toast.makeText(x.app(), "加入成功", Toast.LENGTH_LONG).show();
//                    setUpdate();
                }
            }
        });
    }
}
