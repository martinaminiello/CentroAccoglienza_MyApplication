package com.example.centroaccoglienza_myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Amministrazione extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPageAdapter myViewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amministrazione);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);

        myViewPageAdapter = new MyViewPageAdapter(this);
        viewPager2.setAdapter(myViewPageAdapter);

        myViewPageAdapter.setupTabLayout(tabLayout, viewPager2);
        viewPager2.setOffscreenPageLimit(4);
        viewPager2.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Dati");
                            break;
                        case 1:
                            tab.setText("Mappa");
                            break;
                        case 2:
                            tab.setText("Pdf");
                            break;
                        case 3:
                            tab.setText("Video");
                            break;

                    }
                }).attach();
    }
}
