package com.example.sombra.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sombra.myapplication.CommentLoader.CommentChangeListener;

public class CampsiteActivity extends AppCompatActivity {

    public static String ERROR;
    CommentLoader cl = null;
    CampsiteModel cm;
    private CommentChangeListener listener;
    private List<Comment> comments;
    Context me;
    private String url;

    Button deleteCM;

    /*CampsiteModel camp = new CampsiteModel(
            "04ef19e0-db39-47ed-9e2c-6605e06e2d7c",
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
            12312);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite);

        url = this.getResources().getString(R.string.apiURL);
        me = this;

        listener = new CommentChangeListener() {
            @Override
            public void onCommentChangeList(List<Comment> cList) {
                Log.d("COMMENTLOADER: ", "RESETTING LIST");
                cl.resetListView(cList);
            }
        };

        init();
    }

    public void init() {
        //ERROR = CampsiteActivity.class.getSimpleName() + getString(R.string.ERROR);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("Extras: ", " NULL");
            return;
        }

        cm = (CampsiteModel) extras.getParcelable("cm");
        setCampsiteView(cm);

        Button button = (Button) findViewById(R.id.comment_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new CommentDialog(me, cl, cm);
                newFragment.show(getFragmentManager(), "comment");
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Campsite");
            builder.setMessage("Are you sure you want to delete campsite " + cm.name + "?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    deleteCampsite(cm);
                }});
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    return;
                }});
            builder.create();

        deleteCM = (Button) findViewById(R.id.delete_button);
        deleteCM.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                builder.show();
            }

        });

        Log.d("Username: ", User.getUsername());
        Log.d("CM Username: ", cm.username);

        if(User.getUsername().equals(cm.username)) {
            deleteCM.setVisibility(View.VISIBLE);
        } else {
            deleteCM.setVisibility(View.INVISIBLE);
        }

        cl = new CommentLoader(me, (ListView) findViewById(R.id.comments_listview), cm, listener);

        comments = new ArrayList<>();
        cl.resetListView(comments);
        cl.getComments();
        updateCampsite("views", cm);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(cm.location);
        setSupportActionBar(toolbar);
    }

    public void setCampsiteView(CampsiteModel cm) {
        TextView locationView = (TextView) findViewById(R.id.location);
        TextView typeView = (TextView) findViewById(R.id.type);
        TextView feeView = (TextView) findViewById(R.id.fee);
        TextView capacityView = (TextView) findViewById(R.id.capacity);
        TextView availabilityView = (TextView) findViewById(R.id.availability);
        TextView descriptionView = (TextView) findViewById(R.id.description);

        locationView.setText("Location: " + cm.location);
        typeView.setText("Type: " + cm.type);
        feeView.setText("Fee: " + cm.fee);
        capacityView.setText("Capacity: " + cm.capacity);
        availabilityView.setText("Availability: " + cm.availability);
        descriptionView.setText("Description: " + cm.description);

    }

    public void deleteCampsite(CampsiteModel cm) {

        GenericRequest gr = new GenericRequest(
                Request.Method.DELETE,
                url + "?type=campsite&campsiteId='" + cm.id + "'",
                String.class,
                "",
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(me, "Campsite Deleted!", Toast.LENGTH_LONG).show();
                        MapsActivity.setDeleteM();
                        finish();
                        Log.d("CampsiteActivity", "on Repsonse DELETE: Campsite was deleted");
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DELETE-request cause", error.toString());
                        Log.d("CAMPSITEACTIVITY: ", "ERROR RESPONSE FROM DELETECAMPSITE");
                        Toast.makeText(me, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(gr);
    }

    public void updateCampsite(String property, CampsiteModel cm) {

        GenericRequest gr = new GenericRequest(
                Request.Method.PUT,
                url + "?type=" + property + "&campsiteId=" + cm.id,
                String.class,
                "",
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Log.d("CampsiteActivity", "on Repsonse PUT: Campsite was updated");
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("PUT-request cause", error.toString());
                        Log.d("CAMPSITEACTIVITY: ", "ERROR RESPONSE FROM UPDATECAMPSITE");
                        Toast.makeText(me, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(gr);
    }



   /*
    public CampsiteModel createCampsite(String jsonStr) {

    String test_JSON = getString(R.string.test_JSON);

        JSONObject jsonObj = null;

        try {
            jsonObj = new JSONObject(jsonStr);
        } catch (JSONException e) {
            Log.d("error 1", e.getMessage());
        }

        try {
            return new CampsiteModel(
                    jsonObj.getString("id"),
                    jsonObj.getString("location"),
                    jsonObj.getDouble("lat"),
                    jsonObj.getDouble("lng"),
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
    */

}
