package com.example.lfy.myapplication.Group;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GroupGoodsBean;
import com.example.lfy.myapplication.Bean.GroupOrder;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.DividerItemDecoration;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class GroupNear extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout sw;
    private RecyclerView rv;
    private LinearLayoutManager manager;
    View view;
    private List<Integer> myList = new ArrayList<Integer>();
    private List<GroupOrder> groupOrders;
    MyAdapter adapter;
    RadioButton rb_progress, rb_time;
    int CurrentRb = 2;

    public GroupNear() {
        // Required empty public constructor
        manager = new LinearLayoutManager(getContext());
        groupOrders = new ArrayList<>();
        Near_xUtils(2);
        adapter = new MyAdapter(groupOrders);
    }


    //type=1按时间，type=2按进度，刚进来默认按进度
    private void Near_xUtils(int type) {
        RequestParams params = new RequestParams(Variables.GetTuanOrderByPoint);
        params.addBodyParameter("point", Variables.point.getID());
        params.addBodyParameter("type", type + "");
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
                    Log.d("数据", result);
                    Near_JSON(result);
                } else {
//                    success();
                }
                sw.setRefreshing(false);
            }

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    private void Near_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                if (groupOrders != null) {
                    groupOrders.clear();
                }
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    GroupOrder good = new GroupOrder();
                    good.setOrderID(everyone.getString("OrderID"));
                    good.setOrderNO(everyone.getString("OrderNO"));
                    good.setOrderPrice(everyone.getString("OrderPrice"));
                    good.setOrderType(everyone.getString("OrderType"));
                    good.setPoint(everyone.getString("point"));
                    good.setProductID(everyone.getString("ProductID"));
                    good.setCreateTime(everyone.getString("CreateTime"));
                    good.setPayTime(everyone.getString("PayTime"));
                    good.setCustomerStr(everyone.getString("CustomerStr"));
                    good.setCustomerNum(everyone.getInt("CustomerNum"));
                    good.setTuanNumber(everyone.getString("TuanNumber"));
                    good.setCost(everyone.getString("Cost"));
                    good.setCommand(everyone.getString("command"));
                    good.setIsSingleBuy(everyone.getString("IsSingleBuy"));
                    good.setCustomerID(everyone.getString("CustomerID"));
                    good.setCustomerName(everyone.getString("CustomerName"));
                    good.setPhoneNameber(everyone.getString("PhoneNameber"));
                    good.setTimestamp(everyone.getString("timestamp"));
                    good.setPayed(everyone.getString("Payed"));
                    good.setTuanid(everyone.getString("tuanid"));
                    good.setTitle(everyone.getString("title"));
                    String url = everyone.getString("img");
                    url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
                    good.setImg(url);
                    good.setTuanPrice(everyone.getString("tuanPrice"));
                    good.setMarketPrice(everyone.getString("marketPrice"));
                    good.setSinglePrice(everyone.getString("singlePrice"));
                    good.setStandard(everyone.getString("Standard"));
                    good.setPlace(everyone.getString("Place"));
                    good.setPersonNum(everyone.getInt("personNum"));
                    good.setTuanCount(everyone.getString("tuanCount"));
                    good.setShelfState(everyone.getString("ShelfState"));
                    groupOrders.add(good);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group_near, container, false);
        sw = (SwipeRefreshLayout) view.findViewById(R.id.groupnear_swipe_refresh);
        sw.setColorSchemeColors(R.color.green);
        sw.setOnRefreshListener(this);
        rv = (RecyclerView) view.findViewById(R.id.groupnear_recyclerView);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

//        new MyThread().start();
        rv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));
        rb_time = (RadioButton) view.findViewById(R.id.rb_time);
        rb_progress = (RadioButton) view.findViewById(R.id.rb_progress);
        rb_time.setOnClickListener(this);
        rb_progress.setOnClickListener(this);
        return view;
    }

    //下拉刷新里的操作
    @Override
    public void onRefresh() {
        Near_xUtils(CurrentRb);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_time:
                Near_xUtils(1);
                CurrentRb = 1;
                break;
            case R.id.rb_progress:
                Near_xUtils(2);
                CurrentRb = 2;
                break;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<GroupOrder> list;

        public MyAdapter(List<GroupOrder> list) {
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.groupnear_item, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
//            holder.countdowntime_tv.setText(list.get(position) + "");
            ImageOptions imageOptions = new ImageOptions.Builder()
                    .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setFailureDrawableId(R.mipmap.all_longding)
                    //设置使用缓存
                    .setUseMemCache(true)
                    .setLoadingDrawableId(R.mipmap.all_longding)
                    .build();
            x.image().bind(holder.groupnear_item_image, list.get(position).getImg(), imageOptions);
            holder.groupnear_item_title.setText(list.get(position).getTitle());
            holder.groupfind_item_price.setText(list.get(position).getTuanPrice());
            holder.countdowntime_tv.setText(list.get(position).getCreateTime());
            holder.groupnear_item_progressbar.setMax(list.get(position).getPersonNum());
            holder.groupnear_item_progressbar.setProgress(list.get(position).getCustomerNum());
            holder.groupnear_item_needmore.setText("还差" + (list.get(position).getPersonNum() - list.get(position).getCustomerNum()) + "人")
            ;


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView countdowntime_tv, groupnear_item_title, groupfind_item_price, groupnear_item_needmore;
            ImageView groupnear_item_image;
            ProgressBar groupnear_item_progressbar;

            public MyViewHolder(View itemView) {
                super(itemView);
                countdowntime_tv = (TextView) itemView.findViewById(R.id.countdowntimetv);
                CountDownTimer cdt = new CountDownTimer(600000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countdowntime_tv.setText(millisUntilFinished / 1000 + "");
                    }

                    @Override
                    public void onFinish() {
                        countdowntime_tv.setText("倒计时结束");
                    }
                };
//                cdt.start();
                groupnear_item_image = (ImageView) itemView.findViewById(R.id.groupnear_item_image);
                groupnear_item_title = (TextView) itemView.findViewById(R.id.groupnear_item_title);
                groupfind_item_price = (TextView) itemView.findViewById(R.id.groupfind_item_price);
                groupnear_item_needmore = (TextView) itemView.findViewById(R.id.groupnear_item_needmore);
                groupnear_item_progressbar = (ProgressBar) itemView.findViewById(R.id.groupnear_item_progressbar);
            }
        }
    }
}
