package com.example.lfy.myapplication.Group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GroupOrder;
import com.example.lfy.myapplication.R;
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


public class GroupFind extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout sw;
    TextView tv;
    RecyclerView rv;
    View view;
    List<GroupOrder> groupGoodslist;
    MyAdapter adapter;

    public GroupFind() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupGoodslist = new ArrayList<>();
        Find_xUtils();
        view = inflater.inflate(R.layout.fragment_group_find, container, false);
        sw = (SwipeRefreshLayout) view.findViewById(R.id.groupfind_swipe_refresh);
        sw.setOnRefreshListener(this);
//        tv = (TextView) view.findViewById(R.id.groupfind_tv);
        rv = (RecyclerView) view.findViewById(R.id.groupfind_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter(groupGoodslist);
        rv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onRefresh() {
        Find_xUtils();
    }

    private void Find_xUtils() {
        RequestParams params = new RequestParams(Variables.http_GetTuanProduct);
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
                    Find_JSON(result);
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

    private void Find_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                if (groupGoodslist != null) {
                    groupGoodslist.clear();
                }
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    GroupOrder good = new GroupOrder();
                    good.setTuanid(everyone.getString("tuanid"));
                    good.setTitle(everyone.getString("title"));
                    String url = everyone.getString("img");
                    url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
                    good.setImg(url);
                    good.setDetail(everyone.getString("detail"));
                    good.setTuanPrice(everyone.getDouble("tuanPrice"));
                    good.setMarketPrice(everyone.getString("marketPrice"));
                    good.setSinglePrice(everyone.getString("singlePrice"));
                    good.setCost(everyone.getDouble("cost"));
                    good.setStandard(everyone.getString("Standard"));
                    good.setPlace(everyone.getString("Place"));
                    good.setPersonNum(everyone.getInt("personNum"));
                    good.setTuanCount(everyone.getString("tuanCount"));
                    good.setShelfState(everyone.getString("ShelfState"));
                    groupGoodslist.add(good);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<GroupOrder> goodslist;

        MyAdapter(List<GroupOrder> goodslist) {
            this.goodslist = goodslist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.groupfind_item, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.price.setText(goodslist.get(position).getTuanPrice() + "");
            holder.title.setText(goodslist.get(position).getTitle());
            holder.btn.setText(goodslist.get(position).getPersonNum() + "人团|去开团");
            ImageOptions imageOptions = new ImageOptions.Builder()
                    .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setFailureDrawableId(R.mipmap.all_longding)
                    //设置使用缓存
                    .setUseMemCache(true)
                    .setLoadingDrawableId(R.mipmap.all_longding)
                    .build();
            x.image().bind(holder.image, goodslist.get(position).getImg(), imageOptions);
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(), groupGoodslist.get(position).getTuanid()+"", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), GroupGoodParticular.class);
                    intent.putExtra("groupgood", groupGoodslist.get(position));
                    intent.putExtra("from", "GroupFind");
                    startActivity(intent);
                }
            });
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), GroupGoodParticular.class);
                    intent.putExtra("groupgood", groupGoodslist.get(position));
                    intent.putExtra("from", "GroupFind");
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return goodslist.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            Button btn;
            TextView price, title;
            ImageView image;
            CardView cv;


            public MyViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.groupfind_item_cardview);
                btn = (Button) itemView.findViewById(R.id.groupfind_item_buybutton);
                price = (TextView) itemView.findViewById(R.id.groupfind_item_price);
                title = (TextView) itemView.findViewById(R.id.groupfind_item_title);
                image = (ImageView) itemView.findViewById(R.id.groupfind_item_image);

            }
        }
    }
}
