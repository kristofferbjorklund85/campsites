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

    VolleyHandler vh;
    ArrayList<CampsiteModel> cml;
    private static Context context;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        vh = new VolleyHandler();

        LandingActivity.context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        url = this.getResources().getString(R.string.apiURL);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scooby dooby doo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        User.setUsername("JanBanan");
        getCampsites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCampsites();
    }

    public void mapsView(View view) {
        Intent intent = new Intent(LandingActivity.this, MapsActivity.class);
        intent.putParcelableArrayListExtra("cmList", cml);
        startActivity(intent);
        setContentView(R.layout.activity_maps);
    }

    public void loadCampsite(View view) {
        //cml = (ArrayList<CampsiteModel>) vh.getCampList();

        CampsiteModel camp = new CampsiteModel(
                                    "860f729e-5a4f-4398-ba05-0062cdf875b3",
                                    "Gothenburg",
                                    "Lindholmen",
                                    -32.952854,
                                    116.857342,
                                    "School",
                                    "Free",
                                    50,
                                    "All year",
                                    "Very nice place, lots of cool people",
                                    3.5,
                                    12312,
                                    "Satan666");


        Log.d("starting: ", "CampsiteActivity");
        Intent intent = new Intent(LandingActivity.this, CampsiteActivity.class);
        intent.putExtra("cm", camp);
        startActivity(intent);
    }

    public void getCampsites() {

        Log.d("getCampsites ", "starting");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=campsite", //+ "&param1=" + currentLat + "&param2=" + currentLng,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        cml = (ArrayList) fakeJSON(array);
                        Log.d("on Response: ", "setting campsites");
                        Log.d("campsitemodeListLanding", String.valueOf(cml.size()));
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.getCause().getMessage());
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    public List fakeJSON(JSONArray array) {
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
                        jsonObj.getInt("capacity"),
                        jsonObj.getString("availability"),
                        jsonObj.getString("description"),
                        jsonObj.getDouble("rating"),
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
