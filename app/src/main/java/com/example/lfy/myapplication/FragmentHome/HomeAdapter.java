package com.example.lfy.myapplication.FragmentHome;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.example.lfy.myapplication.Bean.FootPhoto;
import com.example.lfy.myapplication.Bean.HomePhoto;
import com.example.lfy.myapplication.FragmentHome.campaign.HorizontalAdapter;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by lfy on 2016/6/3.
 */

/**
 * Created by Lijizhou on 2016/2/21.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<HomePhoto> viewpagerPhoto = null;
    List<HomePhoto> gridPhoto = null;
    List<HomePhoto> itemPhoto = null;
    List<FootPhoto> activityPhoto = null;

    int a;
    int b;
    int c;
    int d;


    public int lastPosition = -1;

    public void addBanner(List<HomePhoto> viewpagerPhoto) {
        this.viewpagerPhoto = viewpagerPhoto;
    }

    public void addGrid(List<HomePhoto> gridPhoto) {
        this.gridPhoto = gridPhoto;
    }

    public void addItem(List<HomePhoto> itemPhoto) {
        this.itemPhoto = itemPhoto;
    }

    public void addFoot(List<FootPhoto> activityPhoto) {
        this.activityPhoto = activityPhoto;
    }

    public OnItemClickListen Listen;

    public void setOnItemClickListen(OnItemClickListen Listen) {
        this.Listen = Listen;
    }

    interface OnItemClickListen {
        void SetOnBannerClick(HomePhoto banner);

        void SetOnGridClick(HomePhoto grid,int position);

        void SetOnItemClick(HomePhoto item);

        void SetOnFootClick(FootPhoto foot);

        void SetOnLookClick();
    }

    private LayoutInflater mLayoutInflater;
    private Context context;

    //建立枚举 5个item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2,
        ITEM3,
        ITEM4,
        ITEM5
    }

    public HomeAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//加载Item View的时候根据不同TYPE加载不同的布局
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new Item1ViewHolder(mLayoutInflater.inflate(R.layout.home_item_head, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mLayoutInflater.inflate(R.layout.home_item_gradview, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM3.ordinal()) {
            return new Item3ViewHolder(mLayoutInflater.inflate(R.layout.home_item, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM4.ordinal()) {
            return new Item4ViewHolder(mLayoutInflater.inflate(R.layout.home_item_foot, parent, false));
        } else {
            //底部查看更多等会做
            return new Item5ViewHolder(mLayoutInflater.inflate(R.layout.home_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        setAnimation(holder.itemView, position);

        if (holder instanceof Item1ViewHolder) {
            binding1(holder);
        } else if (holder instanceof Item2ViewHolder) {
            binding2(holder, position - a);
        } else if (holder instanceof Item3ViewHolder) {
            binding3(holder, position - a - b);
        } else if (holder instanceof Item4ViewHolder) {
            binding4(holder, position - a - b - c);
        } else if (holder instanceof Item5ViewHolder) {
            binding5(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemViewType(int position) {

        if (position < a) {
            return ITEM_TYPE.ITEM1.ordinal();
        } else if (a <= position && position < a + b) {
            return ITEM_TYPE.ITEM2.ordinal();
        } else if (a + b <= position && position < a + b + c) {
            return ITEM_TYPE.ITEM3.ordinal();
        } else if (a + b + c <= position && position < a + b + c + d) {
            return ITEM_TYPE.ITEM4.ordinal();
        } else {
            return ITEM_TYPE.ITEM5.ordinal();
        }
    }

    @Override
    public int getItemCount() {

        a = viewpagerPhoto == null ? 0 : 1;
        b = gridPhoto == null ? 0 : gridPhoto.size();
        c = itemPhoto == null ? 0 : itemPhoto.size();
        d = activityPhoto == null ? 0 : activityPhoto.size();

        return a + b + c + d + 1;
    }

    //item1 的ViewHolder
    public class Item1ViewHolder extends RecyclerView.ViewHolder {

        SliderLayout slider;
        PagerIndicator custom_indicator;
        TextView home_notification;


        public Item1ViewHolder(View itemView) {
            super(itemView);
            slider = (SliderLayout) itemView.findViewById(R.id.slider);
            custom_indicator = (PagerIndicator) itemView.findViewById(R.id.custom_indicator);
            home_notification = (TextView) itemView.findViewById(R.id.home_notification);
        }
    }

    //item2 的ViewHolder
    public class Item2ViewHolder extends RecyclerView.ViewHolder {
        public ImageView home_tab_image;
        public TextView home_tab_text;

        public Item2ViewHolder(View itemView) {
            super(itemView);
            home_tab_image = (ImageView) itemView.findViewById(R.id.home_tab_image);
            home_tab_text = (TextView) itemView.findViewById(R.id.home_tab_text);
        }
    }

    //item3 的ViewHolder
    public class Item3ViewHolder extends RecyclerView.ViewHolder {
        public ImageView Image;

        public Item3ViewHolder(View itemView) {
            super(itemView);
            Image = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

    //item4 的ViewHolder
    public class Item4ViewHolder extends RecyclerView.ViewHolder {
        public ImageView sales_image;
        public RecyclerView sales_grad;

        public Item4ViewHolder(View itemView) {
            super(itemView);
            sales_image = (ImageView) itemView.findViewById(R.id.sales_image);
            sales_grad = (RecyclerView) itemView.findViewById(R.id.sales_grad);
        }
    }

    //item5 的ViewHolder
    public class Item5ViewHolder extends RecyclerView.ViewHolder {
        public ImageView Image;

        public Item5ViewHolder(View itemView) {
            super(itemView);
            Image = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

    private boolean flag = true;

    private void binding1(RecyclerView.ViewHolder holder) {
//        ((Item1ViewHolder) holder).slider.removeAllSliders();
        if (flag) {
            initSlider(((Item1ViewHolder) holder).slider, viewpagerPhoto);
            ((Item1ViewHolder) holder).slider.setCustomIndicator(((Item1ViewHolder) holder).custom_indicator);
            ((Item1ViewHolder) holder).slider.getIndicatorVisibility();
            ((Item1ViewHolder) holder).slider.setPresetTransformer(SliderLayout.Transformer.Default);
            ((Item1ViewHolder) holder).slider.setDuration(3000);
            flag = false;
        }
    }

    private void binding2(RecyclerView.ViewHolder holder, final int position) {

        LinearLayout.LayoutParams para;
        para = (LinearLayout.LayoutParams) ((Item2ViewHolder) holder).home_tab_image.getLayoutParams();
        para.height = Variables.PhoneWidth / 12;
        para.width = Variables.PhoneWidth / 12;
        ((Item2ViewHolder) holder).home_tab_image.setLayoutParams(para);

        LongdingImage(((Item2ViewHolder) holder).home_tab_image, gridPhoto.get(position).getImg());

        ((Item2ViewHolder) holder).home_tab_text.setText(gridPhoto.get(position).getName());
        ((Item2ViewHolder) holder).home_tab_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Listen != null) {
                    Listen.SetOnGridClick(gridPhoto.get(position),position);
                }
            }
        });
    }

    private void binding3(RecyclerView.ViewHolder holder, final int position) {

        LinearLayout.LayoutParams para;
        para = (LinearLayout.LayoutParams) ((Item3ViewHolder) holder).Image.getLayoutParams();
        para.height = Variables.PhoneWidth * 2 / 5;
        para.width = Variables.PhoneWidth;
        ((Item3ViewHolder) holder).Image.setLayoutParams(para);
        LongdingImage(((Item3ViewHolder) holder).Image, itemPhoto.get(position).getImg());
        ((Item3ViewHolder) holder).Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Listen != null) {
                    Listen.SetOnItemClick(itemPhoto.get(position));
                }

            }
        });
    }

    private void binding4(RecyclerView.ViewHolder holder, final int position) {

        LinearLayout.LayoutParams para;
        para = (LinearLayout.LayoutParams) ((Item4ViewHolder) holder).sales_image.getLayoutParams();
        para.height = Variables.PhoneWidth * 5 / 6;
        para.width = Variables.PhoneWidth;
        ((Item4ViewHolder) holder).sales_image.setLayoutParams(para);

        LongdingImage(((Item4ViewHolder) holder).sales_image, activityPhoto.get(position).getActivityImg());

        HorizontalAdapter horizontal = new HorizontalAdapter();
        horizontal.addDate(activityPhoto.get(position).getProducts(), activityPhoto.get(position));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ((Item4ViewHolder) holder).sales_grad.setLayoutManager(linearLayoutManager);
        ((Item4ViewHolder) holder).sales_grad.setAdapter(horizontal);

        ((Item4ViewHolder) holder).sales_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Listen != null) {
                    Listen.SetOnFootClick(activityPhoto.get(position));
                }
            }
        });
    }

    private void binding5(RecyclerView.ViewHolder holder) {
        ((Item5ViewHolder) holder).Image.setImageResource(R.mipmap.login_bg);
        ((Item5ViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Listen != null) {
                    Listen.SetOnLookClick();
                }
            }
        });
    }

    protected void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_slide_bottom_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        } else {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_slide_bottom_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    //轮播图
    private void initSlider(SliderLayout sliderShow, final List<HomePhoto> images) {
        for (int i = 0; i < images.size(); i++) {
            DefaultSliderView textSliderView = new DefaultSliderView(context);
            textSliderView.image(images.get(i).getImg());
            final int finalI = i;
            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    if (Listen != null) {
                        Listen.SetOnBannerClick(images.get(finalI));
                    }
                }
            });
            sliderShow.addSlider(textSliderView);
        }
    }

    private void LongdingImage(ImageView view, String http) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setFailureDrawableId(R.mipmap.all_longding)
                //设置使用缓存
                .setUseMemCache(true)
                .setLoadingDrawableId(R.mipmap.all_longding)
                .build();
        x.image().bind(view, http, imageOptions);
    }


}
