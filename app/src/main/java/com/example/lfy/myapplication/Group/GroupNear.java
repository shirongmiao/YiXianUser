package com.example.lfy.myapplication.Group;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class GroupNear extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout sw;
    private RecyclerView rv;
    private LinearLayoutManager manager;
    View view;
    private List<GroupOrder> groupOrders;
    MyAdapter adapter;
    RadioButton rb_progress, rb_time;
    //当前选中的radiobutton，默认是2按进度（后台获取按进度订单的参数为2，按时间为1）
    int CurrentRb = 2;

    public GroupNear() {
        // Required empty public constructor
        manager = new LinearLayoutManager(getContext());
        groupOrders = new ArrayList<>();
//        Near_xUtils(CurrentRb);
        adapter = new MyAdapter(groupOrders);
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
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        //添加下划线
//        rv.addItemDecoration(new DividerItemDecoration(getContext(),
//                DividerItemDecoration.VERTICAL_LIST));
        rb_time = (RadioButton) view.findViewById(R.id.rb_time);
        rb_progress = (RadioButton) view.findViewById(R.id.rb_progress);
        rb_time.setOnClickListener(this);
        rb_progress.setOnClickListener(this);
        return view;
    }

    //下拉刷新里的操作
    @Override
    public void onRefresh() {
        //根据当前选中的radiobutton进行网络请求刷新
        update();
    }

    @Override
    public void onResume() {
        super.onResume();
        groupOrders.clear();
        if (Variables.my != null) {
            sw.setRefreshing(true);
            Near_xUtils(CurrentRb);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_time:
                CurrentRb = 1;
                update();
                break;
            case R.id.rb_progress:
                CurrentRb = 2;
                update();
                break;
        }
    }

    private void update() {
        if (Variables.my != null) {
            sw.setRefreshing(true);
            groupOrders.clear();
            adapter.notifyDataSetChanged();
            adapter.lastPosition = -1;
            Near_xUtils(CurrentRb);
        } else {
            sw.setRefreshing(false);
        }
    }

    //type=1按时间，type=2按进度，刚进来默认按进度
    private void Near_xUtils(int type) {
        RequestParams params = new RequestParams(Variables.GetTuanOrderByPoint);
        params.addBodyParameter("point", Variables.point.getID());
        params.addBodyParameter("type", type + "");
        params.addBodyParameter("customerId", Variables.my.getCustomerID());
        Log.d("获取附近的团", params.toString());
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
                    good.setCost(everyone.getDouble("Cost"));
                    good.setCommand(everyone.getString("command"));
                    good.setIsSingleBuy(everyone.getInt("IsSingleBuy"));
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
                    groupOrders.add(good);
                }
            } else {

            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<GroupOrder> list;
        public int lastPosition = -1;

        public MyAdapter(List<GroupOrder> list) {
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.groupnear_item, parent, false));
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
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            setAnimation(holder.itemView, position);
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
            holder.groupfind_item_price.setText(list.get(position).getTuanPrice() + "");
            holder.groupnear_item_progressbar.setMax(list.get(position).getPersonNum());
            holder.groupnear_item_progressbar.setProgress(list.get(position).getCustomerNum());
            int num = list.get(position).getPersonNum() - list.get(position).getCustomerNum();
            if ((num * 100) / holder.groupnear_item_progressbar.getMax() < 20) {
                holder.groupnear_item_needmore.setText("仅剩" + num + "人");
            } else {
                holder.groupnear_item_needmore.setText("还差" + num + "人");
            }
            holder.groupnear_item_percent.setText(((holder.groupnear_item_progressbar.getProgress() * 100) / holder.groupnear_item_progressbar.getMax()) + "%");

            holder.groupnear_item_linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), GroupGoodParticular.class);
                    intent.putExtra("groupgood", list.get(position));
                    intent.putExtra("from", "GroupNear");
                    startActivity(intent);
                }
            });
            if (holder.cd != null) {
                holder.cd.cancel();
            }
            String CreateTime = groupOrders.get(position).getCreateTime().substring(0, 19);
            CreateTime = CreateTime.replaceAll("T", "-");
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String nowday = sDateFormat.format(new Date());
            final long time = compare_date(nowday, CreateTime);
            holder.cd = new CountDownTimer(time, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setDate(millisUntilFinished, holder.groupnear_item_countdown);
                }

                @Override
                public void onFinish() {
                    holder.groupnear_item_countdown.setText("拼团失败");
                    holder.groupnear_item_countdown.setTextColor(Color.parseColor("#f95300"));
//                    removeItem(position);
                }
            };
            holder.cd.start();
            holder.group_near_item_joinbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), GroupGoodParticular.class);
                    intent.putExtra("groupgood", list.get(position));
                    intent.putExtra("from", "GroupNear");
                    startActivity(intent);
                }
            });

        }

        public void removeItem(int position) {
            list.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
//            if (position != list.size()) {      // 这个判断的意义就是如果移除的是最后一个，就不用管它了
//                notifyItemRangeChanged(position, list.size() - position);
//            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView groupnear_item_countdown, groupnear_item_title, groupfind_item_price, groupnear_item_percent, groupnear_item_needmore;
            ImageView groupnear_item_image;
            ProgressBar groupnear_item_progressbar;
            LinearLayout groupnear_item_linearlayout;
            Button group_near_item_joinbtn;

            CountDownTimer cd = null;

            public MyViewHolder(View itemView) {
                super(itemView);

                groupnear_item_image = (ImageView) itemView.findViewById(R.id.groupnear_item_image);
                groupnear_item_title = (TextView) itemView.findViewById(R.id.groupnear_item_title);
                groupfind_item_price = (TextView) itemView.findViewById(R.id.groupfind_item_price);
                groupnear_item_percent = (TextView) itemView.findViewById(R.id.groupnear_item_percent);
                groupnear_item_progressbar = (ProgressBar) itemView.findViewById(R.id.groupnear_item_progressbar);
                groupnear_item_linearlayout = (LinearLayout) itemView.findViewById(R.id.groupnear_item_linearlayout);
                groupnear_item_needmore = (TextView) itemView.findViewById(R.id.groupnear_item_needmore);
                groupnear_item_countdown = (TextView) itemView.findViewById(R.id.groupnear_item_countdown);
                group_near_item_joinbtn = (Button) itemView.findViewById(R.id.group_near_item_joinbtn);
            }
        }
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

    public static void setDate(long diff, TextView tv) {

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
