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

import java.util.ArrayList;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    static LatLng latLng = null;

    private String url;

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

        url = this.getResources().getString(R.string.apiURL);
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


        if( Utils.checkString(ln.getText().toString(), "Location name", 3, 30) &&
            Utils.checkString(name.getText().toString(), "Campsite name", 3, 20) &&
            Utils.checkString(type.getText().toString(), "Type", 3, 20) &&
            Utils.checkString(fee.getText().toString(), "Fee", 3, 30) &&
            Utils.checkString(cap.getText().toString(), "Capacity", 3, 30) &&
            Utils.checkString(avail .getText().toString(), "Availability", 3, 20) &&
            Utils.checkString(desc.getText().toString(), "Description", 3, 100)) {
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
                        //Notera att views är 1
                        1,
                        SessionSingleton.getId());

                postCampsites(this, cm);
                MapsActivity.setNewCM(cm);
                finish();
        }
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
                        Toast.makeText(SessionSingleton.getAppContext(), "Campsite posted!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SessionSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(joReq);
    }

    public static JSONObject toJSON(CampsiteModel cm) {
        JSONObject jo = new JSONObject();

        try {
            jo.put("id", cm.id);
            jo.put("location", cm.location);
            jo.put("name", cm.name);
            jo.put("lat", cm.lat);
            jo.put("lng", cm.lng);
            jo.put("type", cm.type);
            jo.put("fee", cm.fee);
            jo.put("capacity", cm.capacity);
            jo.put("availability", cm.availability);
            jo.put("description", cm.description);
            jo.put("views", cm.views);
            jo.put("username", cm.userId);
        } catch(JSONException e) {
            Log.d("toJSON obj", e.toString());
        }
        return jo;
    }
}
