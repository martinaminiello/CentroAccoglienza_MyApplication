package com.example.centroaccoglienza_myapplication;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.centroaccoglienza_myapplication.fragments.InformazioniFragment;
import com.example.centroaccoglienza_myapplication.fragments.PosizioneFragment;
import com.example.centroaccoglienza_myapplication.fragments.VideoFragment;
import com.example.centroaccoglienza_myapplication.fragments.ServiziFragment;


public class MyViewPageAdapter extends FragmentStateAdapter {
    public MyViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

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
                return new ServiziFragment();
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

}
