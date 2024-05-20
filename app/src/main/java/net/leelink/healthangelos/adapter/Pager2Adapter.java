package net.leelink.healthangelos.adapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class Pager2Adapter extends FragmentStateAdapter {
    private List<Fragment> fragments;

    public Pager2Adapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public Pager2Adapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);

        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }


    @Override
    public int getItemCount() {
        return fragments==null?0:fragments.size();
    }


}
