package com.example.lfy.myapplication.FragmentOrder.child_fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.Bean.OrderBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by lfy on 2016/2/23.
 */
public class ExpandListViewAdapter extends BaseExpandableListAdapter {


    public List<OrderBean> group_list;
    public List<List<CarDbBean>> child_list;
    Context context;

    public void addData(List<OrderBean> list, List<List<CarDbBean>> ProductStr) {
        this.child_list = ProductStr;
        this.group_list = list;
    }

    public void Clear() {
        this.child_list.clear();
        this.group_list.clear();
    }

    private OnItemClickListenr listenr;

    public void setOnItemClickListenr(OnItemClickListenr listenr) {
        this.listenr = listenr;
    }


    @Override
    public int getGroupCount() {
        return group_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_list.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child_list.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.order_item_head, null);
            groupHolder = new GroupHolder();

            groupHolder.order_number = (TextView) convertView.findViewById(R.id.order_number);
            groupHolder.member = (TextView) convertView.findViewById(R.id.member);
            groupHolder.order_type = (TextView) convertView.findViewById(R.id.order_type);
            groupHolder.timer = (TextView) convertView.findViewById(R.id.timer);
            groupHolder.timer_line = (LinearLayout) convertView.findViewById(R.id.timer_line);

            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.order_number.setText(group_list.get(groupPosition).getSwiftNumber());
        String next = group_list.get(groupPosition).getIsNextDay();
        if (next.equals("1")) {
            groupHolder.member.setVisibility(View.VISIBLE);
        } else {
            groupHolder.member.setVisibility(View.INVISIBLE);
        }
        String a = group_list.get(groupPosition).getOrderType();
        if (a.equals("0")) {
            groupHolder.order_type.setText("待付款");
            groupHolder.timer_line.setVisibility(View.GONE);
//            TimeStamp(groupHolder.timer, groupHolder.timer_line, group_list.get(groupPosition).getCreateTime());

        } else if (a.equals("1")) {
            groupHolder.order_type.setText("未提货");
            groupHolder.timer_line.setVisibility(View.GONE);
        } else {
            groupHolder.order_type.setText("已付款");
            groupHolder.timer_line.setVisibility(View.GONE);
        }
        return convertView;
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemHolder itemHolder = null;
        context = parent.getContext();
        final String ispay;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemHolder = new ItemHolder();
            convertView = inflator.inflate(R.layout.order_item_foot, null);
            itemHolder.pay = (TextView) convertView.findViewById(R.id.expand_pay);
            itemHolder.delete = (TextView) convertView.findViewById(R.id.expand_delete);
            itemHolder.create_time = (TextView) convertView.findViewById(R.id.expand_time);
            itemHolder.item_price = (TextView) convertView.findViewById(R.id.Fruit_Price);
            itemHolder.item_Num = (TextView) convertView.findViewById(R.id.Fruit_Num);
            itemHolder.order_price = (TextView) convertView.findViewById(R.id.pay_price);
            itemHolder.order_Num = (TextView) convertView.findViewById(R.id.all_goods);
            itemHolder.food = (LinearLayout) convertView.findViewById(R.id.food);

            itemHolder.item_name = (TextView) convertView.findViewById(R.id.Fruit_Name);
            itemHolder.item_Standard = (TextView) convertView.findViewById(R.id.Standard);
            itemHolder.item_Promotion = (TextView) convertView.findViewById(R.id.Promotion);
            itemHolder.item_Image = (ImageView) convertView.findViewById(R.id.Fruit_Image);

            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenr != null) {
                    listenr.ItemOnClick(child_list.get(groupPosition), group_list.get(groupPosition));
                }
            }
        });

        if (childPosition == child_list.get(groupPosition).size() - 1) {
            itemHolder.food.setVisibility(View.VISIBLE);
            String time = group_list.get(groupPosition).getCreateTime().replaceAll("T", " ");
            time = time.substring(0, 19);
            itemHolder.create_time.setText(time);
            itemHolder.order_price.setText(group_list.get(groupPosition).getPayedPrice());
            itemHolder.order_Num.setText(child_list.get(groupPosition).size() + "");

            if (group_list.get(groupPosition).getOrderType().equals("0")) {
                ispay = "pay";
                //再来一单目前停止使用
                itemHolder.pay.setVisibility(View.VISIBLE);
                itemHolder.pay.setText("付款");
                itemHolder.delete.setVisibility(View.VISIBLE);
            } else {
                itemHolder.pay.setText("再来一单");
                ispay = "once";
                itemHolder.delete.setVisibility(View.GONE);
                itemHolder.pay.setVisibility(View.GONE);
            }
            itemHolder.pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenr != null) {
                        listenr.OnClick(child_list.get(groupPosition), group_list.get(groupPosition), ispay, groupPosition);
                    }
                }
            });

            itemHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new android.support.v7.app.AlertDialog.Builder(context)
                            .setMessage("确定删除该订单？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    delete_order(group_list.get(groupPosition).getOrderNO(), group_list.get(groupPosition).getCouponID(), groupPosition);
                                }
                            })
                            .setNegativeButton("取消", null).create();
                    dialog.show();

                }
            });
        } else {
            itemHolder.food.setVisibility(View.GONE);
        }

//        if (child_list.get(groupPosition).get(childPosition).getTitle().contains("新用户") && !group_list.get(groupPosition).getOrderType().equals("0")) {
//            itemHolder.pay.setVisibility(View.GONE);
//        } else {
//            itemHolder.pay.setVisibility(View.VISIBLE);
//        }
        itemHolder.item_name.setText(child_list.get(groupPosition).get(childPosition).getTitle());
        itemHolder.item_Standard.setText(child_list.get(groupPosition).get(childPosition).getStandard());
        itemHolder.item_Promotion.setText(child_list.get(groupPosition).get(childPosition).getPromotionName() + "");
        itemHolder.item_Num.setText(child_list.get(groupPosition).get(childPosition).getProductCount() + "");
        x.image().bind(itemHolder.item_Image, child_list.get(groupPosition).get(childPosition).getImage());
        itemHolder.item_price.setText((Math.round(child_list.get(groupPosition).get(childPosition).getPrice() * 10000) / 10000.00) + "");
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupHolder {
        public TextView order_number;
        public TextView member;
        public TextView order_type;
        public TextView timer;
        public LinearLayout timer_line;
    }

    class ItemHolder {
        private ImageView item_Image;
        private TextView item_name;
        private TextView item_Standard;
        private TextView item_Promotion;
        private TextView item_price;
        private TextView item_Num;
        private TextView create_time;
        private TextView delete;
        private TextView pay;
        private TextView order_price;
        private TextView order_Num;
        private LinearLayout food;
    }

    private void delete_order(String orderno, String couponid, final int position) {


        RequestParams params = new RequestParams(Variables.http_delete_order);
        params.addBodyParameter("orderNo", orderno);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        params.addBodyParameter("CouponID", couponid);
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false;
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
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
                    String Ret = null;
                    try {
                        JSONObject object = new JSONObject(result);
                        Ret = object.getString("Ret");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (Ret.equals("1")) {
                        group_list.remove(position);
                        child_list.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }
        });

    }

    public interface OnItemClickListenr {
        void ItemOnClick(List<CarDbBean> my_goodses, OrderBean my_orders);

        void OnClick(List<CarDbBean> my_goodses, OrderBean my_orders, String ispay, int position);
    }
}
