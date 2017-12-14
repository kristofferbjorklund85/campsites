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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CampsiteActivity extends AppCompatActivity {

    public static String ERROR;
    CommentLoader cl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Maps");
        setSupportActionBar(toolbar);
        init(this);
    }


    public void init(final Context context) {
        ERROR = CampsiteActivity.class.getSimpleName() + getString(R.string.ERROR);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("Extras: ", " NULL");
            return;
        }

        setCampsiteView((CampsiteModel) extras.getParcelable("cm"));

        Button button = (Button) findViewById(R.id.comment_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new CommentDialog();
                newFragment.show(getFragmentManager(), "comment");
            }
        });

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

        CommentLoader cl = new CommentLoader();
        cl.loadComments(this, (ListView) findViewById(R.id.comments_listview), cm);
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
