package com.example.foodrecipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.foodrecipe.R;
import com.example.foodrecipe.ui.login.Login;

public class SplashScreen extends AppCompatActivity {

    ImageView logo,appName,splashImg;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        //Hide Action Bar
//        getSupportActionBar().hide();

        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.app_name);
        splashImg = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.lottie);

        //Duration of Splash Screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, Login.class);
                startActivity(i);
                finish();
            }
        },1200);

        //Animating Splash Screen
        splashImg.animate().translationY(-1600).setDuration(1000).setStartDelay(400);
        logo.animate().translationY(1400).setDuration(1000).setStartDelay(400);
        appName.animate().translationY(1400).setDuration(1000).setStartDelay(400);
        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(400);
    }
}