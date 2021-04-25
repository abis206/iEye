package com.example.saucey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register2 extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    final private static int GalleryCode = 123;
    private EditText location, fire;
    private ImageView profile;
    private Button finish;
    private ProgressBar progressBar;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        mAuth = FirebaseAuth.getInstance();
        String[] id = mAuth.getCurrentUser().getEmail().split("@");
        UserID = id[0];

        location = (EditText) findViewById(R.id.Location);
        fire  = (EditText) findViewById(R.id.password);
        profile = (ImageView) findViewById(R.id.imageView2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        finish = (Button) findViewById(R.id.complete);
        profile = (ImageView) findViewById(R.id.imageView2);
        
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camIntent = new Intent();
                camIntent.setType("image/*");
                camIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(camIntent,"Pick a Profile Picture"),GalleryCode);
                Toast.makeText(Register2.this, "Right Button was clicked", Toast.LENGTH_SHORT).show();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Register2.this,"U pressed Login",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                finishRegistration();
            }
        });
   }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GalleryCode && resultCode == RESULT_OK && data != null){
          Uri imageData = data.getData();
          profile.setImageURI(imageData);
        }
    }

    private void finishRegistration() {
        String firee = fire.getText().toString().trim();
        String loc = location.getText().toString().trim();
        Toast.makeText(Register2.this,"U pressed finished",Toast.LENGTH_SHORT).show();

        if(firee.isEmpty()){
            fire.setError("Please Enter Your Validity");
            fire.requestFocus();
            return;
        }
        if(loc.isEmpty()){
            location.setError("Please Enter Your Location");
            location.requestFocus();
            return;
        }

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
        String userID = UserID;
        Toast.makeText(Register2.this,"User ID: " + db.push().getKey(),Toast.LENGTH_SHORT).show();
        db.child(UserID).child("location").setValue(loc);
        db.child(UserID).child("realniggastatus").setValue(firee).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(Register2.this,Profile.class));
            }
        });
       // db.child(UserID).child("profile").setValue(profile);
       // Toast.makeText(Register2.this,"It worked?",Toast.LENGTH_SHORT).show();

    }
}
