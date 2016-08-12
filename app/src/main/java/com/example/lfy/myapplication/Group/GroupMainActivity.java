package com.example.lfy.myapplication.Group;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

public class GroupMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {

    //UI Objects
    private TextView txt_topbar;
    private ImageView search, group_back;
    private RadioGroup rg_tab_bar;
    private RadioButton rb_find;
    private RadioButton rb_near;
    private RadioButton rb_mine;
    private ViewPager vpager;

    private MyFragmentPagerAdapter mAdapter;

    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.activity_group_main);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        rb_find.setChecked(true);
    }

    private void bindViews() {
        txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_find = (RadioButton) findViewById(R.id.rb_find);
        rb_near = (RadioButton) findViewById(R.id.rb_near);
        rb_mine = (RadioButton) findViewById(R.id.rb_mine);
        search = (ImageView) findViewById(R.id.group_serach);
        group_back = (ImageView) findViewById(R.id.group_back);
        group_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rg_tab_bar.setOnCheckedChangeListener(this);

        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setOffscreenPageLimit(2);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_find:
                vpager.setCurrentItem(PAGE_ONE);
                txt_topbar.setText(R.string.tab_menu_find);
                search.setVisibility(View.INVISIBLE);
                break;
            case R.id.rb_near:
                vpager.setCurrentItem(PAGE_TWO);
                txt_topbar.setText(R.string.tab_menu_near);
                search.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_mine:
                vpager.setCurrentItem(PAGE_THREE);
                txt_topbar.setText(R.string.tab_menu_mine);
                search.setVisibility(View.INVISIBLE);
                break;
        }
    }


    //重写ViewPager页面切换的处理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_find.setChecked(true);
                    txt_topbar.setText(R.string.tab_menu_find);
                    search.setVisibility(View.INVISIBLE);
                    break;
                case PAGE_TWO:
                    rb_near.setChecked(true);
                    txt_topbar.setText(R.string.tab_menu_near);
                    search.setVisibility(View.VISIBLE);
                    break;
                case PAGE_THREE:
                    rb_mine.setChecked(true);
                    txt_topbar.setText(R.string.tab_menu_mine);
                    search.setVisibility(View.INVISIBLE);
                    if (Variables.my == null) {
                        AlertDialog.Builder dig = new AlertDialog.Builder(this);
                        dig.setMessage("您还未登录，是否去登录？");
                        dig.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(GroupMainActivity.this, LoginBg.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", null);
                        dig.show();
                    }
                    break;
            }
        }
    }
}
