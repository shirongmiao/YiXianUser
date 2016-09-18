package com.example.lfy.myapplication.FragmentCar;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.GridPhoto;
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
    public List<GridPhoto> classify;
    private double FreedPrice;

    private OnClickListen onClickListen;



    public void SetOnClickListen(OnClickListen onClickListen) {
        this.onClickListen = onClickListen;
    }


    //建立枚举 2个item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3,
        ITEM4
    }

    public void addDate(List<CarDbBean> titles) {
        this.titles = titles;
        notifyDataSetChanged();
    }

    public void addMore(List<GridPhoto> classify) {
        this.classify = classify;
        notifyDataSetChanged();
    }

    public void FreedPrice(double FreedPrice) {
        this.FreedPrice = FreedPrice;
    }

    public void Clean() {
        titles.clear();
        notifyDataSetChanged();
    }


    //设置ITEM类型，可以自由发挥，这里设置item position单数显示item1 偶数显示item2
    @Override
    public int getItemViewType(int position) {
//Enum类提供了一个ordinal()方法，返回枚举类型的序数，这里ITEM_TYPE.ITEM1.ordinal()代表0， ITEM_TYPE.ITEM2.ordinal()代表1
        if (position <= titles.size() - 1) {
            return ITEM_TYPE.ITEM1.ordinal();
        } else if (position == titles.size()) {
            return ITEM_TYPE.ITEM2.ordinal();
        } else if (position == titles.size() + 1) {
            return ITEM_TYPE.ITEM3.ordinal();
        } else {
            return ITEM_TYPE.ITEM4.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//加载Item View的时候根据不同TYPE加载不同的布局
        mLayoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.car_item1, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.car_item2, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM3.ordinal()) {
            return new Item3ViewHolder(mLayoutInflater.inflate(R.layout.car_item3, parent, false));
        } else {
            return new Item4ViewHolder(mLayoutInflater.inflate(R.layout.car_item4, parent, false));
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
        } else if (holder instanceof Item4ViewHolder) {
            x.image().bind(((Item4ViewHolder) holder).imageView, classify.get(position - titles.size() - 2).getImage());
            ((Item4ViewHolder) holder).price.setText("￥" + classify.get(position - titles.size() - 2).getPrice() + "/");
            ((Item4ViewHolder) holder).vip.setText("会员价￥" + classify.get(position - titles.size() - 2).getPromotionPrice());
            ((Item4ViewHolder) holder).second_PromotionName.setText(classify.get(position - titles.size() - 2).getPromotionName());
            ((Item4ViewHolder) holder).content.setText(classify.get(position - titles.size() - 2).getTitle());
            ((Item4ViewHolder) holder).second_weight.setText(classify.get(position - titles.size() - 2).getStandard());

            if (Variables.point.getState().equals("1")) {
                ((Item4ViewHolder) holder).second_into.setEnabled(true);
                ((Item4ViewHolder) holder).second_into.setBackgroundResource(R.drawable.all_add_car_select);
            } else {
                ((Item4ViewHolder) holder).second_into.setEnabled(false);
                ((Item4ViewHolder) holder).second_into.setBackgroundResource(R.drawable.round_hollow_down);
            }
        }
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size() + 1 + (classify == null ? 0 : 1 + classify.size());
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

    //item3 的ViewHolder
    public class Item3ViewHolder extends RecyclerView.ViewHolder {
        public Item3ViewHolder(View itemView) {
            super(itemView);
        }
    }

    //item4 的viewHolder
    public class Item4ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView content;
        TextView price;
        TextView vip;
        Button second_into;
        TextView second_PromotionName;
        TextView second_weight;

        public Item4ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.second_image);
            content = (TextView) itemView.findViewById(R.id.second_content);
            price = (TextView) itemView.findViewById(R.id.second_price);
            vip = (TextView) itemView.findViewById(R.id.second_vip);
            second_into = (Button) itemView.findViewById(R.id.second_into);
            second_PromotionName = (TextView) itemView.findViewById(R.id.second_PromotionName);
            second_weight = (TextView) itemView.findViewById(R.id.second_weight);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SetOnItemClick(classify.get(getLayoutPosition()- titles.size() - 2).getProductID());
                    }
                }
            });

            second_into.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SerOnClick(classify.get(getLayoutPosition()- titles.size() - 2).getProductID());
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

        void SetOnItemClick(String productId);

        void SerOnClick(String productId);
    }
}
