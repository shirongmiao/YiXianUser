package com.example.lfy.myapplication.FragmentHome.ChoosePoint;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.lfy.myapplication.Bean.Baidu_Bean;
import com.example.lfy.myapplication.FragmentClassify.FirstAdapter;
import com.example.lfy.myapplication.FragmentMine.partner.Partner;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.UserInfo;
import com.example.lfy.myapplication.Util.permission.PermissionAction;
import com.example.lfy.myapplication.Util.permission.PermissionHandler;
import com.example.lfy.myapplication.Util.permission.PermissionManager;
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
 * Created by lfy on 2016/7/16.
 */
public class MyLocation extends AppCompatActivity {

    ImageView return_black;
    TextView hand;
    Button location_open;
    // 定位相关
    LocationClient mLocClient;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    //定位屏幕中心位置
    MapView mMapView;
    BaiduMap mBaiduMap;
    //检索
    PoiNearbySearchOption poiNearbySearchOption;
    // UI相关
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位

    public static MyLocation instant = null;

    PoiSearch mPoiSearch;

    RecyclerView recyclerView;
    LocationAdapter locationAdapter;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        getPermission();
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.location);
        instant = this;
        requestLocButton = (Button) findViewById(R.id.button1);
        return_black = (ImageView) findViewById(R.id.return_black);
        recyclerView = (RecyclerView) findViewById(R.id.mylocation_recyclerview);
        location_open = (Button) findViewById(R.id.location_open);

        locationAdapter = new LocationAdapter();

        //recyclerview 适配器布局
        linearLayoutManager = new LinearLayoutManager(MyLocation.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        return_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hand = (TextView) findViewById(R.id.hand);
        hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyLocation.this, AllPoint.class);
                startActivity(intent);

            }
        });

        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        requestLocButton.setText("普通");
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocButton.setText("跟随");
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case COMPASS:
                        requestLocButton.setText("普通");
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case FOLLOWING:
//                        requestLocButton.setText("罗盘");
//                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
//                        mBaiduMap
//                                .setMyLocationConfigeration(new MyLocationConfiguration(
//                                        mCurrentMode, true, mCurrentMarker));
//                        break;
                        requestLocButton.setText("普通");
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                    default:
                        break;
                }
            }
        };

        requestLocButton.setOnClickListener(btnClickListener);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mPoiSearch = PoiSearch.newInstance();

        //地图定位监听
        mLocClient.registerLocationListener(myListener);
        //屏幕中心位置初始化划动监听
        mBaiduMap.setOnMapStatusChangeListener(listener);
        //mark点击事件鉴定
        mBaiduMap.setOnMarkerClickListener(markerClickListener);
        //周边检索
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        locationAdapter.setOnItemClickListen(new LocationAdapter.OnItemClickListen() {
            @Override
            public void SetOnItemClick(PoiInfo gridPhoto) {
//                视图转移到标注位置
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(gridPhoto.location);
//                mBaiduMap.setMapStatus(u);
                InTo_xUtil(gridPhoto.location);
            }
        });

    }

    public void getPermission() {
        PermissionHandler permissionHandler = PermissionManager.getInstance().getPermissionHandler(this);
        permissionHandler.requestPermission(new PermissionAction() {
            @Override
            public void onGrated(String permission) {
                //权限拥有
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {

                } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {

                } else if (Manifest.permission.WAKE_LOCK.equals(permission)) {

                }
            }

            @Override
            public void onDenied(String permission) {
                //权限拒绝
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {


                } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {

                } else if (Manifest.permission.WAKE_LOCK.equals(permission)) {

                }
            }
        }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WAKE_LOCK});
        //权限拒绝不显示对话框的回调
        permissionHandler.setOnRationaleListener(new PermissionHandler.OnRationaleListener() {
            @Override
            public void onRationale(String[] permission) {
            }
        });
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult != null) {
                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                    Toast.makeText(MyLocation.this, "未找到结果", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                    return;
                }

                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
                    List<PoiInfo> allAddress = poiResult.getAllPoi();
                    Log.d("我是检索监听", "有执行");

                    if (allAddress != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        if (locationAdapter.poiAddrInfos == null) {
                            locationAdapter.addDate(allAddress);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(locationAdapter);
                            location_open.setVisibility(View.GONE);
                        } else {
                            locationAdapter.addDate(allAddress);
                            locationAdapter.notifyDataSetChanged();
                        }
                    } else {
                        recyclerView.setVisibility(View.GONE);

                    }
//                    int totalPage = poiResult.getTotalPageNum();// 获取总分页数
//                    int numPage = poiResult.getTotalPoiNum();// 获取总分页数
//                    Toast.makeText(MyLocation.this,
//                            "总共查到" + poiResult.getTotalPoiNum() + "个兴趣点, 分为" + totalPage + "页",
//                            Toast.LENGTH_SHORT).show();
                }

            }

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {


        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Baidu_Bean info = (Baidu_Bean) marker.getExtraInfo().get("info");
            Intent intent = new Intent(MyLocation.this, Point_Particular.class);
            intent.putExtra("point_url", info.getID());
            startActivity(intent);
            return false;
        }
    };

    BaiduMap.OnMapStatusChangeListener listener = new BaiduMap.OnMapStatusChangeListener() {
        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         */
        public void onMapStatusChangeStart(MapStatus status) {
        }

        /**
         * 地图状态变化中
         * @param status 当前地图状态
         */
        public void onMapStatusChange(MapStatus status) {
        }

        /**
         * 地图状态改变结束
         * @param status 地图状态改变结束后的地图状态
         */
        public void onMapStatusChangeFinish(MapStatus status) {
            LatLng center = status.target;

            baidu_xUtil(center);
            //检索屏幕中心点周边的服务站
            //检索初始化
            recyclerView.setVisibility(View.VISIBLE);
            locationAdapter.addLatLng(center);
            poiNearbySearchOption = new PoiNearbySearchOption();
            poiNearbySearchOption.location(center);
            poiNearbySearchOption.radius(1000);
            poiNearbySearchOption.keyword("小区");
            poiNearbySearchOption.pageCapacity(50);
            mPoiSearch.searchNearby(poiNearbySearchOption);
//                            mPoiSearch.searchNearby(new PoiNearbySearchOption()
//                                    .keyword("小区")
//                                    .location(latLng)
//                                    .radius(1000));

        }
    };
    /**
     * 定位SDK监听函数
     */

    public MyLocationListenner myListener = new MyLocationListenner();

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng center = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(center).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                Log.d("我是纬度" + location.getLatitude(), "我是经度" + location.getLongitude());

                baidu_xUtil(center);
                //检索屏幕中心点周边的服务站
                //检索初始化
                recyclerView.setVisibility(View.VISIBLE);
                locationAdapter.addLatLng(center);
                poiNearbySearchOption = new PoiNearbySearchOption();
                poiNearbySearchOption.location(center);
                poiNearbySearchOption.radius(1000);
                poiNearbySearchOption.keyword("小区");
                poiNearbySearchOption.pageCapacity(50);
                mPoiSearch.searchNearby(poiNearbySearchOption);
