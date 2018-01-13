package com.example.sombra.camps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * MainActivity starts the app.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * onCreate() starts the LoginActivity.
     *
     * @param savedInstanceState The standard Bundle from previous class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
