package com.example.saucey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saucey.Register2;
import com.example.saucey.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Register extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private TextView title,next;
    private EditText editTextEmail,editTextpassword,editTextfullName,editTextAge,editTextaddress,editTextGender;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        next = (Button) findViewById(R.id.Login);
        next.setOnClickListener(this);
        title = (TextView) findViewById(R.id.textView2);
        title.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.Location);
        editTextpassword = (EditText) findViewById(R.id.password);
        editTextAge = (EditText) findViewById(R.id.Username);
        editTextfullName = (EditText) findViewById(R.id.fullName);
        editTextaddress = (EditText) findViewById(R.id.llocation);
        editTextGender = (EditText) findViewById(R.id.gender);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.textView2:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.Login:
                RegisterUser();
        }
    }

    private void RegisterUser() {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();
        final String age = editTextAge.getText().toString().trim();
        final String fullName= editTextfullName.getText().toString();
        final String gender = editTextGender.getText().toString().trim();
        final String address = editTextaddress.getText().toString().trim();
        ArrayList<String> contactList = new ArrayList<String>();
        contactList.add("test");
        //Lack of submission picks
        if(fullName.isEmpty()){
            editTextfullName.setError("Please Enter Your Full Name");
            editTextfullName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("Please Enter Your Email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextpassword.setError("Please Enter Your Password");
            editTextpassword.requestFocus();
            return;
        }
        if(age.isEmpty()){
            editTextAge.setError("Please Enter Your Username");
            editTextAge.requestFocus();
            return;
        }
        if(address.isEmpty()){
            editTextaddress.setError("Please Enter Your Bio");
            editTextaddress.requestFocus();
            return;
        }
        if(gender.isEmpty()){
            editTextGender.setError("Please Enter Your Bio");
            editTextGender.requestFocus();
            return;
        }

        //Email Checker validity checking
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please Enter a Valid Email Address");
            editTextEmail.requestFocus();
            return;
        }

        //Password Checking
        if(password.length() < 6){
            editTextpassword.setError("Password Must Be Atleast 6 Characters");
            editTextpassword.requestFocus();
            return;
        }

        final User user = new User(fullName, email, gender, address, age, contactList);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference root = db.getReference().child("Users");
        final String[] emaill = email.split("@");
        progressBar.setVisibility(ViewStub.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(Register.this, "Create Auth for User Succeeded", Toast.LENGTH_SHORT).show();
                    root.child(emaill[0]).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(ViewStub.GONE);
                                Toast.makeText(Register.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this,HomeScreen.class));
                            }
                            else{
                                Toast.makeText(Register.this, "Failed to Register User :( ", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(ViewStub.GONE);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Register.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ViewStub.GONE);
                }
            }
        });
    }
}