package com.RadioSfax.acer.radiosfax;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent Homeintent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(Homeintent);
                finish();
            }

        }, SPLASH_TIME_OUT);


    }
}
