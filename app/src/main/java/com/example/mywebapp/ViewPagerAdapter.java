package com.example.mywebapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> lstfragments = new ArrayList<Fragment>();
    private final List<String> lsttitles = new ArrayList<String>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return lstfragments.get(position);
    }

    @Override
    public int getCount() {
        return lsttitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return lsttitles.get(position);
    }

    public void AddFragment(Fragment f, String t) {
        lstfragments.add(f);
        lsttitles.add(t);
    }
}


