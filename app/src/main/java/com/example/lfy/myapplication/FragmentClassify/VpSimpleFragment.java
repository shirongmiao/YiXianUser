package com.example.lfy.myapplication.FragmentClassify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.ToastUtils;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/6/6.
 */
public class VpSimpleFragment extends Fragment {
    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "1";
    SecondAdapter secondAdapter;
    RecyclerView classify_recyclerView;

    SecondViewpager secondMenu;

    String ShowType = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.classify_viewpager_item, container, false);
        classify_recyclerView = (RecyclerView) view.findViewById(R.id.classify_viewpager_item);


        Bundle arguments = getArguments();

        if (arguments != null) {
            mTitle = arguments.getString(BUNDLE_TITLE);
            ShowType = arguments.getString("ShowType");
        }
        if (ShowType.equals("1")) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
            classify_recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            classify_recyclerView.setLayoutManager(gridLayoutManager);
        }
        secondAdapter = new SecondAdapter();
        secondAdapter.setType(ShowType);
        secondMenu = new SecondViewpager();
        //获取商品并排序
        Goods_xUtils(mTitle, "1");

        return view;

    }


    public static VpSimpleFragment newInstance(String title, int showtype) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        bundle.putString("ShowType", showtype + "");
        VpSimpleFragment fragment = new VpSimpleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setSort(String sort, View v) {
        if (!sort.equals("4")) {
            Goods_xUtils(mTitle, sort);
        }
    }

    //商品排序
    private void Goods_xUtils(String type, String orderby) {
        RequestParams params = new RequestParams(Variables.second_product);
        params.addBodyParameter("point", Variables.point.getID());
        params.addBodyParameter("type", type);
        params.addBodyParameter("orderby", orderby);
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
                    Goods_JSON(result);
                }
            }
        });

    }

    private void Goods_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                List<GridPhoto> small = new ArrayList<>();
                for (int a = 0; a < data.length(); a++) {
                    JSONObject gradOne = data.getJSONObject(a);
                    GridPhoto ever = new GridPhoto();
                    String imag;
                    if (ShowType.equals("1")) {
                        imag = gradOne.getString("Image");
                    } else {
                        imag = gradOne.getString("Image1");
                    }
                    Log.d("我是图片地址", imag);
                    ever.setImage("http://www.baifenxian.com/" + java.net.URLEncoder.encode(imag, "UTF-8"));
                    ever.setOrderCount(gradOne.getInt("OrderCount"));
                    ever.setPrice(gradOne.getDouble("Price"));//市场价格
                    ever.setProductID(gradOne.getString("ProductID"));//
                    ever.setPromotionPrice(gradOne.getDouble("PromotionPrice"));
                    ever.setShelfState(gradOne.getString("ShelfState"));
                    ever.setPlace(gradOne.getString("Place"));
                    ever.setStandard(gradOne.getString("Standard"));
                    ever.setType1(gradOne.getString("Type1"));
                    ever.setTitle(gradOne.getString("Title"));
                    ever.setTypeName1(gradOne.getString("TypeName1"));
                    ever.setTypeName2(gradOne.getString("TypeName2"));
                    ever.setPromotionName(gradOne.getString("PromotionName"));
                    ever.setPoint(gradOne.getString("point"));
//                    ever.setInventory(gradOne.getInt("inventory"));
                    small.add(ever);
                }

                startAdapter(small);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    int totalDy;

    private void startAdapter(List<GridPhoto> small) {

        if (secondAdapter.classify == null) {
            secondAdapter.addDate(small);
            classify_recyclerView.setAdapter(secondAdapter);
        } else {
            secondAdapter.addDate(small);
            secondAdapter.notifyDataSetChanged();
        }
        secondAdapter.setOnClickListen(new SecondAdapter.OnClickListen() {
            @Override
            public void SetOnItemClick(String productId) {
                Intent intent = new Intent(getActivity(), Goods_Particular.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }

            @Override
            public void SerOnClick(String productId) {
                if (Variables.my != null) {
                    Insert_xUtils(productId);
                } else {
                    Intent intent = new Intent(getActivity(), LoginBg.class);
                    startActivity(intent);
                }

            }
        });
    }


    private void Insert_xUtils(String productId) {
        RequestParams params = new RequestParams(Variables.http_InsertCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("ProductID", productId);
        params.addBodyParameter("point", Variables.point.getID());
        params.addBodyParameter("type", "1");
        params.addBodyParameter("num", "1");

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
                    Variables.count = Variables.count + 1;
                    SecondViewpager.bv.setBadgeCount(Variables.count);
                    ToastUtils.showToast("加入成功", true);
                }else {
                    ToastUtils.showToast("加入失败", true);
                }
            }
        });

    }

}