//                            mPoiSearch.searchNearby(new PoiNearbySearchOption()
//                                    .keyword("小区")
//                                    .location(latLng)
//                                    .radius(1000));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        //关闭检索
        mPoiSearch.destroy();
    }

    private void baidu_xUtil(final LatLng latLng) {

        RequestParams params = new RequestParams(Variables.http_baidu);
        params.addBodyParameter("latitude", latLng.latitude + "");
        params.addBodyParameter("longitude", latLng.longitude + "");
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
                        JSONArray data = object.getJSONArray("Data");
                        List<Baidu_Bean> address = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            Baidu_Bean one = new Baidu_Bean();
                            JSONObject point = data.getJSONObject(i);
                            one.setID(point.getString("ID"));
                            one.setName(point.getString("Name"));
                            one.setDistrict(point.getString("District"));
                            one.setLongitude(point.getString("Longitude"));
                            one.setLatitude(point.getString("Latitude"));
                            one.setAddress(point.getString("Address"));
                            one.setTime(point.getString("Time"));
                            one.setPhone(point.getString("Phone"));
                            address.add(one);
                        }
                        biaoji(address);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void biaoji(List<Baidu_Bean> infos) {
        mBaiduMap.clear();
        LatLng point = null;
        OverlayOptions option = null;
        Marker marker = null;
        BitmapDescriptor bitmap = null;
        for (final Baidu_Bean info : infos) {
            //定义Maker坐标点
            double x = Double.parseDouble(info.getLatitude());
            double y = Double.parseDouble(info.getLongitude());
            point = new LatLng(x, y);
            //构建Marker图标
            LayoutInflater inflator = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflator.inflate(R.layout.bitmap, null);
            TextView text = (TextView) view.findViewById(R.id.text);
            text.setText(info.getName());
            bitmap = BitmapDescriptorFactory.fromBitmap(Variables.getBitmapFromView(view));
            //构建MarkerOption，用于在地图上添加Marker
            option = new MarkerOptions().position(point)
                    .icon(bitmap).zIndex(9).draggable(true);

            //在地图上添加Marker，并显示
            marker = (Marker) mBaiduMap.addOverlay(option);
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
            //视图转移到标注位置
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
//            mBaiduMap.setMapStatus(u);
//
//            InfoWindow mInfoWindow = null;
//            //生成一个TextView用户在地图中显示InfoWindow
//            TextView location = new TextView(getApplicationContext());
//            location.setBackgroundResource(R.drawable.location_tips);
//            location.setPadding(30, 20, 30, 50);
//            location.setText(info.getName());
//            location.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MyLocation.this, Point_Particular.class);
//                    intent.putExtra("point_url", info.getID());
//                    Log.d("点击item的名字", info.getName());
//                    intent.putExtra("point_name", info.getName());
//                    startActivity(intent);
//                    finish();
//                }
//            });
//            //将marker所在的经纬度的信息转化成屏幕上的坐标
//            final LatLng ll = marker.getPosition();
//            Point p = mBaiduMap.getProjection().toScreenLocation(ll);
//            p.y -= 47;
//            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
//            // 为弹出的InfoWindow添加点击事件
//            mInfoWindow = new InfoWindow(location, llInfo, -37);
//            // 显示InfoWindow
//            mBaiduMap.showInfoWindow(mInfoWindow);
        }

    }

