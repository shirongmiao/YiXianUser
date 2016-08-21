package com.example.lfy.myapplication.FragmentHome;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.example.lfy.myapplication.Bean.FootPhoto;
import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.Bean.HomePhoto;
import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.FragmentHome.ChoosePoint.MyLocation;
import com.example.lfy.myapplication.FragmentHome.campaign.Campaign;
import com.example.lfy.myapplication.FragmentHome.campaign.HorizontalAdapter;
import com.example.lfy.myapplication.FragmentHome.coupon.GetCoupon;
import com.example.lfy.myapplication.FragmentHome.scan.MipcaActivityCapture;
import com.example.lfy.myapplication.FragmentHome.scan.WEB;
import com.example.lfy.myapplication.FragmentHome.search.Search;
import com.example.lfy.myapplication.FragmentHome.search.Search_edit;
import com.example.lfy.myapplication.FragmentMine.balance.Balance;
import com.example.lfy.myapplication.FragmentMine.help.Help;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.Group.GroupMainActivity;
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

    private RecyclerView rv;
    TextView top_title;
    FrameLayout activity_title;
    LinearLayout into_point;
    SwipeRefreshLayout swipe_refresh;
    ImageView scan, search, top_title_down;
    HeadFootAdapter<HeadViewHolder, FootViewHolder, ItemViewHolder, GridViewHolder> homeAdaptr;

    List<HomePhoto> viewpagerPhoto;
    List<HomePhoto> gridPhoto;
    List<HomePhoto> itemPhoto;
    List<FootPhoto> activityPhoto;

    int totalDy;
    GridLayoutManager gridLayoutManager;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmenthome, container, false);
        initView();

        activityPhoto = new ArrayList<FootPhoto>();
        viewpagerPhoto = new ArrayList<HomePhoto>();
        gridPhoto = new ArrayList<HomePhoto>();
        itemPhoto = new ArrayList<HomePhoto>();

        setTopTitle();

        return view;
    }

    private void initView() {
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.home_swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        top_title = (TextView) view.findViewById(R.id.top_title);
        activity_title = (FrameLayout) view.findViewById(R.id.activity_title);
        into_point = (LinearLayout) view.findViewById(R.id.into_point);
        scan = (ImageView) view.findViewById(R.id.scan);
        search = (ImageView) view.findViewById(R.id.search);
        top_title_down = (ImageView) view.findViewById(R.id.top_title_down);
        gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        rv.setLayoutManager(gridLayoutManager);

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

    }

    public void setTopTitle() {
        if (Variables.point == null) {
            point_xUtils();
            Log.d("我是point为空的时候", "============================");
        } else {
            Foot_xUtils();
            ALL_xUtils();
            TimeStamp();
            top_title.setText(Variables.point.getName());
            totalDy = 0;
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


    private void SetMyAdapter(final List<HomePhoto> viewpagerPhoto, final List<HomePhoto> gridPhoto, final List<HomePhoto> itemPhoto, final List<FootPhoto> activityPhoto) {
        homeAdaptr = new HeadFootAdapter<HeadViewHolder, FootViewHolder, ItemViewHolder, GridViewHolder>() {
            @Override
            public HeadViewHolder onCreateHeaderViewHolder(ViewGroup parent, int position) {
                View header = LayoutInflater.from(getActivity()).inflate(R.layout.home_item_head, parent, false);

                return new HeadViewHolder(header);
            }

            @Override
            public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_item, parent, false);
                return new ItemViewHolder(view);
            }

            @Override
            public FootViewHolder onCreateFooterViewHolder(ViewGroup parent, int position) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_item_foot, parent, false);
                return new FootViewHolder(view);
            }

            @Override
            public GridViewHolder onCreateGridViewHolder(ViewGroup parent, int position) {
                View gridview = LayoutInflater.from(getActivity()).inflate(R.layout.home_item_gradview, parent, false);
                return new GridViewHolder(gridview);
            }

            @Override
            public void onBindHeaderViewHolder(HeadViewHolder holder, int position) {
                holder.home_notification.setText(Variables.point.getPrompt());
                holder.slider.removeAllSliders();
                initSlider(holder.slider, viewpagerPhoto);
                holder.slider.setCustomIndicator(holder.custom_indicator);
                holder.slider.getIndicatorVisibility();
                holder.slider.setPresetTransformer(SliderLayout.Transformer.Default);
                holder.slider.setDuration(3000);
            }

            @Override
            public void onBindItemViewHolder(ItemViewHolder holder, final int position) {
                LinearLayout.LayoutParams para;
                para = (LinearLayout.LayoutParams) holder.Image.getLayoutParams();
                para.height = Variables.PhoneWidth * 2 / 5;
                para.width = Variables.PhoneWidth;
                holder.Image.setLayoutParams(para);

                LongdingImage(holder.Image, itemPhoto.get(position).getImg());

                holder.Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemPhoto.get(position).getJumpType().equals("1")) {
                            Intent intent = new Intent(getActivity(), Search.class);
                            intent.putExtra("content", itemPhoto.get(position).getName());
                            intent.putExtra("title", itemPhoto.get(position).getName());
                            intent.putExtra("point", Variables.point.getID());
                            startActivity(intent);
                        } else if (itemPhoto.get(position).getJumpType().equals("2")) {
                            //跳转到web
                            Intent intent = new Intent(getActivity(), WEB.class);
                            intent.putExtra("title", itemPhoto.get(position).getName());
                            intent.putExtra("url", itemPhoto.get(position).getUrl());
                            startActivity(intent);
                        } else {
                            //跳转到商品详情
                            Intent intent = new Intent(getActivity(), Goods_Particular.class);
                            intent.putExtra("productId", itemPhoto.get(position).getProductid());
                            startActivity(intent);
                        }
                    }
                });

            }

            @Override
            public void onBindFooterViewHolder(FootViewHolder holder, final int position) {

                LinearLayout.LayoutParams para;
                para = (LinearLayout.LayoutParams) holder.sales_image.getLayoutParams();
                para.height = Variables.PhoneWidth * 5 / 6;
                para.width = Variables.PhoneWidth;
                holder.sales_image.setLayoutParams(para);

                LongdingImage(holder.sales_image, activityPhoto.get(position).getActivityImg());

                HorizontalAdapter horizontal = new HorizontalAdapter();
                horizontal.addDate(activityPhoto.get(position).getProducts(), activityPhoto.get(position));

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder.sales_grad.setLayoutManager(linearLayoutManager);
                holder.sales_grad.setAdapter(horizontal);
                holder.sales_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), Campaign.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("campaign", activityPhoto.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                if (position == activityPhoto.size() - 1) {
                    holder.sales_more.setVisibility(View.VISIBLE);
                    holder.sales_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.classify.performClick();
                        }
                    });
                } else {
                    holder.sales_more.setVisibility(View.GONE);
                }
            }

            @Override
            public void onBindGridViewHolder(GridViewHolder holder, final int position) {

                LinearLayout.LayoutParams para;
                para = (LinearLayout.LayoutParams) holder.home_tab_image.getLayoutParams();
                para.height = Variables.PhoneWidth / 12;
                para.width = Variables.PhoneWidth / 12;
                holder.home_tab_image.setLayoutParams(para);

                LongdingImage(holder.home_tab_image, gridPhoto.get(position).getImg());

                holder.home_tab_text.setText(gridPhoto.get(position).getName());
                holder.home_tab_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 0) {
                            Intent intent = new Intent(getActivity(), GroupMainActivity.class);
                            startActivity(intent);
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
                });

            }

            @Override
            public int getHeadViewCount() {
                return 1;
            }

            @Override
            public int getGridViewCount() {
                return gridPhoto.size();
            }

            @Override
            public int getItemViewCount() {
                return itemPhoto.size();
            }

            @Override
            public int getFootViewCount() {
                return activityPhoto.size();
            }

        };
        homeAdaptr.notifyDataSetChanged();
        rv.setAdapter(homeAdaptr);

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


    class HeadViewHolder extends RecyclerView.ViewHolder {
        SliderLayout slider;
        PagerIndicator custom_indicator;
        TextView home_notification;

        public HeadViewHolder(View itemView) {
            super(itemView);
            slider = (SliderLayout) itemView.findViewById(R.id.slider);
            custom_indicator = (PagerIndicator) itemView.findViewById(R.id.custom_indicator);
            home_notification = (TextView) itemView.findViewById(R.id.home_notification);
        }
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        public ImageView home_tab_image;
        public TextView home_tab_text;

        public GridViewHolder(View itemView) {
            super(itemView);
            home_tab_image = (ImageView) itemView.findViewById(R.id.home_tab_image);
            home_tab_text = (TextView) itemView.findViewById(R.id.home_tab_text);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView Image;

        public ItemViewHolder(View itemView) {
            super(itemView);
            Image = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        //        public TextView sales_title;
        public ImageView sales_image;
        public RecyclerView sales_grad;
        public TextView sales_more;

        public FootViewHolder(View itemView) {
            super(itemView);
//            sales_title = (TextView) itemView.findViewById(R.id.sales_title);
            sales_image = (ImageView) itemView.findViewById(R.id.sales_image);
            sales_grad = (RecyclerView) itemView.findViewById(R.id.sales_grad);
            sales_more = (TextView) itemView.findViewById(R.id.sales_more);
        }
    }

    //轮播图
    private void initSlider(SliderLayout sliderShow, final List<HomePhoto> images) {

        for (int i = 0; i < images.size(); i++) {
            DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
            textSliderView.image(images.get(i).getImg());
            final int finalI = i;
            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    if (images.get(finalI).getJumpType().equals("2")) {
                        Intent intent = new Intent(getActivity(), WEB.class);
                        intent.putExtra("title", images.get(finalI).getName());
                        intent.putExtra("url", images.get(finalI).getUrl());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), Search.class);
                        intent.putExtra("content", images.get(finalI).getName());
                        intent.putExtra("title", images.get(finalI).getName());
                        intent.putExtra("point", Variables.point.getID());
                        startActivity(intent);
                    }
                }
            });
            sliderShow.addSlider(textSliderView);
        }
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
                } else {
                    success();
                }
            }
        });

    }

    private void FootJSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
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
                    if (activityPhoto != null) {
                        activityPhoto.clear();
                        activityPhoto.add(all);
                    } else {
                        activityPhoto.add(all);
                    }
                }
                success();
            } else {
                success();
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
                } else {
                    success();
                }
            }
        });
    }

    private void ALL_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {

                if (viewpagerPhoto != null) {
                    viewpagerPhoto.clear();
                }
                if (gridPhoto != null) {
                    gridPhoto.clear();
                }
                if (itemPhoto != null) {
                    itemPhoto.clear();
                }

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
                success();
            } else {
                success();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    int m;

    private void success() {
        m = m + 1;
        if (m == 2) {
            m = 0;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (1 <= position && position <= gridPhoto.size()) {
                        return 1;
                    }
                    return 4;
                }
            });
            swipe_refresh.setRefreshing(false);
            SetMyAdapter(viewpagerPhoto, gridPhoto, itemPhoto, activityPhoto);
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
            Foot_xUtils();
            ALL_xUtils();
            TimeStamp();
            top_title.setText(Variables.point.getName());

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
