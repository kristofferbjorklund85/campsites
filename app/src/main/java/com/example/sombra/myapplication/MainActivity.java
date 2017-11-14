package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static String ERROR;

    // Start the app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ERROR = getString(R.string.ERROR);
        Intent intent = new Intent(MainActivity.this, Campsite.class);
        startActivity(intent);
        finish();
    }

}
