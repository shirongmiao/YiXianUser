package com.example.lfy.myapplication.FragmentMine.address;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/2/17.
 */
public class Manage_Adapter extends RecyclerView.Adapter<Manage_Adapter.ViewHolder> {

    private LayoutInflater inflater;
    public List<AddressBean> date = null;
    public Boolean isVISIBLE = false;
    Context context;

    private OnItemClickListenr listenr;

    public void setOnItemClickListenr(OnItemClickListenr listenr) {
        this.listenr = listenr;
    }

    /**
     * 直接添加  不需要 new 构造器
     * <p/>
     * * @param datas
     */
    public void addDatas(List<AddressBean> datas) {
        date = datas;
    }

    String ID = "none";

    public void setAddressId(String ID) {
        this.ID = ID;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.mine_address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.name.setText(date.get(position).getName());
        holder.phone.setText(date.get(position).getPhone());
        holder.address.setText(date.get(position).getDistrict() + " " + date.get(position).getAddress());

        if (date.get(position).getIsdefault().equals("1")) {
            holder.default_address.setChecked(true);
        } else {
            holder.default_address.setChecked(false);
        }

        if (isVISIBLE) {
            holder.setting.setVisibility(View.INVISIBLE);
        } else {
            holder.setting.setVisibility(View.VISIBLE);
        }

        holder.itemView.setTag(date.get(position));

    }


    @Override
    public int getItemCount() {
        return date.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone;
        TextView address;
        public Button setting;
        public Button address_delete;
        CheckBox default_address;

        public ViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.address_name);
            phone = (TextView) itemView.findViewById(R.id.address_phone);
            address = (TextView) itemView.findViewById(R.id.address_address);
            setting = (Button) itemView.findViewById(R.id.address_setting);
            address_delete = (Button) itemView.findViewById(R.id.address_delete);
            default_address = (CheckBox) itemView.findViewById(R.id.default_address);

            default_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Default_xUtil(default_address, date.get(getLayoutPosition()).getId());
                }
            });
            address_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ID.equals(date.get(getLayoutPosition()).getId())) {
                        Toast.makeText(context, "该地址正在使用", Toast.LENGTH_SHORT).show();
                    } else {
                        Dialog dialog = new android.support.v7.app.AlertDialog.Builder(context)
                                .setMessage("确定要删除该地址吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Delete_xUtils(date.get(getLayoutPosition()), getLayoutPosition());
                                    }
                                })
                                .setNegativeButton("取消", null).create();
                        dialog.show();
                    }
                }
            });

            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), UpdateAddress.class);
                    intent.putExtra("address", date.get(getLayoutPosition()));
                    context.startActivity(intent);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenr != null) {
                        listenr.onItemClick(getLayoutPosition(), date.get(getLayoutPosition()));
                    }
                }
            });

        }
    }

    public interface OnItemClickListenr {
        //        void onClick(int position, AddressBean address);
        void onItemClick(int position, AddressBean address);
    }


    private void Default_xUtil(final CheckBox default_address, String addressID) {
        RequestParams params = new RequestParams(Variables.http_Default_address);
        params.addBodyParameter("addressID", addressID);
        params.addBodyParameter("customerID", Variables.my.getCustomerID());
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }

            @Override
            public void onSuccess(String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    address_xUtil();
                } else {
                    default_address.setChecked(false);
                    notifyDataSetChanged();
                }
            }
        });

    }

    private void Delete_xUtils(AddressBean bean, final int position) {

        RequestParams params = new RequestParams(Variables.http_delete_address);
        params.addBodyParameter("addressID", bean.getId());
        params.setCacheMaxAge(10000 * 60);
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }

            @Override
            public void onSuccess(String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    date.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            }
        });

    }

    private void address_xUtil() {

        RequestParams params = new RequestParams(Variables.http_all_address);
        params.addBodyParameter("customerID", Variables.my.getCustomerID());
        x.http().post(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }

            @Override
            public void onSuccess(String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    JSON(result);
                    notifyDataSetChanged();
                }
            }
        });


    }

    private void JSON(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                List<AddressBean> all = new ArrayList<AddressBean>();
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    AddressBean address = new AddressBean();
                    address.setId(object.getString("id"));
                    address.setCustomerID(object.getString("customerID"));
                    address.setName(object.getString("name"));
                    address.setPointname(object.getString("pointname"));
                    address.setCity(object.getString("city"));
                    address.setPhone(object.getString("phone"));
                    address.setDistrict(object.getString("district"));
                    address.setAddress(object.getString("address"));
                    address.setSex(object.getString("sex"));
                    address.setIsdefault(object.getString("Isdefault"));
                    all.add(address);
                }
                date = all;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
