package com.example.lfy.myapplication.FragmentHome.ChoosePoint;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.pinnedheaderlistview.SectionedBaseAdapter;

import java.util.List;

/**
 * Created by lfy on 2015/12/14.
 */
public class RightHeadAdapter extends SectionedBaseAdapter {
    List<List<HomePoint>> car;
    String[] typename2;

    public void addDate(List<List<HomePoint>> car, String[] typename2) {
        this.car = car;
        this.typename2 = typename2;
    }

    public void clean() {
        car.clear();
    }

    @Override
    public Object getItem(int section, int position) {
        return car.get(section).size();
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return typename2.length;
    }

    @Override
    public int getCountForSection(int section) {
        return car.get(section).size();
    }

    @Override
    public View getItemView(final int section, final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.all_point_item, null);
            holder.fragment_two_left = (TextView) view.findViewById(R.id.fragment_two_left);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.fragment_two_left.setText(car.get(section).get(position).getName());

        return view;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.all_point_heade, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        ((TextView) layout.findViewById(R.id.textItem)).setText(typename2[section]);
        return layout;
    }

    class ViewHolder {
        TextView fragment_two_left;
    }

    //--------------------------------------
    private HolderClickListener mHolderClickListener;

    public void SetOnSetHolderClickListener(HolderClickListener holderClickListener) {
        this.mHolderClickListener = holderClickListener;
    }

    public interface HolderClickListener {
        public void onHolderClick(Drawable drawable, int[] start_location);
    }

}
