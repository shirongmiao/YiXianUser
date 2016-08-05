package com.example.lfy.myapplication.FragmentHome.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lfy.myapplication.R;


/**
 * Created by lfy on 2016/1/4.
 */
public class Search_edit_adapter extends BaseAdapter {

    String[] search;
    Context context;

    public Search_edit_adapter(String[] search, Context context) {
        this.search = search;
        this.context = context;
    }

    @Override
    public int getCount() {
        return search.length;
    }

    @Override
    public Object getItem(int position) {
        return search[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.home_search_edit_item, null);

            holder.search_text = (TextView) view.findViewById(R.id.search_text);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.search_text.setText(search[position]);

        return view;
    }

    class ViewHolder {
        TextView search_text;
    }
}
