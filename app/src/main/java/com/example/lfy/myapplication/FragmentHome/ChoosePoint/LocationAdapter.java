package com.example.lfy.myapplication.FragmentHome.ChoosePoint;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by lfy on 2016/6/3.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHold> {

    List<PoiInfo> poiAddrInfos;
    LatLng latLng;

    public void addDate(List<PoiInfo> Type) {
        this.poiAddrInfos = Type;
    }

    public void addLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    private OnItemClickListen Listen;

    public void setOnItemClickListen(OnItemClickListen Listen) {
        this.Listen = Listen;
    }


    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        holder.itemView.setTag(position);
        double a = DistanceUtil.getDistance(latLng, poiAddrInfos.get(position).location);
        holder.location.setText("距离" + String.format("%.2f", a));
        holder.address.setText(poiAddrInfos.get(position).address);
        holder.name.setText(poiAddrInfos.get(position).name);

    }

    @Override
    public int getItemCount() {
        return poiAddrInfos.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {
        TextView address;
        TextView location;
        TextView name;

        public ViewHold(final View itemView) {
            super(itemView);
            address = (TextView) itemView.findViewById(R.id.location_address);
            location = (TextView) itemView.findViewById(R.id.location_size);
            name = (TextView) itemView.findViewById(R.id.location_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listen != null) {
                        Listen.SetOnItemClick(poiAddrInfos.get((Integer) itemView.getTag()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListen {
        void SetOnItemClick(PoiInfo gridPhoto);
    }
}
