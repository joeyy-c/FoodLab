package com.example.foodrecipe.ui.login;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodrecipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private TextView signIn;
    private EditText mEmail,mPassword,mConfirmPass,mUsername;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        signIn = findViewById(R.id.signin_tv);
        registerBtn = findViewById(R.id.reg_btn);
        mEmail = findViewById(R.id.email_et);
        mPassword = findViewById(R.id.pass_et);
        mUsername = findViewById(R.id.username_et);
        mConfirmPass = findViewById(R.id.confirmPass_et);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

//        getSupportActionBar().hide();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String confirm = mConfirmPass.getText().toString();
                if (TextUtils.isEmpty(username)){
                    mUsername.setError("Enter Your Username");
                }
                else if (TextUtils.isEmpty(password)){
                    mEmail.setError("Enter Your Email");
                }
                else if (TextUtils.isEmpty(password)){
                    mPassword.setError("Enter Your Password");
                }
                else if (!confirm.equals(password)){
                    Toast.makeText(Register.this,"Password does not match",Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Register.this,"Account Created",Toast.LENGTH_SHORT).show();
                                userID = mAuth.getCurrentUser().getUid();
                                //Insert into Firestore database
                                DocumentReference r = fStore.collection("users").document(userID);
                                Map<String,Object> user = new HashMap<>();
                                user.put("username",username);
                                user.put("email",email);
                                user.put("password",password);
                                r.set(user);
                                startActivity(new Intent(getApplicationContext(),Login.class));
                            }else {
                                Toast.makeText(Register.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });}
            }
        });//REGISTER BUTTON
    }//OnCreate
}//Class