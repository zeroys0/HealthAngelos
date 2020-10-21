package net.leelink.healthangelos.adapter;

import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class HomePagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;
    FragmentManager fragmentManager;

    public HomePagerAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fragmentManager = fm;
        this.fragments = fragments;
    }
    @Override
    public int getCount() {
        return fragments.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


}
