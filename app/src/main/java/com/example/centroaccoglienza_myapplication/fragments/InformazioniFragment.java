package com.example.centroaccoglienza_myapplication.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.centroaccoglienza_myapplication.R;


public class InformazioniFragment extends Fragment {

    EditText editNome;
    EditText editIndirizzo;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_informazioni, container, false);

        editNome=view.findViewById(R.id.editNome);

        return view;
    }
}