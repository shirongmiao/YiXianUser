package com.example.lfy.myapplication.FragmentMine.discount;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.CouponBean;
import com.example.lfy.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by lfy on 2015/12/14.
 */
public class Coupon_adapter extends BaseAdapter {

    List<CouponBean> car;

    public Coupon_adapter(List<CouponBean> car) {
        this.car = car;
    }

    @Override
    public int getCount() {
        return car.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.mine_discount_adapter, null);

            holder.discount_bg = (ImageView) view.findViewById(R.id.discount_bg);
            holder.price = (TextView) view.findViewById(R.id.fragment_discount_couponPrice);
            holder.name = (TextView) view.findViewById(R.id.fragment_discount_name);
            holder.couponInfo = (TextView) view.findViewById(R.id.fragment_discount_couponInfo);
            holder.time = (TextView) view.findViewById(R.id.fragment_discount_time);
            holder.couponState = (ImageView) view.findViewById(R.id.fragment_discount_couponState);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.price.setText(car.get(position).getCouponPrice()+"");
        holder.name.setText(car.get(position).getName());
        holder.couponInfo.setText(car.get(position).getCouponInfo());

        Log.d("我是优惠券开始时间" + position, car.get(position).getCouponStart());//----
        Log.d("我是优惠券结束时间" + position, car.get(position).getCouponEnd());//----

        String start = car.get(position).getCouponStart();
        String end = car.get(position).getCouponEnd();

        String i = start.substring(0, start.indexOf("T"));
        String j = end.substring(0, end.indexOf("T"));
        holder.time.setText(i + "到" + j + "有效");

        //    start = start.replaceAll("-|T|:", "");
        //   start = start.substring(0,13);
        end = end.replaceAll("-|T|:", "");
        end = end.substring(0, 14);

        long time_end = Long.parseLong(end);

        Log.d("转化为整数---我是过期时间" + position, time_end + "");

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        long time_now = Long.parseLong(date.replaceAll("-|:", "").trim());

        return view;
    }

    class ViewHolder {
        ImageView discount_bg;
        TextView price;
        TextView name;
        TextView couponInfo;
        TextView time;
        TextView couponUsed;
        ImageView couponState;
    }
}
