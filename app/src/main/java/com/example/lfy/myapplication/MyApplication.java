package com.example.lfy.myapplication;

import android.app.Application;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.Util.UserInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wyouflf on 15/10/28.
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);//Xutils初始化
        point_xUtils();
    }

    private void point_xUtils() {
        UserInfo userInfo = new UserInfo(getApplicationContext());
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
                    JSON(result);
                }
            }
        });

    }

    private void JSON(String json) {

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
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
