package com.example.mywebapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class HeadlinesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.headlines_fragment, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_id);
        adapter = new ViewPagerAdapter(getFragmentManager());

        adapter.AddFragment(new WorldFragment(), "world");
        adapter.AddFragment(new BusinessFragment(), "business");
        adapter.AddFragment(new PoliticsFragment(), "politics");
        adapter.AddFragment(new SportsFragment(), "sports");
        adapter.AddFragment(new TechnologyFragment(), "technology");
        adapter.AddFragment(new ScienceFragment(), "science");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
