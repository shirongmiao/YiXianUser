package com.example.lfy.myapplication.FragmentCar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.xutils.x;

import java.util.List;

/**
 * Created by lfy on 2016/6/11.
 */
public class CarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    public List<CarDbBean> titles;
    private double FreedPrice;

    private OnClickListen onClickListen;

    public void SetOnClickListen(OnClickListen onClickListen) {
        this.onClickListen = onClickListen;
    }


    //建立枚举 2个item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2
    }

    public void addDate(List<CarDbBean> titles) {
        this.titles = titles;
        notifyDataSetChanged();
    }

    public void FreedPrice(double FreedPrice) {
        this.FreedPrice = FreedPrice;
    }

    public void Clean() {
        titles.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//加载Item View的时候根据不同TYPE加载不同的布局
        mLayoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.car_item1, parent, false));
        } else {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.car_item2, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        if (holder instanceof Item1ViewHolder) {
            x.image().bind(((Item1ViewHolder) holder).car_item_image, titles.get(position).getImage());
            ((Item1ViewHolder) holder).car_item_content.setText(titles.get(position).getTitle());
            ((Item1ViewHolder) holder).car_item_price.setText("￥" + titles.get(position).getPrice());
            ((Item1ViewHolder) holder).car_item_vipPrice.setText("会员价：￥" + titles.get(position).getPromotionPrice());
            ((Item1ViewHolder) holder).car_item_count.setText(titles.get(position).getProductCount() + "");
            ((Item1ViewHolder) holder).car_item_PromotionName.setText(insertStringInParticularPosition(titles.get(position).getPromotionName(), "\n", 2));
        } else if (holder instanceof Item2ViewHolder) {
            String result = String.format("%.2f", FreedPrice);
            ((Item2ViewHolder) holder).mTextView.setText("差" + result + "免配送费");
            if (FreedPrice > 0) {
                ((Item2ViewHolder) holder).car_end.setVisibility(View.VISIBLE);
                ((Item2ViewHolder) holder).car_item2_SendPrice.setText("￥" + Variables.point.getSendPrice() + "元");
            } else {
                ((Item2ViewHolder) holder).car_end.setVisibility(View.GONE);
            }
        }
    }

    //设置ITEM类型，可以自由发挥，这里设置item position单数显示item1 偶数显示item2
    @Override
    public int getItemViewType(int position) {
//Enum类提供了一个ordinal()方法，返回枚举类型的序数，这里ITEM_TYPE.ITEM1.ordinal()代表0， ITEM_TYPE.ITEM2.ordinal()代表1
        return position != titles.size() ? ITEM_TYPE.ITEM1.ordinal() : ITEM_TYPE.ITEM2.ordinal();
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size() + 1;
    }

    //item1 的ViewHolder
    public class Item1ViewHolder extends RecyclerView.ViewHolder {

        ImageView car_item_image;
        TextView car_item_content;
        TextView car_item_price;
        TextView car_item_vipPrice;
        ImageView car_item_remove;
        TextView car_item_count;
        TextView car_item_PromotionName;
        ImageView car_item_add;

        public Item1ViewHolder(final View itemView) {
            super(itemView);
            car_item_image = (ImageView) itemView.findViewById(R.id.car_item_image);
            car_item_content = (TextView) itemView.findViewById(R.id.car_item_content);
            car_item_price = (TextView) itemView.findViewById(R.id.car_item_price);
            car_item_remove = (ImageView) itemView.findViewById(R.id.car_item_remove);
            car_item_count = (TextView) itemView.findViewById(R.id.car_item_count);
            car_item_vipPrice = (TextView) itemView.findViewById(R.id.car_item_VipPrice);
            car_item_PromotionName = (TextView) itemView.findViewById(R.id.car_item_PromotionName);
            car_item_add = (ImageView) itemView.findViewById(R.id.car_item_add);

            car_item_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListen != null) {
                        onClickListen.setOnClick((Integer) itemView.getTag(), titles.get((Integer) itemView.getTag()), "1");
                    }
                }
            });

            car_item_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (onClickListen != null) {
                            onClickListen.setOnClick((Integer) itemView.getTag(), titles.get((Integer) itemView.getTag()), "2");
                        }
                    } catch (IndexOutOfBoundsException e) {

                    }
                }
            });
        }
    }

    //item2 的ViewHolder
    public class Item2ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        TextView car_item2_SendPrice;
        LinearLayout car_end;

        public Item2ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_item2_text);
            car_item2_SendPrice = (TextView) itemView.findViewById(R.id.car_item2_SendPrice);
            car_end = (LinearLayout) itemView.findViewById(R.id.Linear_end);
            car_end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SetOnItemClick();
                    }
                }
            });
        }
    }

    interface OnClickListen {
        void setOnClick(int position, CarDbBean carDbBean, String type);
    }

    public String insertStringInParticularPosition(String src, String dec, int position) {
        StringBuffer stringBuffer = new StringBuffer(src);
        return stringBuffer.insert(position, dec).toString();

    }


    private OnItemClickListen Listen;

    public void setOnItemClickListen(OnItemClickListen Listen) {
        this.Listen = Listen;
    }

    public interface OnItemClickListen {
        void SetOnItemClick();
    }
}
