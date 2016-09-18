package com.example.lfy.myapplication.FragmentOrder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.Evaluate;
import com.example.lfy.myapplication.Bean.EvaluateItem;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lfy on 2016/8/21.
 */
public class PingLun extends AppCompatActivity implements View.OnClickListener {
    ImageView pinglun_back;
    TextView txt_topbar, sendEvaluate_tv_btn;
    LinearLayout pinglun_bottom_linear;
    RecyclerView rv;
    MyAdapter adapter;
    RatingBar pinglun_distributionStar, pinglun_serviceStar;
    EditText pinglun_evaluateText;
    List<CarDbBean> goods;
    OrderBean order;
    Evaluate evaluate;
    List<EvaluateItem> evaluateItems;
    //true为评价，false为查看评价
    boolean orderType = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.pinglun);
        Intent intent = getIntent();
        if (!intent.getStringExtra("type").equals("5")) {
            //true为评价，5:false为查看评价
            orderType = true;
        }
        goods = (List<CarDbBean>) intent.getSerializableExtra("goods");
        order = (OrderBean) intent.getSerializableExtra("orders");
        evaluateItems = new ArrayList<>();
        initView();
        setListener();
        if (orderType) {
            SetEvaluate();
        } else {
            GetEvaluate();
            txt_topbar.setText("查看评价");
            pinglun_bottom_linear.setVisibility(View.GONE);
        }
    }

    public void initView() {
        pinglun_back = (ImageView) findViewById(R.id.pinglun_back);
        txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        rv = (RecyclerView) findViewById(R.id.pinglun_rv);
        pinglun_distributionStar = (RatingBar) findViewById(R.id.pinglun_distributionStar);
        pinglun_serviceStar = (RatingBar) findViewById(R.id.pinglun_serviceStar);
        pinglun_evaluateText = (EditText) findViewById(R.id.pinglun_evaluateText);
        pinglun_bottom_linear = (LinearLayout) findViewById(R.id.pinglun_bottom_linear);
        sendEvaluate_tv_btn = (TextView) findViewById(R.id.sendEvaluate_tv_btn);
    }

    public void setListener() {
        pinglun_back.setOnClickListener(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(evaluateItems);
        rv.setAdapter(adapter);
        sendEvaluate_tv_btn.setOnClickListener(this);
        pinglun_distributionStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1) {
                    rating = 1;
                    pinglun_distributionStar.setRating(rating);
                }
            }
        });
        pinglun_serviceStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1) {
                    rating = 1;
                    pinglun_serviceStar.setRating(rating);
                }
            }
        });
    }

    public void setData() {
        pinglun_distributionStar.setRating(evaluate.getDistributionStar());
        pinglun_distributionStar.setIsIndicator(true);
        pinglun_serviceStar.setRating(evaluate.getServiceStar());
        pinglun_serviceStar.setIsIndicator(true);
        pinglun_evaluateText.setText("评价：" + evaluate.getEvaluateText());
        pinglun_evaluateText.setBackground(null);
        pinglun_evaluateText.setPadding(0, 0, 0, 0);
        pinglun_evaluateText.setFocusable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pinglun_back:
                finish();
                break;
            case R.id.sendEvaluate_tv_btn:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("发表评价");
                dialog.setMessage("确认发表？");
                dialog.setPositiveButton("发了吧", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SendEvaluate();
                    }
                }).setNegativeButton("算了再改改", null).show();
                break;
        }
    }

    private void SetEvaluate() {
        for (int i = 0; i < goods.size(); i++) {
            EvaluateItem item = new EvaluateItem();
            item.setProductName(goods.get(i).getTitle());
            item.setImage(goods.get(i).getImage());
            item.setProductId(goods.get(i).getProductID());
            item.setStar(5);
            evaluateItems.add(item);
        }
        adapter.notifyDataSetChanged();
    }

    private void GetEvaluate() {
        RequestParams params = new RequestParams(Variables.GetEvaluate);
        params.addBodyParameter("orderId", order.getOrderID());
        Log.d("Pinglun.java-139", params.toString());
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
                    JSONGetEvaluate(result);
                }
            }
        });
    }

    private void JSONGetEvaluate(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                JSONObject object = jsonArray.getJSONObject(0);
                evaluate = new Evaluate();
                evaluate.setEvaluateID(object.getString("evaluateID"));
                evaluate.setOrderID(object.getString("orderID"));
                evaluate.setCustomerID(object.getString("customerID"));
                evaluate.setPointID(object.getString("pointID"));
                evaluate.setEvaluateStr(object.getString("evaluateStr"));
                JSONGetEvaluateItem(evaluate.getEvaluateStr());
                evaluate.setCreatTime(object.getString("creatTime"));
                evaluate.setDistributionStar(object.getInt("distributionStar"));
                evaluate.setServiceStar(object.getInt("serviceStar"));
                evaluate.setEvaluateText(object.getString("evaluateText"));
                setData();
            } else {
                Toast.makeText(PingLun.this, "获取评价数据失败", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void JSONGetEvaluateItem(String evaluateStr) {
        try {
            JSONArray jsonArray = new JSONArray(evaluateStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                EvaluateItem item = new EvaluateItem();
                JSONObject object = jsonArray.getJSONObject(i);
                item.setProductName(object.getString("productName"));
                item.setEvaluateText(object.getString("evaluateText"));
                item.setProductId(object.getString("productId"));
                item.setStar(object.getInt("Star"));
                item.setImage(object.getString("Image"));
                evaluateItems.add(item);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SendEvaluate() {
        RequestParams params = new RequestParams(Variables.SendEvaluate);
        params.addBodyParameter("orderId", order.getOrderID());
        params.addBodyParameter("customerId", order.getCustomerID());
        params.addBodyParameter("pointId", Variables.point.getID());
        params.addBodyParameter("distributionStar", pinglun_distributionStar.getRating() + "");
        params.addBodyParameter("serviceStar", pinglun_serviceStar.getRating() + "");
        if (pinglun_evaluateText.getText().toString().equals("")) {
            pinglun_evaluateText.setText("东西很不错，购买方便，好评");
        }
        params.addBodyParameter("evaluateText", pinglun_evaluateText.getText() + "");
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < evaluateItems.size(); i++) {
                JSONObject item = new JSONObject();
                EvaluateItem evaluateItem = evaluateItems.get(i);
                item.put("productName", evaluateItem.getProductName());
                item.put("productId", evaluateItem.getProductId());
                item.put("Image", evaluateItem.getImage());
                item.put("Star", evaluateItem.getStar());
                if (evaluateItem.getEvaluateText().equals("")) {
                    evaluateItem.setEvaluateText("东西很不错，购买方便，好评");
                }
                item.put("evaluateText", evaluateItem.getEvaluateText() + " ");
                array.put(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("evaluateStr", array.toString());
        Log.d("SendEvaluate", params.toString());
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
                    finish();
                } else {
                    Toast.makeText(PingLun.this, "提交评论出错", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<EvaluateItem> evaluateItems;

        MyAdapter(List<EvaluateItem> evaluateItems) {
            this.evaluateItems = evaluateItems;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(PingLun.this).inflate(R.layout.pinglun_item, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if (orderType) {
                holder.pinglun_item_describe.setText(evaluateItems.get(position).getProductName());
                holder.pinglun_item_rating.setRating(evaluateItems.get(position).getStar());
                holder.pinglun_item_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (rating < 1) {
                            rating = 1;
                            holder.pinglun_item_rating.setRating(rating);
                        }
                        evaluateItems.get(position).setStar(rating);
                    }
                });
                x.image().bind(holder.pinglun_item_image, goods.get(position).getImage());
                holder.pinglun_item_pinglunstr.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        evaluateItems.get(position).setEvaluateText(holder.pinglun_item_pinglunstr.getText().toString());
                    }
                });
            } else {
                holder.pinglun_item_describe.setText(evaluateItems.get(position).getProductName());
                holder.pinglun_item_rating.setRating(evaluateItems.get(position).getStar());
                holder.pinglun_item_rating.setIsIndicator(true);
                x.image().bind(holder.pinglun_item_image, evaluateItems.get(position).getImage());
                holder.pinglun_item_pinglunstr.setText("评价:" + evaluateItems.get(position).getEvaluateText());
                holder.pinglun_item_pinglunstr.setBackground(null);
                //判断第一个字符是否是【，是的话设置padding使评价与标题对齐
                if (evaluateItems.get(position).getProductName().substring(0, 1).equals("【")) {
                    holder.pinglun_item_pinglunstr.setPadding(20, 0, 0, 0);
                } else {
                    holder.pinglun_item_pinglunstr.setPadding(0, 0, 0, 0);
                }
                holder.pinglun_item_pinglunstr.setFocusable(false);
            }
        }

        @Override
        public int getItemCount() {
            return evaluateItems.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            RatingBar pinglun_item_rating;
            ImageView pinglun_item_image;
            TextView pinglun_item_describe;
            EditText pinglun_item_pinglunstr;

            public MyViewHolder(View itemView) {
                super(itemView);
                pinglun_item_rating = (RatingBar) itemView.findViewById(R.id.pinglun_item_rating);
                pinglun_item_image = (ImageView) itemView.findViewById(R.id.pinglun_item_image);
                pinglun_item_describe = (TextView) itemView.findViewById(R.id.pinglun_item_describe);
                pinglun_item_pinglunstr = (EditText) itemView.findViewById(R.id.pinglun_item_pinglunstr);
            }
        }

    }
}
