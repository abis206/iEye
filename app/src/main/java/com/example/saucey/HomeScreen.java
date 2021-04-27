package com.example.saucey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scwang.wave.MultiWaveHeader;

public class HomeScreen extends AppCompatActivity {
    MultiWaveHeader header,footer;
    TextView name;
    FirebaseAuth mAuth;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homee);
        name = findViewById(R.id.homeuser);
        mAuth = FirebaseAuth.getInstance();
        String[] id = mAuth.getCurrentUser().getEmail().split("@");
        UserID = id[0];
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(UserID).child("fullname");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String f = snapshot.getValue(String.class);
                String[] di = f.split(" ");
                name.setText(di[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        header = findViewById(R.id.header);
        footer = findViewById(R.id.footer);

        header.setVelocity(1);
        header.setProgress(1);
        header.isRunning();
        header.setGradientAngle(45);
        header.setWaveHeight(40);
        header.setStartColor(Color.CYAN);
        header.setCloseColor(Color.BLUE);

        footer.setVelocity(1);
        footer.setProgress(1);
        footer.isRunning();
        footer.setGradientAngle(45);
        footer.setWaveHeight(40);
        footer.setStartColor(Color.CYAN);
        header.setCloseColor(Color.BLUE);


        footer.setVelocity(1);
    }
}