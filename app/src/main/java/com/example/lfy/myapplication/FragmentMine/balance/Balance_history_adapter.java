package com.example.lfy.myapplication.FragmentMine.balance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.BalanceBean;
import com.example.lfy.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/4/12.
 */
public class Balance_history_adapter extends RecyclerView.Adapter<Balance_history_adapter.ViewHold> {

    List<BalanceBean> mList = new ArrayList<>();

    public void addData(List<BalanceBean> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    private OnItemClick itemClick;

    public void SetOnItemClick(OnItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mine_balance_history_adapter, parent, false);

        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        holder.have_money.setText(mList.get(position).getGetPrice());
        String a = mList.get(position).getPayed();
        if (a.equals("1")){
            holder.is_pay.setText("交易成功");
        }else {
            holder.is_pay.setText("交易失败");
        }
        holder.order_id.setText((position+1)+"");
        String time = mList.get(position).getTopUpTime().replaceAll("T"," ");
        holder.order_time.setText(time.substring(0,time.lastIndexOf(".")));
        holder.order_price.setText(mList.get(position).getPrice);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {
        TextView order_id;
        TextView order_time;
        TextView is_pay;
        TextView order_price;
        TextView have_money;

        public ViewHold(final View itemView) {
            super(itemView);

            order_id = (TextView) itemView.findViewById(R.id.order_id);
            order_time=(TextView) itemView.findViewById(R.id.order_time);
             is_pay=(TextView) itemView.findViewById(R.id.is_pay);
             order_price=(TextView) itemView.findViewById(R.id.order_price);
             have_money=(TextView) itemView.findViewById(R.id.have_money);

        }
    }

    interface OnItemClick {
        void onItemClick(int position, BalanceBean balance_bean);
    }

}
