package com.example.lfy.myapplication.FragmentMine.partner;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;


/**
 * Created by lfy on 2016/1/8.
 */
public class Partner extends AppCompatActivity {
    WebView webView;
    ImageView all_return;
    TextView bout;
    ImageView one_fenxiang, all_share;
    public static Partner instance = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_parther);
        instance = this;
        init();
    }

    private void init() {
        one_fenxiang = (ImageView) findViewById(R.id.one_share);
        one_fenxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");  //纯文本
//                //图片分享
//                intent.setType("image/png");
////                File f = new File("http://img6.cache.netease.com/cnews/news2012/img/logo_news.png");
////                Uri u = Uri.fromFile(f);
//                intent.putExtra(Intent.EXTRA_STREAM, "http://img6.cache.netease.com/cnews/news2012/img/logo_news.png");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "http://www.baifenxian.com/user/partner.html");
                startActivity(Intent.createChooser(intent, getTitle()));

            }
        });
        bout = (TextView) findViewById(R.id.bout);
        bout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.my != null) {
                    Intent intent = new Intent(getApplication(), Partner_com.class);
                    startActivity(intent);
                } else {
                    login();
                }


            }
        });
        all_return = (ImageView) findViewById(R.id.all_return);
        all_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.webView);
        //WebView加载web资源
        webView.loadUrl("http://www.baifenxian.com/user/partner.html");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        all_share = (ImageView) findViewById(R.id.all_share);
        all_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//                intent.putExtra(Intent.EXTRA_TEXT, "http://www.baifenxian.com/user/partner.html");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(Intent.createChooser(intent, getTitle()));
                IWXAPI api;
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "http://www.baifenxian.com/user/partner.html";

                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = "申请开店";
                msg.description = "申请开店";
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.all_logo);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.PNG, 100, baos);
                msg.thumbData = baos.toByteArray();

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                api = WXAPIFactory.createWXAPI(getApplicationContext(), "wxf4d91338ae39b676", false);
                api.sendReq(req);
            }
        });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    private void login() {
        new android.support.v7.app.AlertDialog.Builder(Partner.this)
                .setMessage("登陆后才能申请开店，是否去登陆？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Partner.this, LoginBg.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", null).create().show();
    }
}
