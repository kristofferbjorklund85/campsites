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

    public void getCampsites(Context context, LatLng latLng) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=campsite",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        campsites = fakeJSON(array);
                        Log.d("on Response: ", "setting campsites");
                        v = true;
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.getCause().getMessage());
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
        while(v == false) {}
    }

    public void getComments(Context context) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=comment",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        Log.d("COMMENTLOADER", "on Response: setting comments");
                        b = true;
                        comments = fakeJSON(array);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.getCause().getMessage());
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
        Log.d("COMMENTLOADER", "While: Started");
        while(b == false) {}
        Log.d("COMMENTLOADER", "While: Done");
    }

    public void postCampsites(Context context, CampsiteModel cm) {
        JSONObject jo = toJSON(cm);

        JsonObjectRequest joReq = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("on Repsonse POST: ", "sent Campsite");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("POST-request cause", error.getCause().getMessage());
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(joReq);
    }

    public static JSONObject toJSON(CampsiteModel cm) {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("id", cm.id);
            jsonObj.put("location", cm.location);
            jsonObj.put("lat", cm.lat);
            jsonObj.put("lng", cm.lng);
            jsonObj.put("type", cm.type);
            jsonObj.put("fee", cm.fee);
            jsonObj.put("capacity", cm.capacity);
            jsonObj.put("availability", cm.availability);
            jsonObj.put("description", cm.description);
        } catch(JSONException e) {
            Log.d("toJSON obj", e.toString());
        }

        return jsonObj;
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
                        jsonObj.getInt("views"));
                campList.add(cm);
                Log.d("fromJSON: ", "created object");
            } catch (JSONException e) {
                Log.d("fromJSON Exception: ", e.getMessage());
            }
        }

        Log.d("fromJSON: ", "Returning List of " + campList.size());
        return campList;
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
