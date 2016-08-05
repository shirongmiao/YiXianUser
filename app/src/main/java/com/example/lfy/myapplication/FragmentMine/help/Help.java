package com.example.lfy.myapplication.FragmentMine.help;

import android.content.Intent;
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


/**
 * Created by lfy on 2016/1/8.
 */
public class Help extends AppCompatActivity {
    WebView webView;
    ImageView all_return;
    TextView web_title;
    String url = null;
    String title = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_help);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        if (url != null) {
            url = intent.getStringExtra("url");
            title = "一鲜";
        } else {
            url = "http://www.baifenxian.com/user/appHelp.html";
            title = "帮助";
        }
        init();
    }

    private void init() {
        web_title = (TextView) findViewById(R.id.web_title);
        web_title.setText(title);
        all_return = (ImageView) findViewById(R.id.all_return);
        all_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.webView);
        //WebView加载web资源
        webView.loadUrl(url);
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
    }
}
