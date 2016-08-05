package com.example.lfy.myapplication.FragmentHome.ChoosePoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.R;


/**
 * Created by lfy on 2015/12/17.
 */
public class Left_adapter extends BaseAdapter {

    String[] list;
    public Left_adapter(String[] user_goods) {
        this.list = user_goods;
    }

    @Override
    public int getCount() {
        return list.length;
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
            view = inflator.inflate(R.layout.all_point_item, null);
            holder.goods_text = (TextView) view.findViewById(R.id.fragment_two_left);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.goods_text.setText(list[position]);


        return view;
    }


    class ViewHolder {
        ImageView image;
        TextView goods_text;
    }

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     */
    public interface Callback {
        public void click(View v);
    }

}
