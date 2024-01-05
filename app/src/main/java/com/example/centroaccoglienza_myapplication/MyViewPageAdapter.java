package com.example.centroaccoglienza_myapplication;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import com.example.centroaccoglienza_myapplication.fragments.DocumentiFragment;
import com.example.centroaccoglienza_myapplication.fragments.InformazioniFragment;
import com.example.centroaccoglienza_myapplication.fragments.PosizioneFragment;
import com.example.centroaccoglienza_myapplication.fragments.VideoFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MyViewPageAdapter extends FragmentStateAdapter {
    private final int[] tabIcons = {
            R.drawable.building,
            R.drawable.mapicon_removebg_preview,
            R.drawable.docu_removebg_preview,
            R.drawable.video_removebg_preview,
            R.drawable.services_removebg_preview
    };

    private final Context context;

    public MyViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.context = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new InformazioniFragment();
            case 1:
                return new PosizioneFragment();
            case 2:
                return new DocumentiFragment();
            case 3:
                return new VideoFragment();

            default:
                return new InformazioniFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public void setupTabLayout(TabLayout tabLayout, ViewPager2 viewPager) {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View tabView = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            ImageView tabIcon = tabView.findViewById(R.id.tab_icon);

            tabIcon.setImageResource(tabIcons[position]);

            tab.setCustomView(tabView);
        }).attach();
    }
}
