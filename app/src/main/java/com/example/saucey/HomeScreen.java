package com.example.saucey;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.scwang.wave.MultiWaveHeader;

public class HomeScreen extends AppCompatActivity {
    MultiWaveHeader header,footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homee);

        header = findViewById(R.id.header);
        footer = findViewById(R.id.footer);

        header.setVelocity(1);
        header.setProgress(1);
        header.isRunning();
        header.setGradientAngle(45);
        header.setWaveHeight(40);
        header.setStartColor(R.color.purple);
        header.setCloseColor(Color.CYAN);

        footer.setVelocity(1);
        footer.setProgress(1);
        footer.isRunning();
        footer.setGradientAngle(45);
        footer.setWaveHeight(40);
        footer.setStartColor(Color.CYAN);
        footer.setCloseColor(R.color.purple);


        footer.setVelocity(1);
    }
}