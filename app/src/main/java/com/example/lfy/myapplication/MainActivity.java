package com.example.lfy.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;


import com.example.lfy.myapplication.Bean.MineBean;
import com.example.lfy.myapplication.FragmentCar.FragmentCar;
import com.example.lfy.myapplication.FragmentClassify.FragmentClassify;
import com.example.lfy.myapplication.FragmentHome.FragmentHome;
import com.example.lfy.myapplication.FragmentMine.FragmentMine;
import com.example.lfy.myapplication.FragmentOrder.FragmentOrder;
import com.example.lfy.myapplication.Util.BadgeView;
import com.example.lfy.myapplication.Util.permission.PermissionAction;
import com.example.lfy.myapplication.Util.permission.PermissionHandler;
import com.example.lfy.myapplication.Util.permission.PermissionManager;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentManager fm;
    private FragmentHome fg1;
    private FragmentClassify fg2;
    private FragmentOrder fg3;
    private FragmentMine fg4;
    private FragmentCar fg5;

    RadioButton home, mine;
    public static RadioButton classify;
    public static RadioButton order;
    public static RadioButton car;
    private View home_car_red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        initView();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        Variables.PhoneWidth = metric.widthPixels;     // 屏幕宽度（像素）
        Variables.PhoneHeight = metric.heightPixels;   // 屏幕高度（像素）
        home.performClick();
        setBadgeView();
        version_xUtils();
    }



    private void initView() {
        car = (RadioButton) findViewById(R.id.main_car);
        home = (RadioButton) findViewById(R.id.main_home);
        classify = (RadioButton) findViewById(R.id.main_classify);
        order = (RadioButton) findViewById(R.id.main_order);
        mine = (RadioButton) findViewById(R.id.main_mine);
        home_car_red = findViewById(R.id.home_car_red);

        home.setOnClickListener(this);
        classify.setOnClickListener(this);
        car.setOnClickListener(this);
        order.setOnClickListener(this);
        mine.setOnClickListener(this);
    }

    public static boolean flag = false;
    public static int num = 0;

    public static void jump(int n) {
        flag = true;
        num = n;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            order.performClick();
            flag = false;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    FragmentOrder.update(num);
                }
            }, 10);
        }
        bv.setBadgeCount(Variables.count);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_home:
                FragmentTransaction ft1 = fm.beginTransaction();
                hideFragments(ft1);
                if (fg1 == null) {
                    fg1 = new FragmentHome();// 如果fg1为空，则创建一个并添加到界面上
                    ft1.add(R.id.fragment, fg1);
                } else {
                    ft1.show(fg1);// 如果MessageFragment不为空，则直接将它显示出来
                }
                ft1.commitAllowingStateLoss();

                break;
            case R.id.main_classify:
                FragmentTransaction ft2 = fm.beginTransaction();
                hideFragments(ft2);
                if (fg2 == null) {
                    fg2 = new FragmentClassify();// 如果fg1为空，则创建一个并添加到界面上
                    ft2.add(R.id.fragment, fg2);
                } else {
                    ft2.show(fg2);// 如果MessageFragment不为空，则直接将它显示出来
                }
                ft2.commitAllowingStateLoss();
                break;
            case R.id.main_order:
                FragmentTransaction ft3 = fm.beginTransaction();
                hideFragments(ft3);
                if (fg3 == null) {
                    fg3 = new FragmentOrder();// 如果fg1为空，则创建一个并添加到界面上
                    ft3.add(R.id.fragment, fg3);
                } else {
                    ft3.show(fg3);// 如果MessageFragment不为空，则直接将它显示出来
                }
                ft3.commitAllowingStateLoss();
                break;
            case R.id.main_car:
                if (Variables.my != null) {
                    FragmentTransaction ft5 = fm.beginTransaction();
                    hideFragments(ft5);
                    if (fg5 == null) {
                        fg5 = new FragmentCar();// 如果fg1为空，则创建一个并添加到界面上
                        ft5.add(R.id.fragment, fg5);
                    } else {
                        ft5.show(fg5);// 如果MessageFragment不为空，则直接将它显示出来
                        fg5.setUpdate();
                    }
                    ft5.commitAllowingStateLoss();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginBg.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_mine:
                FragmentTransaction ft4 = fm.beginTransaction();
                hideFragments(ft4);
                if (fg4 == null) {
                    fg4 = new FragmentMine();// 如果fg1为空，则创建一个并添加到界面上
                    ft4.add(R.id.fragment, fg4);
                } else {
                    ft4.show(fg4);// 如果MessageFragment不为空，则直接将它显示出来
                }
                ft4.commitAllowingStateLoss();
                break;
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fg1 != null) {
            transaction.hide(fg1);
        }
        if (fg2 != null) {
            transaction.hide(fg2);
        }
        if (fg3 != null) {
            transaction.hide(fg3);
        }
        if (fg4 != null) {
            transaction.hide(fg4);
        }
        if (fg5 != null) {
            transaction.hide(fg5);
        }
    }


    @Override
    public void onBackPressed() {
        ExitApp();
    }

    private long exitTime = 0;

    public void ExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            android.os.Process.killProcess(android.os.Process
                    .myPid());
        }

    }


    // 需要setContentView之前调用
    public void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().getDecorView().setFitsSystemWindows(true);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);}
    }


    private void setBadgeView() {
        bv = new BadgeView(this);
        bv.setTargetView(home_car_red);
        bv.setTextColor(Color.WHITE);

        bv.setGravity(Gravity.TOP | Gravity.RIGHT);
        bv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));

        if (Variables.my != null) {
            getCar_xUtils();
        }

    }


    //获取购物车数据
    private void getCar_xUtils() {
        RequestParams params = new RequestParams(Variables.http_getCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("point", Variables.point.getID());
        params.setCacheMaxAge(1000 * 60);
        Log.d("获取购物车数据", params.toString());
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
                    Get_JSON(result);
                }
            }
        });

    }

    public static BadgeView bv;

    private void Get_JSON(String json) {
        int num = 0;
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    num = num + everyone.getInt("ProductCount");
                }
            }

            Variables.count = num;
            bv.setBadgeCount(num);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void version_xUtils() {
        RequestParams params = new RequestParams(Variables.http_version);
        params.addBodyParameter("version", getVersion(MainActivity.this));
        params.addBodyParameter("system", "android");
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
                        JSONObject jsonObject = new JSONObject(result);
                        Log.d("我是版本信息", result);
                        JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        JSONObject object = jsonArray.getJSONObject(0);
                        String type = object.getString("type");
                        String versionText = object.getString("versionText");
                        String updateVersion = object.getString("updateVersion");
                        if (type.equals("1")) {
                            Dialog dialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                                    .setTitle(updateVersion)
                                    .setMessage(versionText)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            DownLoad(getApplicationContext());
                                        }
                                    })
                                    .setNegativeButton("取消", null).create();
                            dialog.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private String getVersion(Context context)//获取版本号
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return context.getString(R.string.version_unknown);
        }
    }

    static NotificationManager mNotifyManager;
    static NotificationCompat.Builder mBuilder;

    /**
     * 下载工具类
     */
    private void DownLoad(final Context context) {
        final String url = "http://www.baifenxian.com/android/yixian.apk";
        final String path = Environment.getExternalStorageDirectory() + "/yixian.apk";

        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("版本更新")
                .setContentText("正在下载...")
                .setContentInfo("0%")
                .setSmallIcon(R.mipmap.ic_launcher);


        RequestParams params = new RequestParams(url);
        //设置断点续传
        params.setAutoResume(true);
        params.setSaveFilePath(path);

        x.http().get(params, new Callback.ProgressCallback<File>() {


            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                Toast.makeText(x.app(), "开始下载", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                BigDecimal b = new BigDecimal((float) current / (float) total);
                float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                mBuilder.setProgress(100, (int) (f1 * 100), false);
                mBuilder.setContentInfo((int) (f1 * 100) + "%");
                mNotifyManager.notify(1, mBuilder.build());
            }

            @Override
            public void onSuccess(File result) {
                mBuilder.setContentText("正在下载...")
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(1, mBuilder.build());
                mNotifyManager.cancel(1);
                Toast.makeText(x.app(), "下载成功", Toast.LENGTH_LONG).show();
                installApp(context, path);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static void installApp(Context context, String filePath) {
        File _file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(_file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "yixian.apk"))
                , "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
