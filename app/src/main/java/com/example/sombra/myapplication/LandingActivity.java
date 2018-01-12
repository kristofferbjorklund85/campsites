package com.example.sombra.myapplication;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * The 'home' activity of the app where options such as going to the map, my profile and logging out
 * are available.
 */
public class LandingActivity extends AppCompatActivity {
    ArrayList<CampsiteModel> cml;
    Long back_pressed = 0L;

    /**
     * onCreate() sets the view for the activity.
     * Also sets a toolbar for the activity.
     *
     * Finally it runs getCampsites().
     *
     * @param savedInstanceState The standard Bundle from previous class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getCampsites();
    }

    /**
     * onResume() calls getCampsites(), refreshing our arrayList.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getCampsites();
    }

    /**
     * If the back button is pressed once the user is warned
     * that one more press will exit the app.
     * A second press within 1500 milliseconds will exit the app.
     */
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

    /**
     * mapsView() starts the MapsActivity and puts our finished campsiteList as a intent extra.
     *
     * @param view Needed for onClick() usage.
     */
    public void mapsView(View view) {
        Intent intent = new Intent(LandingActivity.this, MapsActivity.class);
        intent.putParcelableArrayListExtra("cmList", cml);
        startActivity(intent);
        setContentView(R.layout.activity_maps);
    }

    /**
     * logOut() resets the SessionSingleton user info and starts the LoginActivity.
     *
     * @param view Needed for onClick() usage.
     */
    public void logOut(View view) {
        Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
        startActivity(intent);
        SessionSingleton.setAppContext(null);
        SessionSingleton.setUsername("guest");
        SessionSingleton.setId(null);
        finish();
    }

    /**
     * getCampsites() retrives the campsites from our databse with a getHttpRequest.
     * If everything is okay we translates the JSONArray to an arrayList
     * and assign it to our campsiteList.
     */
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

    /**
     * fromJson() translate a JSONArray to an arrayList and populating it with campsiteModel objects.
     *
     * @param array JSONArray to be translated.
     * @return The complete campsiteList.
     */
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
                        jsonObj.getString("userId"));
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
