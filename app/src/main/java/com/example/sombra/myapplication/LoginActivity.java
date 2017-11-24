package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        VolleyHandler vh = new VolleyHandler();
        vh.getCampsites(this);

        Log.d("starting: ", "Maps");
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
