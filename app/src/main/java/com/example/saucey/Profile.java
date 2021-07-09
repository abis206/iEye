package com.example.saucey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.squareup.picasso.Picasso;


public class Profile extends AppCompatActivity implements View.OnClickListener{
    String userID;

    public String testName;
    public String profileURL;
    String contactEmail;
    String contactAddress;
    String contactPhone;
    String imageKey;
    String retrievalCode;

    ImageView profilePic;
    TextView name;
    ImageView emailListIcon;
    TextView emailListText;
    TextView email;
    TextView address;
    TextView phoneNumber;
    Button modify;
    EditText editEmail,editAdd,editPhone;
    Boolean inEmailList;
    FirebaseAuth mAuth;
    DatabaseReference contactInfo;
    DatabaseReference emailList;
    boolean modifyPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        testName = intent.getStringExtra("selected_name");
        profilePic = findViewById(R.id.profile_image);
        name = findViewById(R.id.Name);
        emailListIcon = findViewById(R.id.emailListIcon);
        emailListText = findViewById(R.id.emailListText);
        email = findViewById(R.id.textView4);
        address = findViewById(R.id.textView6);
        phoneNumber = findViewById(R.id.textView8);
        editEmail = findViewById(R.id.edit_email);
        editAdd = findViewById(R.id.edit_address);
        editPhone = findViewById(R.id.edit_phone_number);
        modify = findViewById(R.id.button);
        modifyPressed = false;
        editEmail.setVisibility(View.GONE);
        editAdd.setVisibility(View.GONE);
        editPhone.setVisibility(View.GONE);
        emailListIcon.setOnClickListener(this);
        modify.setText("MODIFY");
        modify.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        String[] id = mAuth.getCurrentUser().getEmail().split("@");
        userID = id[0];
        contactInfo = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("contactList").child(testName);
        emailList = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("EmailList");

        name.setText(testName);

        FirebaseDatabase.getInstance().getReference("Users").child(userID).child("EmailList").child(testName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    inEmailList = true;
                    emailListIcon.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                    emailListText.setText("Remove From Email List");
                }else{
                    inEmailList = false;
                    emailListIcon.setImageResource(R.drawable.ic_baseline_notification_add_24);
                    emailListText.setText("Add To Email List");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to check if email is is email list", Toast.LENGTH_SHORT).show();
            }
        });
        

        contactInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactEmail = snapshot.child("email").getValue().toString();
                email.setText(contactEmail);
                contactAddress = snapshot.child("address").getValue(String.class);
                address.setText(contactAddress);
                contactPhone = snapshot.child("phoneNumber").getValue(String.class);
                phoneNumber.setText(contactPhone);
                imageKey = snapshot.child("imageKey").getValue(String.class);
                retrievalCode = snapshot.child("retrievalCode").getValue(String.class);
                StorageReference pic =  FirebaseStorage.getInstance().getReference().child("contactProfiles/"+imageKey);
                Picasso.get().load(retrievalCode).into(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to get the stuff", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emailListIcon:
                if(inEmailList){
                    inEmailList = false;
                    emailListIcon.setImageResource(R.drawable.ic_baseline_notification_add_24);
                    emailListText.setText("Add To Email List");
                    emailList.child(testName).removeValue();
                }else{
                    inEmailList = true;
                    emailListIcon.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                    Map<String,String> email= new HashMap<>();
                    email.put(testName + "'s Email",contactEmail);
                    emailListText.setText("Remove From Email List");
                    emailList.child(testName).setValue(email);
                }
                break;
            case R.id.button:
                if (!modifyPressed){
                    email.setVisibility(View.GONE);
                    address.setVisibility(View.GONE);
                    phoneNumber.setVisibility(View.GONE);
                    editEmail.setVisibility(View.VISIBLE);
                    editAdd.setVisibility(View.VISIBLE);
                    editPhone.setVisibility(View.VISIBLE);
                    modify.setText("CONFIRM");
                    modifyPressed = true;
                }else{
                    email.setText(editEmail.getText());
                    address.setText(editAdd.getText());
                    phoneNumber.setText(editPhone.getText());
                    editEmail.setVisibility(View.GONE);
                    editAdd.setVisibility(View.GONE);
                    editPhone.setVisibility(View.GONE);
                    email.setVisibility(View.VISIBLE);
                    address.setVisibility(View.VISIBLE);
                    phoneNumber.setVisibility(View.VISIBLE);
                    modify.setText("MODIFY");
                    Map<String,String> email = new HashMap<String, String>();
                    email.put("email",editEmail.getText().toString());
                    contactInfo.child("email").setValue(editEmail.getText().toString());
                    if(inEmailList){
                        emailList.child(testName).child(testName+"'s Email").setValue(editEmail.getText().toString());
                    }
                    contactInfo.child("address").setValue(editAdd.getText().toString());
                    contactInfo.child("phoneNumber").setValue(editPhone.getText().toString());
                    modifyPressed = false;
                    Toast.makeText(Profile.this,"Contact Updated!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Profile.this,ContactListPage.class));
                }
        }
    }
}