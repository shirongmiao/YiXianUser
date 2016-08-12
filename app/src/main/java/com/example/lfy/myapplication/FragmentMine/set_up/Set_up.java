package com.example.lfy.myapplication.FragmentMine.set_up;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.UserInfo;
import com.example.lfy.myapplication.Variables;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;


/**
 * Created by lfy on 2015/12/28.
 */
public class Set_up extends AppCompatActivity {

    private static final String USER_NAME = "user_name";
    private static final String PASSWORD = "password";
    TextView dis_login;
    TextView set_up_clean;
    LinearLayout about_our;
    ImageView return_all;
    UserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_setup);
        userInfo = new UserInfo(getApplication());
        about_our = (LinearLayout) findViewById(R.id.about_our);
        about_our.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Set_up.this, About_our.class);
                startActivity(intent);
            }
        });

        return_all = (ImageView) findViewById(R.id.return_all);
        return_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        set_up_clean = (TextView) findViewById(R.id.set_up_clean);
        dis_login = (TextView) findViewById(R.id.dis_login);
        //注销
        dis_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.my!=null){
                    Logout_xUtils();
                }
            }
        });

        set_up_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new android.support.v7.app.AlertDialog.Builder(Set_up.this)
                        .setMessage("是否清除用户数据？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                userInfo.setUserInfo(USER_NAME, "");
                                userInfo.setUserInfo(PASSWORD, "");
                                cleanApplicationData(getApplicationContext());
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.show();

            }
        });


    }


    /** * 清除本应用所有的数据 * * @param context * @param filepath */
    public  void cleanApplicationData(Context context) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanFiles(context);
        finish();
    }


    public  void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
    public  void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }
    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    public  void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }


    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
    //登录
    private void Logout_xUtils() {

        RequestParams params = new RequestParams(Variables.http_Logout);
        params.addBodyParameter("customerID", Variables.my.getCustomerID());
        params.addBodyParameter("devicetoken", Variables.DEVICE_ID);
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
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String Msg = jsonObject.getString("Msg");
                        if (Msg.equals("注销成功")){
                            Variables.my = null;
                            userInfo.setUserInfo(PASSWORD, "");
                            Variables.count = 0;
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Set_up.this, "您的网络奔溃了！", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

}
