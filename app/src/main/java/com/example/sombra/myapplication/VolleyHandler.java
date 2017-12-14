package com.example.sombra.myapplication;

import android.content.Context;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sombra on 2017-11-24.
 */

public class VolleyHandler {

    private List<CampsiteModel> campsites;
    List<Comment> comments;
    private double lat = MapsActivity.getCurrentLatLng().latitude;
    private double lng =  MapsActivity.getCurrentLatLng().longitude;
    private String url = String.format("http://87.96.251.140:8080/API");
    private String urlLatLng = String.format("http://87.96.251.140:8080/API/lat%1$s/lng%2$s", String.valueOf(lat), String.valueOf(lng));
    private static boolean v = false;
    private static boolean b = false;

    public VolleyHandler() {
        v = false;
        b = false;
        //url = getString(R.string.apiURL);
    }

    public List<CampsiteModel> getCampList() {
        Log.d("returning: ", "campsites array");
        return campsites;
    }

    public List<Comment> getCommentList() {
        Log.d("COMMENTLOADER", "returning: comment array");
        return comments;
    }

 /*   public List oldJSON(JSONArray array) {
        List<CampsiteModel> campList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObj = array.getJSONObject(i);
                CampsiteModel cm = new CampsiteModel(
                        jsonObj.getString("id"),
                        jsonObj.getString("location"),
                        jsonObj.getString("coordinates"),
                        jsonObj.getString("type"),
                        jsonObj.getString("fee"),
                        jsonObj.getInt("capacity"),
                        jsonObj.getString("availability"),
                        jsonObj.getString("description"));
                campList.add(cm);
                Log.d("fromJSON: ", "created object");
            } catch (JSONException e) {
                Log.d("fromJSON Exception: ", e.getMessage());
            }
        }

        Log.d("fromJSON: ", "Returning List of " + campList.size());
        return campList;
    }*/
}
