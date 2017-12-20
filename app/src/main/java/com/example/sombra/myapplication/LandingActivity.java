package com.example.sombra.myapplication;

import android.content.Intent;
import android.media.MediaCas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LandingActivity extends AppCompatActivity {
    ArrayList<CampsiteModel> cml;
    String url;
    Long back_pressed = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scooby dooby doo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getCampsites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCampsites();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 1500 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else{
            Utils.toast("Press once again to exit!", "short");
        }
        back_pressed = System.currentTimeMillis();
    }

    public void mapsView(View view) {
        Intent intent = new Intent(LandingActivity.this, MapsActivity.class);
        intent.putParcelableArrayListExtra("cmList", cml);
        startActivity(intent);
        setContentView(R.layout.activity_maps);
    }

    //Remove for production
    public void loadCampsite(View view) {
        /*CampsiteModel camp = new CampsiteModel(
                                    "860f729e-5a4f-4398-ba05-0062cdf875b3",
                                    "Gothenburg",
                                    "Lindholmen",
                                    -32.952854,
                                    116.857342,
                                    "School",
                                    "Free",
                                    "50",
                                    "All year",
                                    "Very nice place, lots of cool people",
                                    12312,
                                    "Satan666");*/


        Log.d("starting: ", "CampsiteActivity");
        Intent intent = new Intent(LandingActivity.this, CampsiteActivity.class);
        intent.putExtra("cm", cml.get(0));
        startActivity(intent);
    }

    public void logOut(View view) {
        Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
        startActivity(intent);
        SessionSingleton.setAppContext(null);
        SessionSingleton.setUsername("guest");
        SessionSingleton.setId(null);
        finish();
    }

    public void getCampsites() {
        Log.d("getCampsites ", "starting");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                SessionSingleton.getURL() + "?type=campsite",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        cml = (ArrayList) fromJson(array);
                        Log.d("on Response: ", "setting campsites");
                        Log.d("campsitemodeListLanding", String.valueOf(cml.size()));
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.toString());
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    public List fromJson(JSONArray array) {
        List<CampsiteModel> campList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObj = array.getJSONObject(i);
                CampsiteModel cm = new CampsiteModel(
                        jsonObj.getString("id"),
                        jsonObj.getString("location"),
                        jsonObj.getString("name"),
                        jsonObj.getDouble("lat"),
                        jsonObj.getDouble("lng"),
                        jsonObj.getString("type"),
                        jsonObj.getString("fee"),
                        jsonObj.getString("capacity"),
                        jsonObj.getString("availability"),
                        jsonObj.getString("description"),
                        jsonObj.getInt("views"),
                        jsonObj.getString("username"));
                campList.add(cm);
                Log.d("fromJSON: ", "created object");
            } catch (JSONException e) {
                Log.d("fromJSON Exception: ", e.getMessage());
            }
        }

        Log.d("fromJSON: ", "Returning List of " + campList.size());
        return campList;
    }
}
