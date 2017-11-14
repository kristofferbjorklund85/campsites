package com.example.sombra.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

public class Campsite extends AppCompatActivity {

    TextView testView;

    public static String ERROR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Campsite");
        setSupportActionBar(toolbar);
        init();

    }

    public void init() {
        ERROR = Campsite.class.getSimpleName() + getString(R.string.ERROR);

        String test_JSON = getString(R.string.test_JSON);
        CampsiteModel cm = createCampsite(test_JSON);

        TextView campsiteView = createTextView(cm);
    }

    public TextView createTextView(CampsiteModel cm) {
        TextView campsiteView = (TextView) findViewById(R.id.campsite_info);

        campsiteView.setText(   "Location: " + cm.location +
                                "\nCoordinates: " + cm.coordinates +
                                "\nType: " + cm.type +
                                "\nFee: " + cm.fee +
                                "\nCapacity: " + cm.capacity +
                                "\nAvailability: " + cm.availability +
                                "\nDescription: " + cm.description
                            );

        return campsiteView;
    }

    public CampsiteModel createCampsite(String jsonStr) {
        JSONObject jsonObj = null;

        try {
            jsonObj = new JSONObject(jsonStr);
        } catch (JSONException e) {
            Log.d("error 1", e.getMessage());
        }

        try {
            return new CampsiteModel(
                    jsonObj.getInt("id"),
                    jsonObj.getString("location"),
                    jsonObj.getString("coordinates"),
                    jsonObj.getString("type"),
                    jsonObj.getString("fee"),
                    jsonObj.getInt("capacity"),
                    jsonObj.getString("availability"),
                    jsonObj.getString("description"));

        } catch (JSONException e) {
            Log.d("error 2", e.getMessage());
        }
        return null;
    }



}
