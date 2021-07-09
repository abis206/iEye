package com.example.saucey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.UUID;

public class ContactCreation extends AppCompatActivity implements View.OnClickListener{
    TextView email,phoneNumber,fullName,address;
    Button create;
    ImageView profile;
    Uri imageUri;
    ProgressBar progressBar;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;
    private String UserID;
    public String url = "neverChanged";
    final private static int GalleryCode = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_creation);

        email = findViewById(R.id.Location);
        phoneNumber = findViewById(R.id.password);
        fullName = findViewById(R.id.fullName);
        address = findViewById(R.id.llocation);
        create = findViewById(R.id.Login);
        profile = findViewById(R.id.imageView2);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camIntent = new Intent();
                camIntent.setType("image/*");
                camIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(camIntent,"Pick a Profile Picture"),GalleryCode);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                CreateNewContact();
            }
        });

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        mAuth = FirebaseAuth.getInstance();
        String[] id = mAuth.getCurrentUser().getEmail().split("@");
        UserID = id[0];
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
    }

    @Override
    public void onClick(View v) {

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GalleryCode && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            profile.setImageURI(imageUri);

        }
    }

    private void CreateNewContact() {
        final String emaill = email.getText().toString().trim();
        final String pn = phoneNumber.getText().toString().trim();
        final String fullname = fullName.getText().toString().trim();
        final String addy = address.getText().toString().trim();

        if(fullname.isEmpty()){
            fullName.setError("Please Enter Your Full Name");
            fullName.requestFocus();
            return;
        }

        if(emaill.isEmpty()){
            email.setError("Please Enter Your Email");
            email.requestFocus();
            return;
        }
        if(pn.isEmpty()){
            phoneNumber.setError("Please Enter Your Password");
            phoneNumber.requestFocus();
            return;
        }

        if(addy.isEmpty()){
            address.setError("Please Enter Your Bio");
            address.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()){
            email.setError("Please Enter a Valid Email Address");
            email.requestFocus();
            return;
        }

        final String imageKey = UUID.randomUUID().toString();
        StorageReference profileref = storageReference.child("contactProfiles/" + imageKey);
        profileref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //setting url equal to url of the image I want to retrieve
                        url = uri.toString();

                        final Contact contact = new Contact(emaill,pn,fullname,addy,imageKey,url);

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(UserID).child("contactList");
                        db.child(fullname).setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ContactCreation.this, "Successfully Created New Contact", Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // progressBar.setVisibility(View.VISIBLE);
                                        Toast.makeText(ContactCreation.this, "Failed to Create Contact", Toast.LENGTH_SHORT).show();
                                    }
                                });


                        Intent intent = new Intent(getApplicationContext(),ContactListPage.class);
                        intent.putExtra("profile_url",url);

                        startActivity(intent);
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ContactCreation.this, "Failed to Upload Profile Picture", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}