package com.example.lfy.myapplication.FragmentMine.collection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.FragmentHome.search.Search;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/2/17.
 */
public class Collection_Adapter extends RecyclerView.Adapter<Collection_Adapter.ViewHolder> {

    private LayoutInflater inflater;
    List<GridPhoto> date = new ArrayList<>();
    Context context;
    String from;

    private OnItemClickListener listenr;

    public void setOnItemClickListener(OnItemClickListener li) {
        this.listenr = li;
    }

    /**
     * 直接添加  不需要 new 构造器
     * <p/>
     * * @param datas
     */
    public void addDatas(List<GridPhoto> datas, String from) {
        date.addAll(datas);
        this.from = from;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.classify_second_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.Title.setText(date.get(position).getTitle());
        holder.PromotionName.setText(date.get(position).getPromotionName());
        holder.price.setText("￥" + date.get(position).getPrice());
        holder.VipPrice.setText("￥" + date.get(position).getPromotionPrice());
        x.image().bind(holder.img, date.get(position).getImage());
        holder.place.setText(date.get(position).getStandard());

        if (Variables.point.getState().equals("1")) {

            holder.add.setEnabled(true);
            holder.add.setBackgroundResource(R.drawable.all_add_car_select);

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Variables.my != null) {
                        Insert_xUtils(date.get(position).getProductID());
                    } else {
                        Intent intent = new Intent(context, LoginBg.class);
                        context.startActivity(intent);
                    }
                }
            });
        }else {
            holder.add.setEnabled(true);
            holder.add.setBackgroundResource(R.drawable.round_hollow_down);
        }

    }


    @Override
    public int getItemCount() {
        return date.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView price;
        TextView VipPrice;
        TextView PromotionName;
        ImageView img;
        Button add;
        TextView place;

        public ViewHolder(final View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.second_content);
            VipPrice = (TextView) itemView.findViewById(R.id.second_vip);
            price = (TextView) itemView.findViewById(R.id.second_price);
            PromotionName = (TextView) itemView.findViewById(R.id.second_PromotionName);
            img = (ImageView) itemView.findViewById(R.id.second_image);
            add = (Button) itemView.findViewById(R.id.second_into);
            place = (TextView) itemView.findViewById(R.id.second_weight);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenr != null) {
                        listenr.onItemClick(getLayoutPosition(), date.get(getLayoutPosition()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, GridPhoto data);
//        void onClick(View v, int position);
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
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int Ret = jsonObject.getInt("Ret");
                        if (Ret == 0) {
                            Toast.makeText(x.app(), jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
                        } else {
                            Variables.count = Variables.count + 1;
                            if (from.equals("Collection")) {
                                Collection.bv.setBadgeCount(Variables.count);
                            } else {
                                Search.bv.setBadgeCount(Variables.count);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
}
