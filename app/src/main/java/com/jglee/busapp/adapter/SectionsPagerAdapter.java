package com.jglee.busapp.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jglee.busapp.R;
import com.jglee.busapp.ui.fragment.BusListFragment;
import com.jglee.busapp.ui.fragment.StationListFragment;
import com.jglee.busapp.ui.fragment.FavoritesFragment;
import com.jglee.busapp.ui.fragment.RouteFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final String[] TAB_TITLES;
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        TAB_TITLES = mContext.getResources().getStringArray(R.array.tab_titles);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0) return new FavoritesFragment();
        if(position == 1) return new StationListFragment();
        if(position == 2) return new BusListFragment();
        if(position == 3) return new RouteFragment();
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        // Show total pages.
        return TAB_TITLES.length;
    }
}