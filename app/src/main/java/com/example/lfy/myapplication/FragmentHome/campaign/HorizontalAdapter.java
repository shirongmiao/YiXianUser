package com.example.lfy.myapplication.FragmentHome.campaign;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.FootPhoto;
import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.xutils.x;

import java.util.List;

/**
 * Created by lfy on 2016/6/4.
 */
public class HorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<GridPhoto> horizontal;
    FootPhoto footPhoto;

    public void addDate(List<GridPhoto> horizontal, FootPhoto footPhoto) {
        this.horizontal = horizontal;
        this.footPhoto = footPhoto;
    }

    Context context;

    //设置ITEM类型，可以自由发挥，这里设置item position单数显示item1 偶数显示item2
    @Override
    public int getItemViewType(int position) {//Enum类提供了一个ordinal()方法，返回枚举类型的序数，这里ITEM_TYPE.ITEM1.ordinal()代表0， ITEM_TYPE.ITEM2.ordinal()代表1
        return position != horizontal.size() ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//加载Item View的时候根据不同TYPE加载不同的布局
        context = parent.getContext();
        if (viewType == 0) {
            return new Item1ViewHolder(LayoutInflater.from(context).inflate(R.layout.home_horizontal_item, parent, false));
        } else {
            return new Item2ViewHolder(LayoutInflater.from(context).inflate(R.layout.campaigntent_more, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        holder.itemView.setTag(position);
        if (holder instanceof Item1ViewHolder) {
            FrameLayout.LayoutParams topimage;
            topimage = (FrameLayout.LayoutParams) ((Item1ViewHolder) holder).grid_liner.getLayoutParams();
            topimage.width = Variables.PhoneWidth / 3;
            ((Item1ViewHolder) holder).grid_liner.setLayoutParams(topimage);
            x.image().bind(((Item1ViewHolder) holder).imageView, horizontal.get(position).getImage());
            ((Item1ViewHolder) holder).price.setText(horizontal.get(position).getPrice() + "");
            ((Item1ViewHolder) holder).vip.setText(horizontal.get(position).getPromotionPrice() + "");
            ((Item1ViewHolder) holder).content.setText(horizontal.get(position).getTitle());
        } else if (holder instanceof Item2ViewHolder) {
            ((Item2ViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Campaign.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("campaign", footPhoto);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return horizontal.size() + 1;
    }

    class Item1ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView content;
        TextView price;
        TextView vip;
        LinearLayout grid_liner;

        public Item1ViewHolder(View itemView) {
            super(itemView);
            grid_liner = (LinearLayout) itemView.findViewById(R.id.grid_liner);
            imageView = (ImageView) itemView.findViewById(R.id.horizontal_image);
            content = (TextView) itemView.findViewById(R.id.horizontal_content);
            price = (TextView) itemView.findViewById(R.id.horizontal_price);
            vip = (TextView) itemView.findViewById(R.id.horizontal_vip);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Goods_Particular.class);
                    intent.putExtra("productId", horizontal.get(getLayoutPosition()).getProductID());
                    context.startActivity(intent);
                }
            });
        }
    }

    //item2 的ViewHolder
    public class Item2ViewHolder extends RecyclerView.ViewHolder {

        public Item2ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
