package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    ArrayList<CampsiteModel> cml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button = findViewById(R.id.login_button);

        VolleyHandler vh = new VolleyHandler();
        cml = (ArrayList) vh.getCampList(this);

    }

    public void login(View view) {

        /*for(CampsiteModel cm : cml) {
            Log.d("List before: ", cm.location);
        }*/

        Log.d("starting: ", "Maps");
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        intent.putParcelableArrayListExtra("cmList", cml);
        startActivity(intent);
        finish();
    }

}
