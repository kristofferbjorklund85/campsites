package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (true) {
            User.setUsername("JanBanan");
            User.setAppContext(this.getApplicationContext());
            Log.d("starting: ", "Landing");
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void login(View view) {
        EditText un = findViewById(R.id.input_username);
        EditText pw = findViewById(R.id.input_password);
        if (FieldChecker.checkString(this, un.getText().toString(), "Username") &&
                FieldChecker.checkString(this, pw.getText().toString(), "Password")) {
            User.setUsername(un.getText().toString());
            User.setAppContext(this.getApplicationContext());
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
