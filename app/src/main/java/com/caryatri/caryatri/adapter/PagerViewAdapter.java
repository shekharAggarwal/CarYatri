package com.caryatri.caryatri.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.caryatri.caryatri.FragmentOneWay;
import com.caryatri.caryatri.FragmentRoundWay;

public class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new FragmentOneWay();
                break;
            case 1:
                fragment = new FragmentRoundWay();
                break;
            default:
                break;
        }
        return fragment == null ? new FragmentOneWay() : fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
