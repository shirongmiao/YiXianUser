package com.example.lfy.myapplication.SubmitOrder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2016/9/18 0018.
 */


public class PayTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    public Intent orderinfo;
    public List<GridPhoto> classify;
    private double FreedPrice;

    private OnClickListen onClickListen;


    public void SetOnClickListen(OnClickListen onClickListen) {
        this.onClickListen = onClickListen;
    }


    //建立枚举 2个Item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3
    }

    public void addDate(Intent orderinfo) {
        this.orderinfo = orderinfo;
        notifyDataSetChanged();
    }

    public void addMore(List<GridPhoto> classify) {
        this.classify = classify;
        notifyDataSetChanged();
    }

    public void FreedPrice(double FreedPrice) {
        this.FreedPrice = FreedPrice;
    }

//    public void Clean() {
//        titles.clear();
//        notifyDataSetChanged();
//    }


    //设置ITEM类型，可以自由发挥，这里设置Item position单数显示Item1 偶数显示Item2
    @Override
    public int getItemViewType(int position) {
//Enum类提供了一个ordinal()方法，返回枚举类型的序数，这里ITEM_TYPE.ITEM1.ordinal()代表0， ITEM_TYPE.ITEM2.ordinal()代表1
        if (position == 0) {
            return ITEM_TYPE.ITEM1.ordinal();
        } else if (position == 1) {
            return ITEM_TYPE.ITEM2.ordinal();
        } else {
            return ITEM_TYPE.ITEM3.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//加载Item View的时候根据不同TYPE加载不同的布局
        mLayoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.submit_paytype_item1, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.car_item3, parent, false));
        } else {
            return new Item3ViewHolder(mLayoutInflater.inflate(R.layout.submit_paytype_item2, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        if (holder instanceof Item1ViewHolder) {
            AddressBean list = (AddressBean) orderinfo.getSerializableExtra("address");
            String price = orderinfo.getStringExtra("money");
            String type = orderinfo.getStringExtra("payType");

            ((Item1ViewHolder) holder).payType.setText(type);
            ((Item1ViewHolder) holder).user.setText("收件人：" + list.getName() + "  " + list.getPhone());
            ((Item1ViewHolder) holder).address.setText(list.getDistrict() + list.getAddress());
            ((Item1ViewHolder) holder).money.setText("实付款：￥" + keep(price));
        } else if (holder instanceof Item3ViewHolder) {
            x.image().bind(((Item3ViewHolder) holder).imageView, classify.get(position - 2).getImage());
            ((Item3ViewHolder) holder).price.setText("￥" + classify.get(position - 2).getPrice() + "/");
            ((Item3ViewHolder) holder).vip.setText("会员价￥" + classify.get(position - 2).getPromotionPrice());
            ((Item3ViewHolder) holder).second_PromotionName.setText(classify.get(position - 2).getPromotionName());
            ((Item3ViewHolder) holder).content.setText(classify.get(position - 2).getTitle());
            ((Item3ViewHolder) holder).second_weight.setText(classify.get(position - 2).getStandard());

            if (Variables.point.getState().equals("1")) {
                ((Item3ViewHolder) holder).second_into.setEnabled(true);
                ((Item3ViewHolder) holder).second_into.setBackgroundResource(R.drawable.all_add_car_select);
            } else {
                ((Item3ViewHolder) holder).second_into.setEnabled(false);
                ((Item3ViewHolder) holder).second_into.setBackgroundResource(R.drawable.round_hollow_down);
            }
        }
    }

    private String keep(String price) {
        double a = Double.parseDouble(price);
        return String.format("%.2f", a);
    }


    @Override
    public int getItemCount() {
        return 1 + (classify == null ? 0 : classify.size() + 1);
    }

    //Item1 的ViewHolder
    public class Item1ViewHolder extends RecyclerView.ViewHolder {
        TextView payType;
        TextView user;
        TextView address;
        TextView money;
        Button look_order;

        public Item1ViewHolder(final View ItemView) {
            super(ItemView);
            payType = (TextView) ItemView.findViewById(R.id.payType);
            user = (TextView) ItemView.findViewById(R.id.user);
            address = (TextView) ItemView.findViewById(R.id.address);
            money = (TextView) ItemView.findViewById(R.id.money);
            look_order = (Button) ItemView.findViewById(R.id.look_order);
            look_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SetOnItemClick();
                    }
                }
            });
        }
    }

    //Item2 的ViewHolder
    public class Item2ViewHolder extends RecyclerView.ViewHolder {
        public Item2ViewHolder(View ItemView) {
            super(ItemView);
        }
    }

    //Item3 的ViewHolder
    public class Item3ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView content;
        TextView price;
        TextView vip;
        Button second_into;
        TextView second_PromotionName;
        TextView second_weight;

        public Item3ViewHolder(View ItemView) {
            super(ItemView);
            imageView = (ImageView) ItemView.findViewById(R.id.second_image);
            content = (TextView) ItemView.findViewById(R.id.second_content);
            price = (TextView) ItemView.findViewById(R.id.second_price);
            vip = (TextView) ItemView.findViewById(R.id.second_vip);
            second_into = (Button) ItemView.findViewById(R.id.second_into);
            second_PromotionName = (TextView) ItemView.findViewById(R.id.second_PromotionName);
            second_weight = (TextView) ItemView.findViewById(R.id.second_weight);

            ItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SetOnItemClick(classify.get(getLayoutPosition() - 2).getProductID());
                    }
                }
            });

            second_into.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SerOnClick(classify.get(getLayoutPosition() - 2).getProductID());
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
