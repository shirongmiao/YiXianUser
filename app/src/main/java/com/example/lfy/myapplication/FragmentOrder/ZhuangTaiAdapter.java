package com.example.lfy.myapplication.FragmentOrder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.ZhuangTaiBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import java.util.List;

/**
 * Created by lfy on 2016/6/3.
 */
public class ZhuangTaiAdapter extends RecyclerView.Adapter<ZhuangTaiAdapter.ViewHold> {


    List<ZhuangTaiBean> Type = null;

    public void addDate(List<ZhuangTaiBean> Type) {
        this.Type = Type;
    }

    private OnItemClickListen Listen;

    public void setOnItemClickListen(OnItemClickListen Listen) {
        this.Listen = Listen;
    }


    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_particular2_adapter, parent, false);
        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        holder.itemView.setTag(position);
//        ImageOptions imageOptions = new ImageOptions.Builder()
//                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
//                .setImageScaleType(ImageView.ScaleType.FIT_XY)
//                .setFailureDrawableId(R.mipmap.all_longding)
//                .setLoadingDrawableId(R.mipmap.all_longding)
//                .build();
//
//        x.image().bind(holder.zhuangtai_round, Type.get(position).getImage(), imageOptions);
        holder.zhuangtai_round.setImageResource(R.mipmap.all_call);
        if (position == 0) {
            holder.zhuangtai_top.setVisibility(View.INVISIBLE);
        } else {
            holder.zhuangtai_top.setVisibility(View.VISIBLE);
        }

        if (position + 1 == Type.size()) {
            holder.zhuangtai_bottom.setVisibility(View.INVISIBLE);

            LinearLayout.LayoutParams para;
            para = (LinearLayout.LayoutParams) holder.zhuangtai_round.getLayoutParams();
            para.height = Variables.PhoneWidth / 20;
            para.width = Variables.PhoneWidth / 20;
            holder.zhuangtai_round.setLayoutParams(para);
            holder.zhuangtai_round.setImageResource(R.mipmap.all_logo);
        } else {
            holder.zhuangtai_bottom.setVisibility(View.VISIBLE);
        }
        if (position == 0) {
            holder.zhangtai_item_title.setText("订单已提交");
        } else if (Type.get(position).getType() == 1) {
            holder.zhangtai_item_title.setText("支付成功");
            holder.zhangtai_item_content.setText("请耐心等待商家确认");
        } else if (Type.get(position).getType() == 2) {
            holder.zhangtai_item_title.setText("社区服务商已确认");
            holder.zhangtai_item_content.setText("将于次日完成配送，请保持电话通畅");
        } else if (Type.get(position).getType() == 3) {
            holder.zhangtai_item_title.setText("商品已送达");
            holder.zhangtai_item_content.setText("等待用户确认收货");
        } else if (Type.get(position).getType() == 4) {
            holder.zhangtai_item_title.setText("订单完成");
            holder.zhangtai_item_content.setText("待评价");
        } else if (Type.get(position).getType() == 5) {
            holder.zhangtai_item_title.setText("订单完成");
            holder.zhangtai_item_content.setText("已评价");
        }
        String str = Type.get(position).getTime();
        str = str.substring(str.indexOf("T") + 1, str.indexOf("."));
        holder.zhuantai_time.setText(str);

    }

    @Override
    public int getItemCount() {
        return Type == null ? 0 : Type.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {

        ImageView zhuangtai_round;
        View zhuangtai_top;
        View zhuangtai_bottom;
        TextView zhangtai_item_title;
        TextView zhangtai_item_content;
        TextView zhuantai_time;


        public ViewHold(final View itemView) {
            super(itemView);
            zhuangtai_round = (ImageView) itemView.findViewById(R.id.zhuangtai_round);
            zhuangtai_top = itemView.findViewById(R.id.zhuangtai_top);
            zhuangtai_bottom = itemView.findViewById(R.id.zhuangtai_bottom);
            zhangtai_item_title = (TextView) itemView.findViewById(R.id.zhangtai_item_title);
            zhangtai_item_content = (TextView) itemView.findViewById(R.id.zhangtai_item_content);
            zhuantai_time = (TextView) itemView.findViewById(R.id.zhuantai_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
//                        Listen.SetOnItemClick(Type.get((Integer) itemView.getTag()).getType1(), Type.get((Integer) itemView.getTag()).getTypeName1(), Type.get((Integer) itemView.getTag()).getShowType());
                        Listen.SetOnItemClick(Type.get((Integer) itemView.getTag()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListen {
        void SetOnItemClick(ZhuangTaiBean gridPhoto);
    }
}
