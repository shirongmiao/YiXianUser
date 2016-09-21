package com.example.lfy.myapplication.FragmentHome;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.FootPhoto;
import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.Bean.HomePhoto;
import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.FragmentHome.ChoosePoint.MyLocation;
import com.example.lfy.myapplication.FragmentHome.campaign.Campaign;
import com.example.lfy.myapplication.FragmentHome.coupon.GetCoupon;
import com.example.lfy.myapplication.FragmentHome.scan.MipcaActivityCapture;
import com.example.lfy.myapplication.FragmentHome.scan.WEB;
import com.example.lfy.myapplication.FragmentHome.search.Search;
import com.example.lfy.myapplication.FragmentHome.search.Search_edit;
import com.example.lfy.myapplication.FragmentMine.balance.Balance;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.UserInfo;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lfy on 2016/4/24.
 */
public class FragmentHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    HomeAdapter homeAdapter;
    private RecyclerView rv;
    TextView top_title;
    FrameLayout activity_title;
    LinearLayout into_point;
    SwipeRefreshLayout swipe_refresh;
    ImageView scan, search, top_title_down;
    GridLayoutManager gridLayoutManager;
    View view;
    int totalDy;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmenthome, container, false);
        homeAdapter = new HomeAdapter(getContext());

        FindView();
        SetView();
        setTopTitle();

        return view;
    }

    private void FindView() {
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.home_swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        top_title = (TextView) view.findViewById(R.id.top_title);
        activity_title = (FrameLayout) view.findViewById(R.id.activity_title);
        into_point = (LinearLayout) view.findViewById(R.id.into_point);
        scan = (ImageView) view.findViewById(R.id.scan);
        search = (ImageView) view.findViewById(R.id.search);
        top_title_down = (ImageView) view.findViewById(R.id.top_title_down);
    }

    private void SetView() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(homeAdapter);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MipcaActivityCapture.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Search_edit.class);
                startActivity(intent);
            }
        });
        into_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyLocation.class);
                startActivity(intent);
            }
        });

        homeAdapter.setOnItemClickListen(new HomeAdapter.OnItemClickListen() {
            @Override
            public void SetOnBannerClick(HomePhoto banner) {
                if (banner.getJumpType().equals("2")) {
                    Intent intent = new Intent(getActivity(), WEB.class);
                    intent.putExtra("title", banner.getName());
                    intent.putExtra("url", banner.getUrl());
                    getActivity().startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), Search.class);
                    intent.putExtra("content", banner.getName());
                    intent.putExtra("title", banner.getName());
                    intent.putExtra("point", Variables.point.getID());
                    getActivity().startActivity(intent);
                }
            }
            @Override
            public void SetOnGridClick(HomePhoto grid,int position) {
                if (position == 0) {
//                            Intent intent = new Intent(getActivity(), GroupMainActivity.class);
//                            startActivity(intent);
                    Toast.makeText(getActivity(), "团购正在升级中", Toast.LENGTH_SHORT).show();
                } else if (position == 1) {
                    if (Variables.my != null) {
                        new_user();
                    } else {
                        Intent intent = new Intent(getActivity(), LoginBg.class);
                        startActivity(intent);
                    }
                } else if (position == 2) {
                    if (Variables.my != null) {
                        Intent intent = new Intent(getActivity(), GetCoupon.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), LoginBg.class);
                        startActivity(intent);
                    }
                } else {
                    if (Variables.my != null) {
                        Intent intent = new Intent(getActivity(), Balance.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), LoginBg.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void SetOnItemClick(HomePhoto item) {
                if (item.getJumpType().equals("1")) {
                    Intent intent = new Intent(getActivity(), Search.class);
                    intent.putExtra("content",item.getName());
                    intent.putExtra("title",item.getName());
                    intent.putExtra("point", Variables.point.getID());
                    startActivity(intent);
                } else if (item.getJumpType().equals("2")) {
                    //跳转到web
                    Intent intent = new Intent(getActivity(), WEB.class);
                    intent.putExtra("title", item.getName());
                    intent.putExtra("url",item.getUrl());
                    startActivity(intent);
                } else {
                    //跳转到商品详情
                    Intent intent = new Intent(getActivity(), Goods_Particular.class);
                    intent.putExtra("productId", item.getProductid());
                    startActivity(intent);
                }
            }

            @Override
            public void SetOnFootClick(FootPhoto foot) {
                Intent intent = new Intent(getActivity(), Campaign.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("campaign",foot);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void SetOnLookClick() {
                MainActivity.classify.performClick();
            }
        });

        //滚动监听
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy += dy;
                Log.d("我是坐标", totalDy + "");
                if (totalDy < 510) {
                    activity_title.setAlpha((float) totalDy / 510);
                } else {
                    activity_title.setAlpha(1);
                }
                if (totalDy < 500) {
                    search.setImageResource(R.mipmap.all_search_black);
                    scan.setImageResource(R.mipmap.all_scan_black);
                    top_title_down.setImageResource(R.mipmap.home_down);
                    top_title.setTextColor(Color.BLACK);
                } else {
                    search.setImageResource(R.mipmap.all_search);
                    scan.setImageResource(R.mipmap.all_scan);
                    top_title_down.setImageResource(R.mipmap.home_down_white);
                    top_title.setTextColor(Color.WHITE);
                }

            }
        });

    }

    public void setTopTitle() {
        if (Variables.point == null) {
            point_xUtils();
        } else {
            TimeStamp();
            ALL_xUtils();
            Foot_xUtils();
            top_title.setText(Variables.point.getName());
        }
    }

    public static boolean flag = false;

    @Override
    public void onResume() {
        super.onResume();
        if (flag) {
            flag = false;
            setTopTitle();
        }
    }

    @Override
    public void onRefresh() {
        point_xUtils();
    }


    private void Foot_xUtils() {
        RequestParams params = new RequestParams(Variables.foot_photo);
        params.addBodyParameter("point", Variables.point.getID());
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
                    FootJSON(result);
                }
            }
        });

    }

    private void FootJSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                List<FootPhoto> activityPhoto = new ArrayList<FootPhoto>();
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    FootPhoto all = new FootPhoto();
                    String url = everyone.getString("ActivityImg");
                    all.setActivityImg("http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8"));
                    all.setType(everyone.getString("Type"));
                    all.setTypename(everyone.getString("Typename"));

                    List<GridPhoto> small = new ArrayList<>();
                    JSONArray product = everyone.getJSONArray("Products");
                    for (int a = 0; a < product.length(); a++) {
                        JSONObject gradOne = product.getJSONObject(a);
                        GridPhoto ever = new GridPhoto();

                        String imag = gradOne.getString("Image1");
                        ever.setImage("http://www.baifenxian.com/" + java.net.URLEncoder.encode(imag, "UTF-8"));
                        ever.setOrderCount(gradOne.getInt("OrderCount"));
                        ever.setPrice(gradOne.getDouble("Price"));//市场价格
                        ever.setProductID(gradOne.getString("ProductID"));//次日提
                        ever.setPromotionPrice(gradOne.getDouble("PromotionPrice"));
                        ever.setShelfState(gradOne.getString("ShelfState"));
                        ever.setStandard(gradOne.getString("Standard"));
                        ever.setType1(gradOne.getString("Type1"));
                        ever.setTypeName1(gradOne.getString("TypeName1"));
                        ever.setTypeName2(gradOne.getString("TypeName2"));
                        ever.setPromotionName(gradOne.getString("PromotionName"));
                        ever.setTitle(gradOne.getString("Title"));
                        ever.setPoint(gradOne.getString("point"));
                        ever.setPlace(gradOne.getString("Place"));
                        small.add(ever);
                    }
                    all.setProducts(small);
                    activityPhoto.add(all);
                }
                homeAdapter.addFoot(activityPhoto);
                homeAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void ALL_xUtils() {
        RequestParams params = new RequestParams(Variables.fragment_photo);
        params.addBodyParameter("point", Variables.point.getID());
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
                swipe_refresh.setRefreshing(false);
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
                    ALL_JSON(result);
                }
            }
        });
    }

    private void ALL_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {

                List<HomePhoto> viewpagerPhoto = new ArrayList<HomePhoto>();
                final List<HomePhoto> gridPhoto = new ArrayList<HomePhoto>();
                final List<HomePhoto> itemPhoto = new ArrayList<HomePhoto>();

                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    HomePhoto ever = new HomePhoto();
                    String url = everyone.getString("img");
                    String num = everyone.getString("num");

                    ever.setImg("http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8"));
                    ever.setId(everyone.getString("id"));
                    ever.setName(everyone.getString("name"));
                    ever.setUrl(everyone.getString("url"));//市场价格
                    ever.setNum(num);//次日提
                    ever.setPoint(everyone.getString("point"));
                    ever.setExplain(everyone.getString("explain"));
                    ever.setSort(everyone.getString("sort"));
                    ever.setJumpType(everyone.getString("jumpType"));
                    ever.setProductid(everyone.getString("productId"));

                    if (num.equals("1")) {
                        viewpagerPhoto.add(ever);
                    } else if (num.equals("2")) {
                        gridPhoto.add(ever);
                    } else if (num.equals("3")) {
                        itemPhoto.add(ever);
                    }
                }

                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (1 <= position && position <= gridPhoto.size()) {
                            return 1;
                        }
                        return 4;
                    }
                });

                homeAdapter.addBanner(viewpagerPhoto);
                homeAdapter.addGrid(gridPhoto);
                homeAdapter.addItem(itemPhoto);
                homeAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void point_xUtils() {
        UserInfo userInfo = new UserInfo(getActivity());
        String point = userInfo.getStringInfo("PARTID");
        RequestParams params = new RequestParams(Variables.http_getPoint);
        params.addBodyParameter("pointid", point);
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
                    Point_JSON(result);
                }
            }
        });

    }

    private void Point_JSON(String json) {

        HomePoint point = new HomePoint();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray data = object.getJSONArray("Data");
            JSONObject everyone = data.getJSONObject(0);

            point.setID(everyone.getString("id"));
            String url = everyone.getString("img");
            url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
            point.setImages(url);
            point.setName(everyone.getString("name"));
            point.setDistrict(everyone.getString("district"));
            point.setAddress(everyone.getString("address"));
            point.setCity(everyone.getString("city"));
            point.setPhone(everyone.getString("phone"));
            point.setTime(everyone.getString("time"));
            point.setState(everyone.getString("state"));
            point.setPrompt(everyone.getString("prompt"));
            point.setDeliveryPrice(everyone.getDouble("deliveryPrice"));
            point.setSendPrice(everyone.getDouble("sendPrice"));
            point.setFreePrice(everyone.getDouble("freePrice"));
            Variables.point = point;
            setTopTitle();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void new_user() {
        RequestParams params = new RequestParams(Variables.http_new_user);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        params.addBodyParameter("search", "新用户");
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
                    try {

                        JSONObject object = new JSONObject(result);
                        String Ret = object.getString("Ret");
                        Log.d("新用户专享", result);
                        if (Ret.equals("0")) {
                            Intent intent = new Intent(getActivity(), Search.class);
                            intent.putExtra("content", "新用户");
                            intent.putExtra("title", "新用户");
                            intent.putExtra("point", "1");
                            startActivity(intent);
                        } else {
                            new android.support.v7.app.AlertDialog.Builder(getActivity())
                                    .setMessage("您不是新用户！")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void TimeStamp() {
        RequestParams params = new RequestParams(Variables.http_time);
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
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        long time = jsonObject.getLong("Msg");
                        Log.d("服务器时间戳", time + "");
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String date = sdf.format(new Date(time * 1000));
                        String start = Variables.point.getTime().substring(0, Variables.point.getTime().indexOf("-"));
                        String end = Variables.point.getTime().substring(Variables.point.getTime().indexOf("-") + 1, Variables.point.getTime().length());

                        boolean flag1 = Variables.compare_date(start, date);
                        boolean flag2 = Variables.compare_date(date, end);
                        if (Variables.point.getState().equals("1")) {
                            if (flag1 && flag2) {
                                Variables.point.setState("1");
                            } else {
                                Variables.point.setState("0");
                            }
                        } else {
                            Variables.point.setState("0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
