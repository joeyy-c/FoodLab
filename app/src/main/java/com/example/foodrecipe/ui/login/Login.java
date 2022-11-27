package com.example.foodrecipe.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodrecipe.MainActivity;
import com.example.foodrecipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText mEmail,mPassword;
    private TextView signUp;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mEmail = findViewById(R.id.email_et);
        mPassword = findViewById(R.id.password_et);
        signUp = findViewById(R.id.signup_tv);
        loginBtn = findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();

//        getSupportActionBar().hide();
        SharedPreferences sharedPreferences = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "");
        if(!userId.isEmpty()){
            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this,"Email is Empty",Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this,"Password is Empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                userId = mAuth.getCurrentUser().getUid();
                                Toast.makeText(Login.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                // set user id in share preferenecs
                                SharedPreferences sharedPreferences = getSharedPreferences("user_id",MODE_PRIVATE);
                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                myEdit.putString("user_id", userId);
                                myEdit.commit();
                                Intent i = new Intent(Login.this, MainActivity.class);
                                startActivity(i);
                            }else {
                                Toast.makeText(Login.this,"Wrong Email or Password",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });//LOGIN BUTTON

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,Register.class);
                startActivity(i);
            }
        });
    }//OnCreate
}