package fr.ycoupe.pronobike.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments;
    private final List<Integer> icons;

    public ViewPagerAdapter(final FragmentManager manager) {
        super(manager);
        fragments = new ArrayList<>();
        icons = new ArrayList<>();
    }

    @Override
    public Fragment getItem(final int position) {
        return fragments.get(position);
    }

    public int getItemIcon(final int position) {
        return icons.get(position);
    }

    public List<Fragment> getItems() {
        return fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public int addFragment(final Fragment fragment, final int icon) {
        fragments.add(fragment);
        icons.add(icon);
        return icons.size() - 1;
    }
}
