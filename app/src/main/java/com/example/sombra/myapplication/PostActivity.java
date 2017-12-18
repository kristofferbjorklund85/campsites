package com.example.sombra.myapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    static LatLng latLng = null;

    private String url = String.format("http://87.96.251.140:8080/API");

    EditText ln;
    EditText type;
    EditText fee;
    EditText cap;
    EditText avail;
    EditText desc;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Button bt = (Button)findViewById(R.id.ButtonCreateCampsite);

        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createCampsite();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            return;
        }
        latLng = extras.getParcelable("latlng");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void createCampsite() {

        ln    = (EditText)findViewById(R.id.EditTextLocationName);
        type  = (EditText)findViewById(R.id.EditTextType);
        fee   = (EditText)findViewById(R.id.EditTextFee);
        cap   = (EditText)findViewById(R.id.EditTextCapacity);
        avail = (EditText)findViewById(R.id.EditTextAvailability);
        desc  = (EditText)findViewById(R.id.EditTextDescription);
        name  = (EditText)findViewById(R.id.EditTextName);

        Log.d("Username ", User.getUsername());

        CampsiteModel cm = new CampsiteModel(UUID.randomUUID().toString(),
                                            ln.getText().toString(),
                                            name.getText().toString(),
                                            latLng.latitude,
                                            latLng.longitude,
                                            type.getText().toString(),
                                            fee.getText().toString(),
                                            cap.getText().toString(),
                                            avail.getText().toString(),
                                            desc.getText().toString(),
                                            0.0,
                                            1,
                                            User.getUsername());

        postCampsites(this, cm);
        MapsActivity.setNewCM(cm);
        finish();
    }

    public void postCampsites(Context context, CampsiteModel cm) {
        JSONObject jo = toJSON(cm);

        JsonObjectRequest joReq = new JsonObjectRequest(
                Request.Method.POST,
                url + "?type=campsite",
                jo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(User.getAppContext(), "Campsite posted!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(User.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(joReq);
    }

    public static JSONObject toJSON(CampsiteModel cm) {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("id", cm.id);
            jsonObj.put("location", cm.location);
            jsonObj.put("name", cm.name);
            jsonObj.put("lat", cm.lat);
            jsonObj.put("lng", cm.lng);
            jsonObj.put("type", cm.type);
            jsonObj.put("fee", cm.fee);
            jsonObj.put("capacity", cm.capacity);
            jsonObj.put("availability", cm.availability);
            jsonObj.put("description", cm.description);
            jsonObj.put("rating", cm.rating);
            jsonObj.put("views", cm.views);
            jsonObj.put("username", cm.username);
        } catch(JSONException e) {
            Log.d("toJSON obj", e.toString());
        }
        return jsonObj;
    }
}
