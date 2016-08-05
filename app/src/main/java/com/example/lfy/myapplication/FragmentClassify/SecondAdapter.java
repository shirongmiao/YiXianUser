package com.example.lfy.myapplication.FragmentClassify;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.xutils.x;

import java.util.List;

/**
 * Created by lfy on 2016/6/4.
 */
public class SecondAdapter extends RecyclerView.Adapter<SecondAdapter.ViewHold> {

    String type;

    public void setType(String type) {
        this.type = type;
    }

    List<GridPhoto> classify;

    public void addDate(List<GridPhoto> horizontal) {
        this.classify = horizontal;
    }

    private OnClickListen Listen;

    public void setOnClickListen(OnClickListen Listen) {
        this.Listen = Listen;
    }


    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (type.equals("1")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classify_second_item1, parent, false);
        } else if (type.equals("2")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classify_second_item2, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classify_second_item3, parent, false);
        }
        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        x.image().bind(holder.imageView, classify.get(position).getImage());
        holder.price.setText("￥" + classify.get(position).getPrice() + "/");
        holder.vip.setText("会员价￥" + classify.get(position).getPromotionPrice());
        holder.second_PromotionName.setText(classify.get(position).getPromotionName());
        holder.content.setText(classify.get(position).getTitle());
        holder.second_weight.setText(classify.get(position).getStandard());
        if (type.equals("1")) {
            holder.second_address.setText(classify.get(position).getPlace());
        }

        if (Variables.point.getState().equals("1")) {
            holder.second_into.setEnabled(true);
            holder.second_into.setBackgroundResource(R.drawable.all_add_car_select);
        } else {
            holder.second_into.setEnabled(false);
            holder.second_into.setBackgroundResource(R.drawable.round_hollow_down);
        }
    }

    @Override
    public int getItemCount() {
        return classify.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView content;
        TextView price;
        TextView vip;
        Button second_into;
        TextView second_PromotionName;
        TextView second_weight;
        TextView second_address;

        public ViewHold(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.second_image);
            content = (TextView) itemView.findViewById(R.id.second_content);
            price = (TextView) itemView.findViewById(R.id.second_price);
            vip = (TextView) itemView.findViewById(R.id.second_vip);
            second_into = (Button) itemView.findViewById(R.id.second_into);
            second_PromotionName = (TextView) itemView.findViewById(R.id.second_PromotionName);
            second_weight = (TextView) itemView.findViewById(R.id.second_weight);
            second_address = (TextView) itemView.findViewById(R.id.second_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SetOnItemClick(classify.get(getLayoutPosition()).getProductID());
                    }
                }
            });

            second_into.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SerOnClick(classify.get(getLayoutPosition()).getProductID());
                    }
                }
            });
        }
    }

    public interface OnClickListen {
        void SetOnItemClick(String productId);

        void SerOnClick(String productId);
    }
}
