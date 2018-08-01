package sk.dejw.android.georiddles.adapters;

/**
 * Based on https://stackoverflow.com/questions/41413150/fragment-tabs-inside-fragment
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class RiddlePagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();

    public RiddlePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void swapFragmentAtPosition(int position, Fragment fragment, String title) {
        mFragmentList.set(position, fragment);
        mFragmentTitleList.set(position, title);
        notifyDataSetChanged();
    }
}
