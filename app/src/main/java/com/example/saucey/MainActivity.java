package com.example.saucey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register;
    private EditText loginText,password;
    private Button login;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar)findViewById(R.id.progressBar3);

        loginText = (EditText)findViewById(R.id.Location);
        password = (EditText)findViewById(R.id.password);

        login = (Button) findViewById(R.id.Login);
        login.setOnClickListener(this);

        register = (TextView) findViewById(R.id.Register);

        register.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.Login:
                Login();
                break;
            case R.id.Register:
                startActivity(new Intent(this,Register.class));
        }
    }

    private void Login() {
      String email = loginText.getText().toString().trim();
      String passwordd = password.getText().toString().trim();

        if(email.isEmpty()){
            loginText.setError("Please Enter Your Email");
            loginText.requestFocus();
            return;
        }
        if(passwordd.isEmpty()){
            password.setError("Please Enter Your Password");
            password.requestFocus();
            return;
        }
        if(passwordd.length() < 6){
            password.setError("Password Must Be Atleast 6 Characters");
            password.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginText.setError("Please Enter a Valid Email Address");
            loginText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   startActivity(new Intent(MainActivity.this,Profile.class));
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}