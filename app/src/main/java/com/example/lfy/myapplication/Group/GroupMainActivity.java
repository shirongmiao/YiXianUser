package com.example.lfy.myapplication.Group;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

public class GroupMainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationBar.OnTabSelectedListener {

    //UI Objects
    private TextView txt_topbar;
    private ImageView search, group_back;
    private ViewPager vpager;
    private BottomNavigationItem find, near, mine;

    private MyFragmentPagerAdapter mAdapter;

    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;

    BottomNavigationBar bottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.activity_group_main);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
    }

    private void bindViews() {
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setBackgroundColor(Color.parseColor("#fefefe"));
        txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        search = (ImageView) findViewById(R.id.group_serach);
        group_back = (ImageView) findViewById(R.id.group_back);


        find = new BottomNavigationItem(R.mipmap.group_find, R.string.tab_menu_find).setActiveColorResource(R.color.green);
        near = new BottomNavigationItem(R.mipmap.group_near, R.string.tab_menu_near).setActiveColorResource(R.color.green);
        mine = new BottomNavigationItem(R.mipmap.group_mine, R.string.tab_menu_mine).setActiveColorResource(R.color.green);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC
                );
        bottomNavigationBar.addItem(find)
                .addItem(near)
                .addItem(mine)
                .setFirstSelectedPosition(0)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(this);


        group_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setOffscreenPageLimit(2);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
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
                    bottomNavigationBar.selectTab(0);
                    txt_topbar.setText(R.string.tab_menu_find);
                    search.setVisibility(View.INVISIBLE);
                    break;
                case PAGE_TWO:
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
                    bottomNavigationBar.selectTab(1);
                    txt_topbar.setText(R.string.tab_menu_near);
                    search.setVisibility(View.VISIBLE);
                    break;
                case PAGE_THREE:
                    bottomNavigationBar.selectTab(2);
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

    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                vpager.setCurrentItem(PAGE_ONE);
                txt_topbar.setText(R.string.tab_menu_find);
                search.setVisibility(View.INVISIBLE);
                break;
            case 1:
                vpager.setCurrentItem(PAGE_TWO);
                txt_topbar.setText(R.string.tab_menu_near);
                search.setVisibility(View.VISIBLE);
                break;
            case 2:
                vpager.setCurrentItem(PAGE_THREE);
                txt_topbar.setText(R.string.tab_menu_mine);
                search.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}
