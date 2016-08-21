package com.example.lfy.myapplication.Group;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GroupOrder;
import com.example.lfy.myapplication.FragmentOrder.FragmentOrder;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class GroupMine extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout sw;
    private RecyclerView rv;
    private LinearLayoutManager lm;
    private RadioButton rb_singlebuy, rb_grouping, rb_groupsuccess, rb_groupfalse;

    View view;
    //服务器时间戳
    long time;
    List<GroupOrder> groupOrders;
    MyAdapter adapter;
    //当前选中的radiobutton
    int currentRb = 0;
    //type=0 代表是下拉刷新 type=1代表上拉加载
    int type = 0;

    public GroupMine() {
        // Required empty public constructor
        groupOrders = new ArrayList<>();
        adapter = new MyAdapter(groupOrders);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_group_mine, container, false);
        sw = (SwipeRefreshLayout) view.findViewById(R.id.groupmine_swipe_refresh);
        sw.setOnRefreshListener(this);
        rv = (RecyclerView) view.findViewById(R.id.groupmine_recyclerView);
        lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
//        rv.addItemDecoration(new DividerItemDecoration(getContext(),
//                DividerItemDecoration.VERTICAL_LIST));
        rb_singlebuy = (RadioButton) view.findViewById(R.id.rb_singlebuy);
        rb_grouping = (RadioButton) view.findViewById(R.id.rb_grouping);
        rb_groupsuccess = (RadioButton) view.findViewById(R.id.rb_groupsuccess);
        rb_groupfalse = (RadioButton) view.findViewById(R.id.rb_groupfalse);
        rb_singlebuy.setOnClickListener(this);
        rb_grouping.setOnClickListener(this);
        rb_groupsuccess.setOnClickListener(this);
        rb_groupfalse.setOnClickListener(this);
        loadMore();
        update();
        return view;
    }

    int lastVisibleItem;

    private void loadMore() {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("lastVisibleItem", lastVisibleItem + "");

                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    Log.d("newState", newState + "");
                    Log.d("SCROLL_STATE_IDLE", RecyclerView.SCROLL_STATE_IDLE + "");
                    Log.d("adapter.getItemCount()", adapter.getItemCount() + "");
                    if (lastVisibleItem >= 9) {
                        if (lastVisibleItem == -1) {
                            update();
                        } else {
                            sw.setRefreshing(true);
                            loadMoreUpdate();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = lm.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_grouping:
                currentRb = 0;
                break;
            case R.id.rb_groupsuccess:
                currentRb = 1;
                break;
            case R.id.rb_groupfalse:
                currentRb = 2;
                break;
            case R.id.rb_singlebuy:
                currentRb = 3;
                break;
        }
        update();
    }


    @Override
    public void onRefresh() {
        update();
    }

    private void update() {
        if (Variables.my != null) {
            sw.setRefreshing(true);
            adapter.lastPosition = -1;
            lastVisibleItem = -1;
            groupOrders.clear();
            adapter.notifyDataSetChanged();
            TimeStamp();
        } else {
            sw.setRefreshing(false);
        }
    }

    private void loadMoreUpdate() {
        if (Variables.my != null) {
            type = 1;
            switch (currentRb) {
                case 0:
                    Mine_xUtils(0, 1, groupOrders.get(lastVisibleItem).getTimestamp());
                    break;
                case 1:
                    Mine_xUtils(1, 1, groupOrders.get(lastVisibleItem).getTimestamp());
                    break;
                case 2:
                    Mine_xUtils(2, 1, groupOrders.get(lastVisibleItem).getTimestamp());
                    break;
                case 3:
                    Mine_xUtils(3, 1, groupOrders.get(lastVisibleItem).getTimestamp());
                    break;
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<GroupOrder> groupOrders;
        public int lastPosition = -1;

        MyAdapter(List<GroupOrder> groupOrders) {
            this.groupOrders = groupOrders;
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.groupmine_item, parent, false));
            return viewHolder;
        }

        protected void setAnimation(View viewToAnimate, int position) {

            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_slide_bottom_up);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public void onViewDetachedFromWindow(MyViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }


        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
            setAnimation(holder.itemView, position);
            ImageOptions imageOptions = new ImageOptions.Builder()
                    .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setFailureDrawableId(R.mipmap.all_longding)
                    //设置使用缓存
                    .setUseMemCache(true)
                    .setLoadingDrawableId(R.mipmap.all_longding)
                    .build();
            x.image().bind(holder.groupmine_item_image, groupOrders.get(position).getImg(), imageOptions);
//            holder.groupmine_item_image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    removeItem(position);
//                }
//            });
            holder.groupmine_item_title.setText(groupOrders.get(position).getTitle());
            holder.groupmine_item_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), GroupDetail.class);
                    intent.putExtra("groupOrder", groupOrders.get(position));
                    intent.putExtra("from", "GroupMine" + currentRb);
                    startActivity(intent);
                }
            });
            holder.groupmine_item_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), GroupOrderDetail.class);
                    intent.putExtra("groupOrder", groupOrders.get(position));
                    startActivity(intent);
                }
            });
            switch (currentRb) {
                case 0:
                    holder.groupmine_item_state.setText(rb_grouping.getText());
                    holder.groupmine_item_state.setTextColor(getResources().getColor(R.color.green));
                    holder.groupmine_item_price.setText(groupOrders.get(position).getTuanPrice() + "");
                    holder.groupmine_item_order.setVisibility(View.GONE);
                    holder.groupmine_item_countdown.setVisibility(View.VISIBLE);
                    if (holder.cd != null) {
                        holder.cd.cancel();
                    }
                    String CreateTime = groupOrders.get(position).getCreateTime().substring(0, 19);
                    CreateTime = CreateTime.replaceAll("T", "-");
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                    String nowday = sDateFormat.format(new Date());
                    final long time = compare_date(nowday, CreateTime);
                    holder.cd = new CountDownTimer(time - 1000 * 60 * 60 * 22 - 1000 * 60 * 14, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            setDate(millisUntilFinished, holder.groupmine_item_countdown);
                        }

                        @Override
                        public void onFinish() {
//                            holder.itemView.setVisibility(View.GONE);
                            Log.d("移除的position", position + "");
                            removeItem(position);
                        }
                    };
                    holder.cd.start();
                    break;
                case 1:
                    holder.groupmine_item_state.setText(rb_groupsuccess.getText());
                    holder.groupmine_item_state.setTextColor(getResources().getColor(R.color.green));
                    holder.groupmine_item_price.setText(groupOrders.get(position).getTuanPrice() + "");
                    holder.groupmine_item_order.setVisibility(View.VISIBLE);
                    holder.groupmine_item_countdown.setVisibility(View.GONE);
                    break;
                case 2:
                    holder.groupmine_item_state.setText(rb_groupfalse.getText());
                    holder.groupmine_item_state.setTextColor(getResources().getColor(R.color.text_orgin));
                    holder.groupmine_item_price.setText(groupOrders.get(position).getTuanPrice() + "");
                    holder.groupmine_item_order.setVisibility(View.GONE);
                    holder.groupmine_item_countdown.setVisibility(View.GONE);
                    break;
                case 3:
                    holder.groupmine_item_state.setText(rb_singlebuy.getText());
                    holder.groupmine_item_state.setTextColor(getResources().getColor(R.color.car_grey));
                    holder.groupmine_item_price.setText(groupOrders.get(position).getSinglePrice() + "");
                    holder.groupmine_item_order.setVisibility(View.VISIBLE);
                    holder.groupmine_item_countdown.setVisibility(View.GONE);
                    break;
            }
        }

        public void removeItem(int position) {
            if (rv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE && !rv.isComputingLayout()) {
                groupOrders.remove(position);
                notifyItemRemoved(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();

                    }
                }, 1000);
            }
        }

        @Override
        public int getItemCount() {
            return groupOrders.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView groupmine_item_countdown, groupmine_item_order, groupmine_item_group, groupmine_item_title, groupmine_item_price, groupmine_item_state;
            ImageView groupmine_item_image;
            CountDownTimer cd = null;

            public MyViewHolder(View itemView) {
                super(itemView);
                groupmine_item_order = (TextView) itemView.findViewById(R.id.groupmine_item_order);
                groupmine_item_group = (TextView) itemView.findViewById(R.id.groupmine_item_group);
                groupmine_item_image = (ImageView) itemView.findViewById(R.id.groupmine_item_image);
                groupmine_item_title = (TextView) itemView.findViewById(R.id.groupmine_item_title);
                groupmine_item_price = (TextView) itemView.findViewById(R.id.groupmine_item_price);
                groupmine_item_state = (TextView) itemView.findViewById(R.id.groupmine_item_state);
                groupmine_item_countdown = (TextView) itemView.findViewById(R.id.groupmine_item_countdown);
            }
        }
    }

    //通过ordertype,payed查询，
    // ordertype=0,payed=1(拼团中),
    // ordertype=1,payed=1(拼团成功未提货),
    // ordertype=2,payed=1(拼团成功已提货)，返回2和4的数据（4为已退款）
    // ordertype=3,payed=1(单独购)
    private void Mine_xUtils(int ordertype, int payed, long timestamp) {
        RequestParams params = new RequestParams(Variables.GetTuanOrder);
        params.addBodyParameter("timestamp", timestamp + "");
        params.addBodyParameter("payed", payed + "");
        params.addBodyParameter("ordertype", ordertype + "");
        params.addBodyParameter("search", Variables.my.getCustomerID());
        Log.d("获取用户团购订单", params.toString());
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
                    Mine_JSON(result, type);
                } else {
//                    success();
                    sw.setRefreshing(false);
                }

            }

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    //type=0 代表是下拉刷新 type=1代表上拉加载
    private void Mine_JSON(String json, int type) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            boolean flag = false;
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    final GroupOrder good = new GroupOrder();
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
                    good.setCost(everyone.getDouble("Cost"));
                    good.setCommand(everyone.getString("command"));
                    good.setIsSingleBuy(everyone.getInt("IsSingleBuy"));
                    good.setCustomerID(everyone.getString("CustomerID"));
                    good.setCustomerName(everyone.getString("CustomerName"));
                    good.setPhoneNameber(everyone.getString("PhoneNameber"));
                    good.setTimestamp(everyone.getLong("timestamp"));
                    good.setPayed(everyone.getString("Payed"));
                    good.setTuanid(everyone.getString("tuanid"));
                    good.setTitle(everyone.getString("title"));
                    String url = everyone.getString("img");
                    url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
                    good.setImg(url);
                    good.setDetail(everyone.getString("detail"));
                    good.setTuanPrice(everyone.getDouble("tuanPrice"));
                    good.setMarketPrice(everyone.getString("marketPrice"));
                    good.setSinglePrice(everyone.getDouble("singlePrice"));
                    good.setStandard(everyone.getString("Standard"));
                    good.setPlace(everyone.getString("Place"));
                    good.setPersonNum(everyone.getInt("personNum"));
                    good.setTuanCount(everyone.getString("tuanCount"));
                    good.setShelfState(everyone.getString("ShelfState"));
                    good.setCustomerOrderNo(everyone.getString("CustomerOrderNo"));
                    if (type == 1) {//上拉加载更多
                        //判断最后一个item的时间戳和获取到的数据的时间戳是否相同，相同的话就跳过此次循环
                        if (good.getTimestamp() == groupOrders.get(lastVisibleItem).getTimestamp()) {
                            continue;
                        }//如果只有一个数据并且时间戳一样，说明没有新数据，所以flag的状态没改变，为false，
                        // 这时就不执行适配器的刷新
                    }
                    //有进行新数据的添加说明数据有变化，flag设为true
                    flag = true;
                    groupOrders.add(good);
                }
                if (flag) {
                    if (type == 1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sw.setRefreshing(false);
                                adapter.notifyDataSetChanged();
                            }
                        }, 500);
                    } else {
                        sw.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    sw.setRefreshing(false);
                    Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                }
            } else {
                sw.setRefreshing(false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void TimeStamp() {
        RequestParams params = new RequestParams(Variables.http_time);
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
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        time = jsonObject.getLong("Msg");
                        type = 0;
                        switch (currentRb) {
                            case 0:
                                Mine_xUtils(0, 1, time);
                                break;
                            case 1:
                                Mine_xUtils(1, 1, time);
                                break;
                            case 2:
                                Mine_xUtils(2, 1, time);
                                break;
                            case 3:
                                Mine_xUtils(3, 1, time);
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public long compare_date(String now, String create) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        try {
            Date dt1 = sDateFormat.parse(now);
            Date dt2 = sDateFormat.parse(create);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt2);
            cal.add(Calendar.DATE, 1);
            long diff = cal.getTime().getTime() - dt1.getTime();
            return diff;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public void setDate(long diff, TextView tv) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        // 计算差多少天
//        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        long sec = diff % nd % nh % nm / ns;
        String time = "";

        if (hour < 10) {
            time += "0";
        }
        time += hour + ":";
        if (min < 10) {
            time += "0";
        }
        time += min + ":";

        if (sec < 10) {
            time += "0";
        }
        time += sec;

//        String time = (hour > 0 ? hour + "小时" : "") + (min > 0 ? min + "分钟" : "") + (sec > 0 ? sec + "秒" : "");
        tv.setText(time);
    }

}
