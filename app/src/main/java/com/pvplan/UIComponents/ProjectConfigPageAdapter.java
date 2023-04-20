package com.pvplan.UIComponents;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.pvplan.Fragments.ArrayFragment;
import com.pvplan.Fragments.ConsumersFragment;
import com.pvplan.Fragments.LocationFragment;
import com.pvplan.Fragments.ResultsFragment;
import com.pvplan.Fragments.StorageFragment;
import com.pvplan.ProjectConfigurationActivity;

public class ProjectConfigPageAdapter extends FragmentStateAdapter {
    ProjectConfigurationActivity parentActivity;
    int projectId;
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    public ProjectConfigPageAdapter(@NonNull FragmentActivity fragmentActivity, ProjectConfigurationActivity parentActivity, int projectId, TabLayout tabLayout, ViewPager2 viewPager2) {
        super(fragmentActivity);
        this.parentActivity = parentActivity;
        this.projectId = projectId;
        this.tabLayout = tabLayout;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("fragment number", Integer.toString(position));

        if(position == 1){
            return new ConsumersFragment(projectId, tabLayout, viewPager2);
        }else if(position == 2){
            return new ArrayFragment();
        }else if(position == 3){
            return new StorageFragment();
        }else if(position == 4){
            return new ResultsFragment(projectId);
        }
        return new LocationFragment(parentActivity, projectId, tabLayout, viewPager2);
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
