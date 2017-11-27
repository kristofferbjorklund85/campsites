package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    VolleyHandler vh;
    ArrayList<CampsiteModel> cml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vh = new VolleyHandler();
        vh.getCampsites(this);

    }

    public void login(View view) {
        cml = (ArrayList<CampsiteModel>) vh.getCampList();

        for(CampsiteModel cm : cml) {
            Log.d("List before: ", cm.location);
        }

        EditText et = findViewById(R.id.input_username);
        User.setUsername(et.getText().toString());
        Log.d("Username: ", User.getUsername());

        Log.d("starting: ", "Maps");
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        intent.putParcelableArrayListExtra("cmList", cml);
        startActivity(intent);
        finish();
    }

}
