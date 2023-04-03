package com.pvplan.UIComponents;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.pvplan.Fragments.LocationFragment;
import com.pvplan.Fragments.ResultsFragment;

public class ProjectConfigPageAdapter extends FragmentStateAdapter {
    int projectId;
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    public ProjectConfigPageAdapter(@NonNull FragmentActivity fragmentActivity, int projectId, TabLayout tabLayout, ViewPager2 viewPager2) {
        super(fragmentActivity);
        this.projectId = projectId;
        this.tabLayout = tabLayout;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LocationFragment(projectId, tabLayout, viewPager2);
        }else if(position == 1){
            return new ResultsFragment();
        }
        return new LocationFragment(projectId, tabLayout, viewPager2);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
