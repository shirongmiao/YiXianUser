package com.example.lfy.myapplication.FragmentOrder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.R;

import org.xutils.x;

import java.util.List;


/**
 *
 * Created by lfy on 2015/12/17.
 */
public class Order_item_adapter extends BaseAdapter {

    List<CarDbBean> list;

    public Order_item_adapter(List<CarDbBean> user_goods) {
        this.list = user_goods;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.order_item_adapter, null);

            holder.Fruit_Image = (ImageView) view.findViewById(R.id.Fruit_Image);
            holder.Fruit_Name = (TextView) view.findViewById(R.id.Fruit_Name);
            holder.Standard = (TextView) view.findViewById(R.id.Standard);
            holder.Promotion = (TextView) view.findViewById(R.id.Promotion);
            holder.Fruit_Price = (TextView) view.findViewById(R.id.Fruit_Price);
            holder.Fruit_Num = (TextView) view.findViewById(R.id.Fruit_Num);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        x.image().bind(holder.Fruit_Image, list.get(position).getImage());
        holder.Fruit_Name.setText(list.get(position).getTitle());
        holder.Standard.setText(list.get(position).getStandard());
        holder.Promotion.setText(list.get(position).getPromotionName());

        holder.Fruit_Price.setText((Math.round(list.get(position).getPrice() * 10000) / 10000.00) + "");
        holder.Fruit_Num.setText("x" + list.get(position).getProductCount());


        return view;
    }


    class ViewHolder {
        ImageView Fruit_Image;
        TextView Fruit_Name;
        TextView Standard;
        TextView Promotion;
        TextView Fruit_Price;
        TextView Fruit_Num;
    }


}
