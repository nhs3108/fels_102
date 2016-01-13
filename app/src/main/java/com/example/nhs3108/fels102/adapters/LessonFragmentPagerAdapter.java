package com.example.nhs3108.fels102.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by nhs3108 on 1/13/16.
 */
public class LessonFragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> mListFragments;

    public LessonFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.mListFragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mListFragments.get(position);
    }

    @Override
    public int getCount() {
        return mListFragments.size();
    }
}
