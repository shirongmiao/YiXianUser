package com.example.lfy.myapplication.FragmentHome.scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.utils.Utils;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;


/**
 * Created by lfy on 2016/1/8.
 */
public class WEB extends AppCompatActivity {
    WebView webView;

    ImageView web_share;
    String title;
    String urlstr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.web);
        TextView textView = (TextView) findViewById(R.id.web_title);
        ImageView imageView = (ImageView) findViewById(R.id.web_left);
        web_share = (ImageView) findViewById(R.id.web_share);


        Intent intent = getIntent();
        urlstr = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        textView.setText(title);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init(urlstr);
    }


    private void init(String url) {
        webView = (WebView) findViewById(R.id.webView);
        //WebView加载web资源
        webView.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                if (url.contains("product")) {

                    String ii = url.substring(url.indexOf("product") + 8, url.length());
                    Intent intent = new Intent(WEB.this, Goods_Particular.class);
                    intent.putExtra("productId", ii);
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

        web_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IWXAPI api;
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = urlstr;

                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = title;
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.all_logo);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.PNG, 100, baos);
                msg.thumbData = baos.toByteArray();

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                api = WXAPIFactory.createWXAPI(getApplicationContext(), "wxf4d91338ae39b676", false);
                api.sendReq(req);

//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_SUBJECT, title);
//                intent.putExtra(Intent.EXTRA_TEXT, urlstr);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(Intent.createChooser(intent, getTitle()));
            }
        });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }
}
