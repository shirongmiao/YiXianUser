package com.example.lfy.myapplication.Group;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/8/4 0004.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 3;
    private GroupFind myFragment1 = null;
    private GroupNear myFragment2 = null;
    private GroupMine myFragment3 = null;



    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        myFragment1 = new GroupFind();
        myFragment2 = new GroupNear();
        myFragment3 = new GroupMine();

    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case GroupMainActivity.PAGE_ONE:
                fragment = myFragment1;
                break;
            case GroupMainActivity.PAGE_TWO:
                fragment = myFragment2;
                break;
            case GroupMainActivity.PAGE_THREE:
                fragment = myFragment3;
                break;
        }
        return fragment;
    }

}
