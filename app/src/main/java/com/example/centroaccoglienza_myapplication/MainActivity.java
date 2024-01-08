package com.example.centroaccoglienza_myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {
    Toolbar tabLayout;
    Button admin;
ImageView homeIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout=findViewById(R.id.toolbar);
        admin=findViewById(R.id.btnAmministrazione);
        homeIcon=findViewById(R.id.imageHome);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Amministrazione.class);


                startActivity(intent);
            }

       });



    }
}