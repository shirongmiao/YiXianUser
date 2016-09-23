package com.example.lfy.myapplication.FragmentClassify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.FragmentHome.search.Search_edit;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lfy on 2016/5/31.
 */
public class FragmentClassify extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    LinearLayout classify_search;
    SwipeRefreshLayout swipe_refresh;
    View view;
    FirstAdapter firstAdapter;
    LinearLayoutManager linearLayoutManager;

    List<GridPhoto> type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentclassify, container, false);

        firstAdapter = new FirstAdapter();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        initView();

        xUtils();//请求一级分类

        return view;
    }

    @Override
    public void onRefresh() {
        xUtils();
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.classify_recyclerView);
        classify_search = (LinearLayout) view.findViewById(R.id.classify_search);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);

        classify_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Search_edit.class);
                startActivity(intent);
            }
        });

        firstAdapter.setOnItemClickListen(new FirstAdapter.OnItemClickListen() {
            @Override
            public void SetOnItemClick(GridPhoto gridPhoto) {
                Intent intent = new Intent(getActivity(), SecondViewpager.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("one_classify", gridPhoto);
                bundle.putSerializable("all_classify", (Serializable) type);
//                intent.putExtra("show", ShowType + "");
//                intent.putExtra("type", Type1);
//                intent.putExtra("classify", Typename1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }


    private void xUtils() {
//        BaiduParams params = new BaiduParams();
//        params.wd = "xUtils";
//        // 默认缓存存活时间, 单位:毫秒.(如果服务没有返回有效的max-age或Expires)

        RequestParams params = new RequestParams(Variables.first_type);
        params.addBodyParameter("pointId", Variables.point.getID());
//        params.addBodyParameter("point", Variables.point.getID());
        Log.d("xUtils", params.toString());
        params.setCacheMaxAge(1000 * 60);
        Callback.Cancelable cancelable
                // 使用CacheCallback, xUtils将为该请求缓存数据.
                = x.http().get(params, new Callback.CacheCallback<String>() {

            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                // 得到缓存数据, 缓存过期后不会进入这个方法.
                // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
                //
                // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
                //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
                //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
                //
                // * 如果信任该缓存返回 true, 将不再请求网络;
                //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
                //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
                //
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
                swipe_refresh.setRefreshing(false);
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
                    swipe_refresh.setRefreshing(false);
                    // 成功获取数据
                    JSON(result);
                }
            }
        });

    }

    private void JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                type = new ArrayList<GridPhoto>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    GridPhoto ever = new GridPhoto();

                    ever.setType1(everyone.getString("Type1"));
                    ever.setTypeName1(everyone.getString("TypeName1"));
                    String img = everyone.getString("img1");
                    ever.setImage("http://www.baifenxian.com/" + java.net.URLEncoder.encode(img, "UTF-8"));
                    ever.setShowType(everyone.getInt("ShowType"));
                    type.add(ever);
                }
                firstAdapter.lastPosition = -1;
                if (firstAdapter.Type != null) {
                    firstAdapter.addDate(type);
                    firstAdapter.notifyDataSetChanged();
                } else {
                    firstAdapter.addDate(type);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(firstAdapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
