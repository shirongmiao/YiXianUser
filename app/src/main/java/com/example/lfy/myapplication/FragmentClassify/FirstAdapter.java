package com.example.lfy.myapplication.FragmentClassify;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by lfy on 2016/6/3.
 */
public class FirstAdapter extends RecyclerView.Adapter<FirstAdapter.ViewHold> {
    List<GridPhoto> Type;
    public int lastPosition = -1;

    public void addDate(List<GridPhoto> Type) {
        this.Type = Type;
    }

    private OnItemClickListen Listen;

    public void setOnItemClickListen(OnItemClickListen Listen) {
        this.Listen = Listen;
    }


    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classify_item, parent, false);
        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        holder.itemView.setTag(position);
        setAnimation(holder.itemView, position);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setFailureDrawableId(R.mipmap.all_longding)
                .setLoadingDrawableId(R.mipmap.all_longding)
                .build();

        x.image().bind(holder.cardView, Type.get(position).getImage(), imageOptions);


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

    @Override
    public void onViewDetachedFromWindow(ViewHold holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


    @Override
    public int getItemCount() {
        return Type.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {
        ImageView cardView;

        public ViewHold(final View itemView) {
            super(itemView);
            cardView = (ImageView) itemView.findViewById(R.id.card_bg);

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
        void SetOnItemClick(GridPhoto gridPhoto);
    }
}
