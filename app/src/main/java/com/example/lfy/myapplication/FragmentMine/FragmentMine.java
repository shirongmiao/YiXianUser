package com.example.lfy.myapplication.FragmentMine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.FragmentMine.address.ManageAddress;
import com.example.lfy.myapplication.FragmentMine.balance.Balance;
import com.example.lfy.myapplication.FragmentMine.collection.Collection;
import com.example.lfy.myapplication.FragmentMine.communication.Communication;
import com.example.lfy.myapplication.FragmentMine.discount.Coupon;
import com.example.lfy.myapplication.FragmentMine.feedback.Feedback;
import com.example.lfy.myapplication.FragmentMine.help.Help;
import com.example.lfy.myapplication.FragmentMine.partner.Partner;
import com.example.lfy.myapplication.FragmentMine.set_up.Set_up;
import com.example.lfy.myapplication.Group.GroupMainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.CircleImageView;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * Created by lfy on 2016/5/31.
 */
public class FragmentMine extends Fragment implements View.OnClickListener {

    private CircleImageView user_photo;
    private ImageView set_up;
    private TextView login_name;
    private LinearLayout discount, integral;

    private LinearLayout news, collection, partner, help, feedback;
    private LinearLayout balance;
    private TextView my_money;
    private TextView login_text;//登陆注册
    private LinearLayout group;
    private LinearLayout address;
    View view;

    LinearLayout.LayoutParams para;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentmine, container, false);
        initView();

        para = (LinearLayout.LayoutParams) login_text.getLayoutParams();
        return view;
    }

    private void initView() {
        my_money = (TextView) view.findViewById(R.id.my_money);
        user_photo = (CircleImageView) view.findViewById(R.id.user_photo);//头像
        discount = (LinearLayout) view.findViewById(R.id.fragment_discount);//优惠券
        integral = (LinearLayout) view.findViewById(R.id.fragment_integral);//积分商城
        collection = (LinearLayout) view.findViewById(R.id.fragment_collection);//收藏
        news = (LinearLayout) view.findViewById(R.id.fragment_news);//消息
        help = (LinearLayout) view.findViewById(R.id.fragment_help);//帮助中心
        set_up = (ImageView) view.findViewById(R.id.fragment_set_up);//设置
        login_name = (TextView) view.findViewById(R.id.login_name);
        partner = (LinearLayout) view.findViewById(R.id.fragment_partner);
        feedback = (LinearLayout) view.findViewById(R.id.fragment_feedback);
        balance = (LinearLayout) view.findViewById(R.id.fragment_balance);
        group = (LinearLayout) view.findViewById(R.id.fragment_group);
        address = (LinearLayout) view.findViewById(R.id.fragment_address);
        login_text = (TextView) view.findViewById(R.id.login_text);


        user_photo.setOnClickListener(this);
        login_text.setOnClickListener(this);
        discount.setClickable(false);
        discount.setOnClickListener(this);
        integral.setOnClickListener(this);
        collection.setOnClickListener(this);
        news.setOnClickListener(this);
        help.setOnClickListener(this);
        set_up.setOnClickListener(this);
        partner.setOnClickListener(this);
        feedback.setOnClickListener(this);
        balance.setOnClickListener(this);
        group.setOnClickListener(this);
        address.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Variables.my != null) {
            login_name.setVisibility(View.VISIBLE);
            login_name.setText(Variables.my.getUserName());
            login_text.setVisibility(View.GONE);
            my_money.setVisibility(View.VISIBLE);
            select_money();
            String liu = Variables.my.getImage();
            if (liu.equals("none")) {
                user_photo.setImageResource(R.mipmap.mine_default_photo);
            } else {
                try {
                    liu = liu.substring(liu.indexOf(",") + 1);
                    Bitmap bitmap = Variables.base64ToBitmap(liu);
                    user_photo.setImageBitmap(bitmap);
                } catch (Exception e) {
                }
            }
        } else {
            login_name.setVisibility(View.GONE);
            login_text.setVisibility(View.VISIBLE);
            para.width = Variables.PhoneWidth * 1 / 2;
            login_text.setLayoutParams(para);
            user_photo.setImageResource(R.mipmap.mine_default_photo);
            my_money.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.user_photo:
                if (Variables.my != null) {
                    intent = new Intent(getActivity(), Communication.class);
                    startActivity(intent);
                } else {
                    login();
                }
                break;
            case R.id.login_text:
                if (Variables.my != null) {
                    intent = new Intent(getActivity(), Communication.class);
                    startActivity(intent);
                } else {
                    login();
                }
                break;

            case R.id.fragment_discount://优惠券
                if (Variables.my != null) {
                    intent = new Intent(getActivity(), Coupon.class);
                    intent.putExtra("from", 0.00);
                    startActivity(intent);
                } else {
                    login();
                }
                break;
            case R.id.fragment_integral://积分
                Toast.makeText(getContext(), "积分商城，尚未开通", Toast.LENGTH_SHORT).show();
                break;

            case R.id.fragment_collection://收藏
                if (Variables.my != null) {
                    intent = new Intent(getActivity(), Collection.class);
                    startActivity(intent);
                } else {
                    login();
                }
                break;

            case R.id.fragment_partner://合作
                intent = new Intent(getActivity(), Partner.class);
                startActivity(intent);
                break;
            case R.id.fragment_news:
                Toast.makeText(getActivity(), "暂无消息", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_help:
                intent = new Intent(getActivity(), Help.class);
                startActivity(intent);
                break;
            case R.id.fragment_set_up:
                intent = new Intent(getActivity(), Set_up.class);
                startActivity(intent);
                break;
            case R.id.fragment_feedback:
                intent = new Intent(getActivity(), Feedback.class);
                startActivity(intent);
                break;
            case R.id.fragment_address:
                if (Variables.my != null) {
                    intent = new Intent(getActivity(), ManageAddress.class);
                    intent.putExtra("from", "FragmentMine");
                    startActivity(intent);
                } else {
                    login();
                }

                break;
            case R.id.fragment_group:

                Toast.makeText(getContext(), "团购正在升级中", Toast.LENGTH_SHORT).show();

//                Intent intent2 = new Intent(getActivity(), GroupMainActivity.class);
//                intent2.putExtra("Selected", 2);
//                startActivity(intent2);
                break;
            case R.id.fragment_balance:
                if (Variables.my != null) {
                    intent = new Intent(getActivity(), Balance.class);
                    startActivity(intent);
                } else {
                    login();
                }
                break;
        }
    }

    private void login() {
        Intent intent = new Intent(getActivity(), LoginBg.class);
        startActivity(intent);
    }

    //查询余额
    private void select_money() {

        RequestParams params = new RequestParams(Variables.http_Select_yue);
        params.addBodyParameter("customerId", Variables.my.getCustomerID());
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
                Toast.makeText(x.app(), "请检查网络", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String Ret = jsonObject.getString("Ret");
                        if (Ret.equals("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Data");
                            double price = jsonArray.getJSONObject(0).getDouble("TopUpPrice");
                            price = (Math.round(price * 10000) / 10000.00);
                            my_money.setText("￥" + price);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }


}