package com.example.lfy.myapplication.FragmentHome.ChoosePoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.pinnedheaderlistview.PinnedHeaderListView;
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
 * Created by lfy on 2015/12/22.
 */
public class AllPoint extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    ListView left_lv;
    TextView location;
    private PinnedHeaderListView right_lv;
    ImageView imageView1;
    List<List<HomePoint>> classify;

    SwipeRefreshLayout swipeRefreshLayout;
    public static AllPoint instant = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.all_point);
        instant = this;
        initView();
        xUtils();
    }


    @Override
    public void onRefresh() {

    }

    private void initView() {
        left_lv = (ListView) findViewById(R.id.adress_left_lv);
        right_lv = (PinnedHeaderListView) findViewById(R.id.adress_right_lv);
        imageView1 = (ImageView) findViewById(R.id.return_black);
        location = (TextView) findViewById(R.id.location);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllPoint.this, MyLocation.class);
                startActivity(intent);
            }
        });

        right_lv.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
                Intent intent = new Intent(getApplication(), Point_Particular.class);
                intent.putExtra("point_url", classify.get(section).get(position).getID());
                startActivity(intent);
            }

            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {

            }
        });

    }


    private void xUtils() {
        RequestParams params = new RequestParams(Variables.http_all_pointid);
        params.setCacheMaxAge(1000 * 60);
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
                    JSON(result);
                }
            }
        });

    }

    private void JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray data = object.getJSONArray("Data");
            List<HomePoint> distrits = new ArrayList<HomePoint>();
            for (int i = 0; i < data.length(); i++) {
                HomePoint point_bean = new HomePoint();
                JSONObject point = data.getJSONObject(i);
                point_bean.setID(point.getString("ID"));
                point_bean.setName(point.getString("Name"));
                point_bean.setDistrict(point.getString("District"));
                point_bean.setLongitude(point.getString("Longitude"));
                point_bean.setLatitude(point.getString("Latitude"));
                point_bean.setAddress(point.getString("Address"));
                point_bean.setTime(point.getString("Time"));
                point_bean.setPhone(point.getString("Phone"));
                point_bean.setFreePrice(point.getDouble("freePrice"));
                point_bean.setSendPrice(point.getDouble("sendPrice"));
                distrits.add(point_bean);

            }
            DeleteRepeat(distrits);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //删除数组中重复的数据
    public void DeleteRepeat(List<HomePoint> distrits) {

        String[] str = new String[distrits.size()];
        for (int a = 0; a < distrits.size(); a++) {
            str[a] = distrits.get(a).getDistrict();
        }
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < str.length; i++) {
            if (!list.contains(str[i])) {
                list.add(str[i]);
            }
        }

        String[] newDistrict = list.toArray(new String[1]); //返回一个包含所有对象的指定类型的数组
        Left_adapter left_adapter = new Left_adapter(newDistrict);
        left_lv.setAdapter(left_adapter);

        classify = new ArrayList<>();
        for (int a = 0; a < newDistrict.length; a++) {
            List<HomePoint> clas = new ArrayList<>();
            for (int j = 0; j < distrits.size(); j++) {
                if (newDistrict[a].equals(distrits.get(j).getDistrict())) {
                    clas.add(distrits.get(j));
                }
            }
            classify.add(clas);
        }

        RightHeadAdapter rightHeadAdapter = new RightHeadAdapter();
        rightHeadAdapter.addDate(classify, newDistrict);
        right_lv.setAdapter(rightHeadAdapter);

    }
}