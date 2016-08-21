package com.example.lfy.myapplication.Group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GroupOrder;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.CircleImageView;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class GroupDetail extends AppCompatActivity {
    ImageView group_detail_image, group_detail_return, group_Detail_photo, group_detail_groupingimg;
    TextView group_detail_share, group_detail_createtime, group_detail_title, group_detail_content, group_detail_price, group_detail_personnum, group_detail_phone;
    GroupOrder groupOrder;
    RecyclerView group_detail_photorv;
    int IsSingleBuy;
    MyAdapter adapter;
    String from;
    List<GroupOrder> groupOrders;
    //团长头像
    Bitmap photo;

    public GroupDetail() {
        groupOrders = new ArrayList<>();
        adapter = new MyAdapter(groupOrders);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.activity_group_detail);
        Intent intent = getIntent();
        groupOrder = (GroupOrder) intent.getSerializableExtra("groupOrder");
        Log.d("groupOrder", groupOrder.toString());
        from = intent.getStringExtra("from");
        if (from.equals("GroupSubmitOrder")) {
            IsSingleBuy = intent.getIntExtra("IsSingleBuy", 0);
        }
        initView();
        setData();
        OrderDetail_xUtils();

    }

    //将手机号码中的4位数用星号代替
    private void setPhoneNumber(TextView mPhoneNumber, String pNumber) {
        if (!TextUtils.isEmpty(pNumber) && pNumber.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            mPhoneNumber.setText(sb.toString());
        }
    }

    private void initView() {
        group_detail_image = (ImageView) findViewById(R.id.group_detail_image);
        group_detail_title = (TextView) findViewById(R.id.group_detail_title);
        group_detail_content = (TextView) findViewById(R.id.group_detail_content);
        group_detail_price = (TextView) findViewById(R.id.group_detail_price);
        group_detail_return = (ImageView) findViewById(R.id.group_detail_return);
        group_detail_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        group_detail_photorv = (RecyclerView) findViewById(R.id.group_detail_photorv);
        group_detail_personnum = (TextView) findViewById(R.id.group_detail_personnum);
        group_detail_phone = (TextView) findViewById(R.id.group_detail_phone);
        group_detail_createtime = (TextView) findViewById(R.id.group_detail_createtime);
        //团长头像
        group_Detail_photo = (ImageView) findViewById(R.id.group_Detail_photo);
        //拼团标志图片
        group_detail_groupingimg = (ImageView) findViewById(R.id.group_detail_groupingimg);
        //分享按钮
        group_detail_share = (TextView) findViewById(R.id.group_detail_share);
    }

    //设置本地信息
    private void setData() {
        Log.d("from", from);
        if (from.equals("GroupMine1") || from.equals("GroupMine2") || from.equals("GroupMine3")) {
            group_detail_groupingimg.setVisibility(View.INVISIBLE);
            group_detail_share.setVisibility(View.INVISIBLE);
        }
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setFailureDrawableId(R.mipmap.all_longding)
                //设置使用缓存
                .setUseMemCache(true)
                .setLoadingDrawableId(R.mipmap.all_longding)
                .build();
        x.image().bind(group_detail_image, groupOrder.getImg(), imageOptions);
        group_detail_title.setText(groupOrder.getTitle());
        group_detail_content.setText(groupOrder.getPlace() + "\t" + groupOrder.getStandard());
        if (from.equals("GroupSubmitOrder")) {
            if (IsSingleBuy == 1) {
                group_detail_price.setText(groupOrder.getSinglePrice() + "");
            } else {
                group_detail_price.setText(groupOrder.getTuanPrice() + "");
            }
        } else {
            if (groupOrder.getIsSingleBuy() == 1) {
                group_detail_price.setText(groupOrder.getSinglePrice() + "");
            } else {
                group_detail_price.setText(groupOrder.getTuanPrice() + "");
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        group_detail_photorv.setLayoutManager(linearLayoutManager);
        group_detail_photorv.setAdapter(adapter);
        group_detail_personnum.setText(groupOrder.getPersonNum() + "人团");
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<GroupOrder> groupOrders;

        MyAdapter(List<GroupOrder> groupOrders) {
            this.groupOrders = groupOrders;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(GroupDetail.this).inflate(R.layout.group_detail_item, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String liu = groupOrders.get(position).getImage();
            if (liu.equals("none")) {
                holder.photo.setImageResource(R.mipmap.mine_default_photo);
            } else {
                try {
                    liu = liu.substring(liu.indexOf(",") + 1);
                    Log.d("图片" + groupOrders.get(position).getPhoneNameber() + "长度", liu.length() + "");
                    Bitmap bitmap = Variables.base64ToBitmap(liu);
                    holder.photo.setImageBitmap(bitmap);
                } catch (Exception e) {
                }
            }

        }

        @Override
        public int getItemCount() {
            return groupOrders.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView photo;

            public MyViewHolder(View itemView) {
                super(itemView);
                photo = (CircleImageView) itemView.findViewById(R.id.group_Detail_item_photo);
            }
        }
    }

    private void OrderDetail_xUtils() {
        RequestParams params = new RequestParams(Variables.GetTuanOrderInfo);
        params.addBodyParameter("orderNo", groupOrder.getOrderNO());
        Log.d("通过团ID查团购详情", params.toString());
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

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
                    OrderDetail_JSON(result);
                } else {
//                    success();
                }
            }

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    private void OrderDetail_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    JSONArray orderinfos = everyone.getJSONArray("orderinfo");

                    for (int j = 0; j < orderinfos.length(); j++) {
                        JSONObject orderinfo = orderinfos.getJSONObject(j);
                        GroupOrder good = new GroupOrder();
                        good.setOrderID(orderinfo.getString("OrderID"));
                        good.setOrderNO(orderinfo.getString("OrderNO"));
                        good.setOrderPrice(orderinfo.getString("OrderPrice"));
                        good.setOrderType(orderinfo.getString("OrderType"));
                        good.setPoint(orderinfo.getString("point"));
                        good.setProductID(orderinfo.getString("ProductID"));
                        good.setCreateTime(orderinfo.getString("CreateTime"));
                        good.setPayTime(orderinfo.getString("PayTime"));
                        good.setCustomerStr(orderinfo.getString("CustomerStr"));
                        good.setCustomerNum(orderinfo.getInt("CustomerNum"));
                        good.setTuanNumber(orderinfo.getString("TuanNumber"));
                        good.setCost(orderinfo.getDouble("Cost"));
                        good.setCommand(orderinfo.getString("command"));
                        good.setIsSingleBuy(orderinfo.getInt("IsSingleBuy"));
                        good.setCustomerID(orderinfo.getString("CustomerID"));
                        good.setCustomerName(orderinfo.getString("CustomerName"));
                        good.setPhoneNameber(orderinfo.getString("PhoneNameber"));
                        good.setImage(orderinfo.getString("image"));
                        good.setTimestamp(orderinfo.getLong("timestamp"));
                        good.setPayed(orderinfo.getString("Payed"));
                        good.setTuanid(orderinfo.getString("tuanid"));
                        good.setTitle(orderinfo.getString("title"));
                        String url = orderinfo.getString("img");
                        url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
                        good.setImg(url);
                        good.setDetail(orderinfo.getString("detail"));
                        good.setTuanPrice(orderinfo.getDouble("tuanPrice"));
                        good.setMarketPrice(orderinfo.getString("marketPrice"));
                        good.setSinglePrice(orderinfo.getDouble("singlePrice"));
                        good.setStandard(orderinfo.getString("Standard"));
                        good.setPlace(orderinfo.getString("Place"));
                        good.setPersonNum(orderinfo.getInt("personNum"));
                        good.setTuanCount(orderinfo.getString("tuanCount"));
                        good.setShelfState(orderinfo.getString("ShelfState"));
                        good.setAddress(orderinfo.getString("address"));
                        good.setCustomerOrderNo(orderinfo.getString("CustomerOrderNo"));
                        groupOrders.add(good);

                    }
                    //设置团长信息
                    setPhoneNumber(group_detail_phone, groupOrders.get(0).getPhoneNameber());
                    group_detail_createtime.setText(groupOrders.get(0).getCreateTime().replace("T", " ") + "开团");
                    String liu = groupOrders.get(0).getImage();
                    if (liu.equals("none")) {
                        group_Detail_photo.setImageResource(R.mipmap.mine_default_photo);
                    } else {
                        try {
                            liu = liu.substring(liu.indexOf(",") + 1);
                            Log.d("团长图片", liu.length() + "");
                            Bitmap bitmap = Variables.base64ToBitmap(liu);
                            group_Detail_photo.setImageBitmap(bitmap);
                        } catch (Exception e) {
                        }
                    }
                }
            } else {

            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
