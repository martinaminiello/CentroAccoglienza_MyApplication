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

        tabLayout= findViewById(R.id.tab_layout);

        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(new MyViewPageAdapter(this));
        viewPager2.setOffscreenPageLimit(3);

        viewPager2.setUserInputEnabled(false);


        TabLayout tabLayout = findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    // Set tab text or icon as needed
                    switch (position) {
                        case 0:
                            tab.setText("Dati");
                            break;
                        case 1:
                            tab.setText("Posizione");
                            break;
                        case 2:
                            tab.setText("Servizi");
                            break;
                        case 3:
                            tab.setText("Video");
                            break;
                    }
                }).attach();
    }
}