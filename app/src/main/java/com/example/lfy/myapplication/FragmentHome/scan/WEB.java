package com.example.lfy.myapplication.FragmentHome.scan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;


/**
 * Created by lfy on 2016/1/8.
 */
public class WEB extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.web);
        TextView textView = (TextView) findViewById(R.id.web_title);
        ImageView imageView = (ImageView) findViewById(R.id.web_left);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        textView.setText(title);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init(url);
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

    }
}
