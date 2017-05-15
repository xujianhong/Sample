package com.example.administrator.myapplication.Tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by xjh on 2016/6/30.
 */
public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragmentList = null;
    public CommonFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if(position < this.mFragmentList.size()) {
            fragment = this.mFragmentList.get(position);
        } else {
            fragment = this.mFragmentList.get(0);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return this.mFragmentList.size();
    }
}
