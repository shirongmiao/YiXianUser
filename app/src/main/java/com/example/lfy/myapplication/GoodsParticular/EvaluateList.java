package com.example.lfy.myapplication.GoodsParticular;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.Evaluate;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.DividerItemDecoration;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvaluateList extends AppCompatActivity {
    RecyclerView evaluate_rv;
    EvaluateListAdapter adapter;
    List<Evaluate> list;
    ImageView evaluate_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.activity_evaluate_list);
        initView();
        Intent intent = getIntent();
        String productId = intent.getStringExtra("productId");
        GetEvaluateList(productId);
    }

    private void initView() {
        evaluate_rv = (RecyclerView) findViewById(R.id.evaluate_rv);
        list = new ArrayList<>();
        adapter = new EvaluateListAdapter(list);
        evaluate_rv.setAdapter(adapter);
        evaluate_rv.setLayoutManager(new LinearLayoutManager(this));
        evaluate_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        evaluate_back = (ImageView) findViewById(R.id.evaluate_back);
        evaluate_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void GetEvaluateList(String productId) {
        RequestParams params = new RequestParams(Variables.ProductEvaluate);
        params.addBodyParameter("productId", productId);
        Log.d("获取评价列表", params.toString());
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

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
                    Evaluate_JSON(result);
                } else {
//                    success();
                }
            }

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    private void Evaluate_JSON(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Evaluate item = new Evaluate();
                    item.setCreatTime(object.getString("creatTime"));
                    item.setCustomerName(object.getString("CustomerName"));
                    item.setStar(object.getInt("star"));
                    item.setEvaluateText(object.getString("evaluateText"));
                    list.add(item);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //判断是否是手机号码
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    //将手机号码中的4位数用星号代替
    private void setPhoneNumber(TextView mPhoneNumber, String pNumber) {
        if (!TextUtils.isEmpty(pNumber) && pNumber.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            mPhoneNumber.setText(sb.toString());
        }
    }

    class EvaluateListAdapter extends RecyclerView.Adapter<EvaluateListAdapter.EvaluateListViewHolder> {

        List<Evaluate> list;

        public EvaluateListAdapter(List<Evaluate> list) {
            this.list = list;
        }

        @Override
        public EvaluateListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            EvaluateListViewHolder viewHolder = new EvaluateListViewHolder(LayoutInflater.from(EvaluateList.this).inflate(R.layout.activity_evaluate_list_item, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(EvaluateListViewHolder holder, int position) {
            String str = list.get(position).getCreatTime();
            str = str.substring(0, str.lastIndexOf("T"));
            str = str.replace("-", ".");
            holder.evaluate_item_creatTime.setText(str);
            //如果名字是手机号，则隐藏中间4位数
            if (isMobileNO(list.get(position).getCustomerName())) {
                setPhoneNumber(holder.evaluate_item_CustomerName, list.get(position).getCustomerName());
            } else {
                holder.evaluate_item_CustomerName.setText(list.get(position).getCustomerName());
            }
            holder.evaluate_item_star.setRating(list.get(position).getStar());
            if (list.get(position).getEvaluateText() != "") {
                holder.evaluate_item_evaluateText.setVisibility(View.VISIBLE);
                holder.evaluate_item_evaluateText.setText(list.get(position).getEvaluateText());
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class EvaluateListViewHolder extends RecyclerView.ViewHolder {
            TextView evaluate_item_CustomerName, evaluate_item_creatTime, evaluate_item_evaluateText;
            RatingBar evaluate_item_star;

            public EvaluateListViewHolder(View itemView) {
                super(itemView);
                evaluate_item_CustomerName = (TextView) itemView.findViewById(R.id.evaluate_item_CustomerName);
                evaluate_item_creatTime = (TextView) itemView.findViewById(R.id.evaluate_item_creatTime);
                evaluate_item_evaluateText = (TextView) itemView.findViewById(R.id.evaluate_item_evaluateText);
                evaluate_item_star = (RatingBar) itemView.findViewById(R.id.evaluate_item_star);
            }
        }
    }
}
