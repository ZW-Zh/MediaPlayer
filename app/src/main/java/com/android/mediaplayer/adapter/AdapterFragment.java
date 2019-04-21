package com.android.mediaplayer.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzw on 2017/4/19.
 */

public class AdapterFragment extends FragmentStatePagerAdapter {
    ArrayList<Fragment> mFragments;
    public Fragment currentFragment;
    private List<String> list_Titles =new ArrayList<>();

    public AdapterFragment(FragmentManager fm, ArrayList<Fragment> mFragments,List<String> list_Titles) {
        super(fm);
        this.mFragments=mFragments;
        this.list_Titles=list_Titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment=(Fragment)object;
        super.setPrimaryItem(container, position, object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list_Titles.get(position);
    }
}
