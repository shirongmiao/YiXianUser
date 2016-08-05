package com.example.lfy.myapplication.FragmentMine.balance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.BalanceBean;
import com.example.lfy.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/4/12.
 */
public class Balance_adapter extends RecyclerView.Adapter<Balance_adapter.ViewHold> {

    List<BalanceBean> mList = new ArrayList<>();

    public void addData(List<BalanceBean> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    int first = 0;

    private OnItemClick itemClick;

    public void SetOnItemClick(OnItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_adapter, parent, false);

        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        holder.itemView.setTag(position);
        String detail = mList.get(position).detail;
        holder.payPrice.setText("￥" + mList.get(position).payPrice + "元");
        if (!detail.equals("null")) {
            holder.things.setText(mList.get(position).detail);
        } else {
            holder.things.setText("可得" + mList.get(position).getPrice + "元");
        }

        if (position == first) {
            holder.balance_select.setVisibility(View.VISIBLE);
        } else {
            holder.balance_select.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {
        TextView things;
        TextView payPrice;
        ImageView balance_select;

        public ViewHold(final View itemView) {
            super(itemView);

            things = (TextView) itemView.findViewById(R.id.things);
            payPrice = (TextView) itemView.findViewById(R.id.payPrice);
            balance_select = (ImageView) itemView.findViewById(R.id.balance_select);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClick != null) {
                        itemClick.onItemClick(mList.get((Integer) itemView.getTag()));
                        first = (Integer) itemView.getTag();
                        notifyDataSetChanged();
                    }
                }
            });

        }
    }

    interface OnItemClick {
        void onItemClick(BalanceBean balance_bean);
    }

}
