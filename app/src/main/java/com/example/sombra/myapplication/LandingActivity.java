package com.example.sombra.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class LandingActivity extends AppCompatActivity {

    VolleyHandler vh;
    ArrayList<CampsiteModel> cml;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        vh = new VolleyHandler();

        LandingActivity.context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //new loadMaps().execute();
    }

    public void mapsView(View view) {
        Intent intent = new Intent(LandingActivity.this, MapsActivity.class);
        intent.putParcelableArrayListExtra("cmList", cml);
        startActivity(intent);
        setContentView(R.layout.activity_maps);
    }

    public void loadCampsite(View view) {
        cml = (ArrayList<CampsiteModel>) vh.getCampList();

        Log.d("starting: ", "CampsiteActivity");
        Intent intent = new Intent(LandingActivity.this, CampsiteActivity.class);
        intent.putExtra("cm", cml.get(0));
        startActivity(intent);
    }

    /*private class loadMaps extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            vh.getCampsites(context);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            cml = (ArrayList<CampsiteModel>) vh.getCampList();
        }
    }*/
}
