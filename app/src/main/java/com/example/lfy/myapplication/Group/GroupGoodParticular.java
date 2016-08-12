package com.example.lfy.myapplication.Group;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GroupGoodsBean;
import com.example.lfy.myapplication.FragmentHome.scan.WEB;
import com.example.lfy.myapplication.GoodsParticular.Goods_Particular;
import com.example.lfy.myapplication.MyApplication;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.xutils.image.ImageOptions;
import org.xutils.x;

public class GroupGoodParticular extends AppCompatActivity {
    ImageView image, group_good_more_image, group_good_return_but;
    TextView group_gp_singlebuy, group_gp_num, group_gp_buy, group_goods_price, group_goods_originalprice, group_goods_title, group_good_Standard, group_goods_content;
    WebView wbv;
    TextView group_good_playexplain;
    RelativeLayout group_gp_buy_btn, group_gp_singlebuy_btn;
    LinearLayout group_good_help;
    GroupGoodsBean groupGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.activity_group_good_particular);
        Intent intent = getIntent();
        groupGoods = (GroupGoodsBean) intent.getSerializableExtra("groupgood");
        initView();
    }

    public void initView() {
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
        group_good_more_image = (ImageView) findViewById(R.id.group_good_more_image);
        x.image().bind(group_good_more_image, "http://www.baifenxian.com/images/peisong.png", imageOptions);
        initWebView(groupGoods.getDetail());
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
                    intent.putExtra("IsSingleBuy", 0);
                    startActivity(intent);
                }
            }
        });
        group_gp_singlebuy = (TextView) findViewById(R.id.group_gp_singlebuy);
        group_gp_singlebuy.setText(groupGoods.getSinglePrice());
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
                    intent.putExtra("IsSingleBuy", 1);
                    startActivity(intent);
                }
            }
        });
        group_gp_num = (TextView) findViewById(R.id.group_gp_num);
        group_gp_num.setText(groupGoods.getPersonNum() + "人团");
        group_good_playexplain = (TextView) findViewById(R.id.group_good_playexplain);
        SpannableStringBuilder builder = new SpannableStringBuilder(group_good_playexplain.getText().toString());
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(Color.GREEN);
        builder.setSpan(greenSpan, 23, 27, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        group_good_playexplain.setText(builder);
        group_good_return_but = (ImageView) findViewById(R.id.group_good_return_but);
        group_good_return_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
