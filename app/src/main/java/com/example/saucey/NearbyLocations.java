package com.example.saucey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearbyLocations extends AppCompatActivity implements View.OnClickListener {
    TextView locationOneName, locationOneDuration, locationTwoName, locationTwoDuration, locationThirdName, locationThirdDuration;
    String[] facilities = new String[3];
    CardView location1, location2, location3;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_locations);
        locationOneName = findViewById(R.id.locationOneName);
        locationOneDuration = findViewById(R.id.locationOneDuration);
        locationTwoName = findViewById(R.id.locationTwoName);
        locationTwoDuration = findViewById(R.id.locationTwoDuration);
        locationThirdName = findViewById(R.id.locationThirdName);
        locationThirdDuration= findViewById(R.id.locationThirdDuration);

        location1 = findViewById(R.id.card1);
        location2 = findViewById(R.id.card2);
        location3 = findViewById(R.id.card);

        location1.setOnClickListener(this);
        location2.setOnClickListener(this);
        location3.setOnClickListener(this);

        Intent intent = getIntent();
        if(intent != null){
            facilities = intent.getStringArrayExtra("locations");
        }

        locationOneName.setText(facilities[0]);
        locationOneDuration.setText(Math.round(Float.parseFloat(facilities[2])) + " Miles");
        locationTwoName.setText(facilities[3]);
        locationTwoDuration.setText((Math.round(Float.parseFloat(facilities[5])) + " Miles"));
        locationThirdName.setText(facilities[6]);
        locationThirdDuration.setText(Math.round(Float.parseFloat(facilities[8]))+ " Miles");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card1:
                String[] info1 = new String[2];
                info1[0] = facilities[0];
                info1[1] = facilities[1];
                Intent intent1 = new Intent(NearbyLocations.this, ContactCreation.class);
                intent1.putExtra("info",info1);
                startActivity(intent1);
                break;
            case R.id.card2:
                String[] info2 = new String[2];
                info2[0] = facilities[3];
                info2[1] = facilities[4];
                Intent intent2 = new Intent(NearbyLocations.this, ContactCreation.class);
                intent2.putExtra("info",info2);
                startActivity(intent2);
                break;
            case R.id.card:
                String[] info3 = new String[2];
                info3[0] = facilities[6];
                info3[1] = facilities[7];
                Intent intent3 = new Intent(NearbyLocations.this, ContactCreation.class);
                intent3.putExtra("info",info3);
                startActivity(intent3);
                break;
        }
    }
}