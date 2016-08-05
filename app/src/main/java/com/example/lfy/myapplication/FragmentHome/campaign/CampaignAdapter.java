package com.example.lfy.myapplication.FragmentHome.campaign;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lfy on 2016/6/11.
 */
public class CampaignAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    public List<GridPhoto> titles;
    private String url;

    //建立枚举 2个item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2
    }

    private OnClickListen listenr;

    public void setOnItemClickListener(OnClickListen li) {
        this.listenr = li;
    }

    public void addDate(List<GridPhoto> titles,String image_url) {
        this.titles = titles;
        url = image_url;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //加载Item View的时候根据不同TYPE加载不同的布局
        mLayoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.campaignitem, parent, false));
        } else {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.classify_second_item2, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        if (holder instanceof Item1ViewHolder) {
            FrameLayout.LayoutParams para;
            para = (FrameLayout.LayoutParams) ((Item1ViewHolder) holder).campaign.getLayoutParams();
            para.height = Variables.PhoneWidth * 5 / 6;
            para.width = Variables.PhoneWidth;
            x.image().bind(((Item1ViewHolder) holder).campaign,url);
        } else if (holder instanceof Item2ViewHolder) {

            x.image().bind(((Item2ViewHolder) holder).img, titles.get(position - 1).getImage());
            ((Item2ViewHolder) holder).Title.setText(titles.get(position - 1).getTitle());
            ((Item2ViewHolder) holder).PromotionName.setText(titles.get(position-1).getPromotionName());
            ((Item2ViewHolder) holder).second_weight.setText(titles.get(position-1).getStandard());
            ((Item2ViewHolder) holder).price.setText("￥" + titles.get(position - 1).getPrice());
            ((Item2ViewHolder) holder).VipPrice.setText("￥" + titles.get(position - 1).getPromotionPrice());


            if (Variables.point.getState().equals("1")){
                ((Item2ViewHolder) holder).add.setEnabled(true);
                ((Item2ViewHolder) holder).add.setBackgroundResource(R.drawable.all_add_car_select);

                ((Item2ViewHolder) holder).add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listenr != null) {
                            listenr.setOnClick(titles.get(position - 1));
                        }
                    }
                });
            }else {
                ((Item2ViewHolder) holder).add.setEnabled(false);
                ((Item2ViewHolder) holder).add.setBackgroundResource(R.drawable.round_hollow_down);
            }
        }
    }

    //设置ITEM类型，可以自由发挥，这里设置item position单数显示item1 偶数显示item2
    @Override
    public int getItemViewType(int position) {
    //Enum类提供了一个ordinal()方法，返回枚举类型的序数，这里ITEM_TYPE.ITEM1.ordinal()代表0， ITEM_TYPE.ITEM2.ordinal()代表1
        return position == 0 ? ITEM_TYPE.ITEM1.ordinal() : ITEM_TYPE.ITEM2.ordinal();
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size() + 1;
    }

    //item1 的ViewHolder
    public class Item1ViewHolder extends RecyclerView.ViewHolder {

        ImageView campaign;

        public Item1ViewHolder(final View itemView) {
            super(itemView);
            campaign = (ImageView) itemView.findViewById(R.id.campaign_image);
        }
    }

    //item2 的ViewHolder
    public class Item2ViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView price;
        TextView VipPrice;
        TextView PromotionName;
        TextView second_weight;
        ImageView img;
        Button add;

        public Item2ViewHolder(View itemView) {
            super(itemView);
            Title = (TextView) itemView.findViewById(R.id.second_content);
            VipPrice = (TextView) itemView.findViewById(R.id.second_vip);
            price = (TextView) itemView.findViewById(R.id.second_price);
            PromotionName = (TextView) itemView.findViewById(R.id.second_PromotionName);
            second_weight = (TextView) itemView.findViewById(R.id.second_weight);
            img = (ImageView) itemView.findViewById(R.id.second_image);
            add = (Button) itemView.findViewById(R.id.second_into);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenr != null) {
                        listenr.setOnItemClick(titles.get(getLayoutPosition()-1));
                    }
                }
            });
        }
    }

    interface OnClickListen {
        void setOnClick(GridPhoto goods);

        void setOnItemClick(GridPhoto gridPhoto);
    }
}
