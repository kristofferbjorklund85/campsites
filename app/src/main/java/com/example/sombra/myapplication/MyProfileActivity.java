package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * MyProfileActivity was never really created.
 * We didn't have time.
 * But the dream never dies.
 */
public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyProfileActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
    }
}
