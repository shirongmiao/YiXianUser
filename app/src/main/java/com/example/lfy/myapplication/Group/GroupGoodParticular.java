package com.example.lfy.myapplication.Group;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GroupOrder;
import com.example.lfy.myapplication.FragmentHome.scan.WEB;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.xutils.image.ImageOptions;
import org.xutils.x;

public class GroupGoodParticular extends AppCompatActivity {
    ImageView image, group_good_more_image, group_good_return_but;
    TextView group_good_personnum, group_gp_singlebuy, group_gp_num, group_gp_buy, group_goods_price, group_goods_originalprice, group_goods_title, group_good_Standard, group_goods_content;
    WebView wbv;
    TextView group_good_playexplain;
    RelativeLayout group_gp_buy_btn, group_gp_singlebuy_btn, group_gp_buy_join;
    LinearLayout group_good_help, group_good_forfind, group_good_fornear;
    GroupOrder groupGoods;
    //从哪个页面跳转而来
    String from = "";
    public static GroupGoodParticular groupGoodParticular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        groupGoodParticular=this;
        setContentView(R.layout.activity_group_good_particular);
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        groupGoods = (GroupOrder) intent.getSerializableExtra("groupgood");
        initView();
    }

    public void initView() {
        //根据跳转过来的页面，设置下方的按钮
        group_good_forfind = (LinearLayout) findViewById(R.id.group_good_forfind);
        group_good_fornear = (LinearLayout) findViewById(R.id.group_good_fornear);
        if (from.equals("GroupNear")) {
            group_good_fornear.setVisibility(View.VISIBLE);
            group_good_forfind.setVisibility(View.GONE);
            group_good_personnum = (TextView) findViewById(R.id.group_good_personnum);
            group_good_personnum.setText("差" + (groupGoods.getPersonNum() - groupGoods.getCustomerNum()) + "人成团");
            group_gp_buy_join = (RelativeLayout) findViewById(R.id.group_gp_buy_join);
            group_gp_buy_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Variables.my == null) {
                        Intent intent = new Intent(GroupGoodParticular.this, LoginBg.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(GroupGoodParticular.this, GroupSubmitOrder.class);
                        intent.putExtra("groupgood", groupGoods);
                        //IsSingleBuy 2:用户参团 1：单独购  0:开团
                        intent.putExtra("IsSingleBuy", 2);
                        startActivity(intent);
                    }
                }
            });
        } else {
            group_good_fornear.setVisibility(View.GONE);
            group_good_forfind.setVisibility(View.VISIBLE);
            group_gp_buy = (TextView) findViewById(R.id.group_gp_buy);
            group_gp_buy.setText(groupGoods.getTuanPrice() + "");
            group_gp_buy_btn = (RelativeLayout) findViewById(R.id.group_gp_buy_btn);
            group_gp_buy_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Variables.my == null) {
                        Intent intent = new Intent(GroupGoodParticular.this, LoginBg.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(GroupGoodParticular.this, GroupSubmitOrder.class);
                        intent.putExtra("groupgood", groupGoods);
                        //IsSingleBuy 2:用户参团 1：单独购  0:开团
                        intent.putExtra("IsSingleBuy", 0);
                        startActivity(intent);
                    }
                }
            });
            group_gp_singlebuy = (TextView) findViewById(R.id.group_gp_singlebuy);
            group_gp_singlebuy.setText(groupGoods.getSinglePrice()+"");
            group_gp_singlebuy_btn = (RelativeLayout) findViewById(R.id.group_gp_singlebuy_btn);
            group_gp_singlebuy_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Variables.my == null) {
                        Intent intent = new Intent(GroupGoodParticular.this, LoginBg.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(GroupGoodParticular.this, GroupSubmitOrder.class);
                        intent.putExtra("groupgood", groupGoods);
                        //IsSingleBuy 2:用户参团 1：单独购  0:开团
                        intent.putExtra("IsSingleBuy", 1);
                        startActivity(intent);
                    }
                }
            });
            group_gp_num = (TextView) findViewById(R.id.group_gp_num);
            group_gp_num.setText(groupGoods.getPersonNum() + "人团");
        }
        //页面通用详情
        group_goods_price = (TextView) findViewById(R.id.group_goods_price);
        group_goods_price.setText(groupGoods.getTuanPrice() + "");
        group_goods_originalprice = (TextView) findViewById(R.id.group_goods_originalprice);
        group_goods_originalprice.setText("￥" + groupGoods.getMarketPrice() + "（市场价）");
        group_goods_originalprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        group_goods_title = (TextView) findViewById(R.id.group_goods_title);
        group_goods_title.setText(groupGoods.getTitle());
        group_good_Standard = (TextView) findViewById(R.id.group_good_Standard);
        group_good_Standard.setText(groupGoods.getStandard());
        group_goods_content = (TextView) findViewById(R.id.group_goods_content);
        group_goods_content.setText(groupGoods.getPlace());
        image = (ImageView) findViewById(R.id.group_good_image);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setFailureDrawableId(R.mipmap.all_longding)
                //设置使用缓存
                .setUseMemCache(true)
                .setLoadingDrawableId(R.mipmap.all_longding)
                .build();
        x.image().bind(image, groupGoods.getImg(), imageOptions);
        wbv = (WebView) findViewById(R.id.group_good_scroll_webview);
        initWebView(groupGoods.getDetail());
        //设置拼团玩法四个字为绿色
        group_good_playexplain = (TextView) findViewById(R.id.group_good_playexplain);
        SpannableStringBuilder builder = new SpannableStringBuilder(group_good_playexplain.getText().toString());
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(Color.GREEN);
        builder.setSpan(greenSpan, 23, 27, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        group_good_playexplain.setText(builder);
        //返回按钮
        group_good_return_but = (ImageView) findViewById(R.id.group_good_return_but);
        group_good_return_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //跳转打开团购玩法帮助页面
        group_good_help = (LinearLayout) findViewById(R.id.group_good_help);
        group_good_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupGoodParticular.this, WEB.class);
                intent.putExtra("url", "http://www.baifenxian.com/html/tuanIntro/tuanIntro.html");
                intent.putExtra("title", "拼团玩法");
                startActivity(intent);
            }
        });
    }

    private void initWebView(String content) {

        //WebView加载web资源
        wbv.loadUrl(content);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        wbv.setWebViewClient(new WebViewClient() {
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
