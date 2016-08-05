package com.example.lfy.myapplication.FragmentOrder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lfy.myapplication.FragmentOrder.child_fragment.All;
import com.example.lfy.myapplication.FragmentOrder.child_fragment.Already;
import com.example.lfy.myapplication.FragmentOrder.child_fragment.No_delivery;
import com.example.lfy.myapplication.FragmentOrder.child_fragment.Non_payment;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.ViewPagerIndicator;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lfy on 2016/5/31.
 */
public class FragmentOrder extends Fragment {

    /**
     * 四个导航按钮
     * <p/>
     * /**
     * 作为页面容器的ViewPager
     */
    MyFrageStatePagerAdapter adapter;
    static ViewPager mViewPager;
    static ViewPagerIndicator indicator;
    /**
     * 页面集合
     */
    List<Fragment> fragmentList;

    /**
     * 四个Fragment（页面）
     */
    All allFragment;
    public static Non_payment paymentFragment;
    public static No_delivery deliveryFragment;
    Already alreadyFragment;

    public View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmetorder, container, false);
        adapter = new MyFrageStatePagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        indicator = (ViewPagerIndicator) view.findViewById(R.id.indicator);

        initView();
        indicator.setTabItemTitles(initTitle());
        mViewPager.setOffscreenPageLimit(4);

        mViewPager.setAdapter(adapter);
        indicator.setViewPager(mViewPager, 0);

        return view;
    }

    public static void update(final int num) {
        indicator.setViewPager(mViewPager, num);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                if (num == 0) {
                    deliveryFragment.setUpdate();
                } else {
                    paymentFragment.setUpdate();
                }
            }
        }, 10);

    }

    private void initView() {
        fragmentList = new ArrayList<Fragment>();
        allFragment = new All();
        paymentFragment = new Non_payment();
        deliveryFragment = new No_delivery();
        alreadyFragment = new Already();

        fragmentList.add(deliveryFragment);
        fragmentList.add(paymentFragment);
        fragmentList.add(alreadyFragment);
        fragmentList.add(allFragment);
    }


    /**
     * 定义自己的ViewPager适配器。
     * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。
     */
    class MyFrageStatePagerAdapter extends FragmentPagerAdapter {

        public MyFrageStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    private List<String> initTitle() {
        List<String> title = new ArrayList<>();
        title.add("未收货");
        title.add("未付款");
        title.add("已提货");
        title.add("全部订单");
        return title;
    }
}