//    @Override
//    public void onBackPressed() {
//        final UserInfo userInfo = new UserInfo(this);
//        if (userInfo.getStringInfo("PARTID").equals("")) {
//            Dialog dialog = new android.support.v7.app.AlertDialog.Builder(MyLocation.this)
//                    .setMessage("未选择自提点，默认选择-成蹊苑自提点！")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            userInfo.setUserInfo("PARTNAME", "成蹊苑百分鲜提货点");
//                            userInfo.setUserInfo("PARTID", "dbc1c95a-4f9e-4242-80ca-8f14f84606f1");
//                            Intent intent = new Intent(MyLocation.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    })
//                    .setNegativeButton("取消", null).create();
//            dialog.show();
//        } else {
//            finish();
//        }
//    }

    private void InTo_xUtil(final LatLng latLng) {

        RequestParams params = new RequestParams(Variables.http_baidu_one);
        params.addBodyParameter("latitude", latLng.latitude + "");
        params.addBodyParameter("longitude", latLng.longitude + "");
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
                        int Ret = object.getInt("Ret");
                        if (Ret == 1) {
                            JSONArray data = object.getJSONArray("Data");
                            JSONObject point = data.getJSONObject(0);
                            String pointid = point.getString("ID");

                            Intent intent = new Intent(MyLocation.this, Point_Particular.class);
                            intent.putExtra("point_url", pointid);
                            startActivity(intent);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            location_open.setVisibility(View.VISIBLE);
                            location_open.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MyLocation.this, Partner.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}