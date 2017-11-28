package com.example.sombra.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Maps");
        setSupportActionBar(toolbar);
        init();

    }

    public void init() {
        ERROR = CampsiteActivity.class.getSimpleName() + getString(R.string.ERROR);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("Extras: ", " NULL");
            return;
        }

        setCampsiteView((CampsiteModel) extras.getParcelable("cm"));
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

        loadComments();
    }

    public void loadComments() {

        Comment[] commentsArray = {
                new Comment(User.getUsername(), "This is great campsite, many friendly people"),
                new Comment(User.getUsername(), "Too many germans, 0/10"),
                new Comment(User.getUsername(), "BUY VIAGRA DELUXE NICE PRICE")};


        ListView comments = (ListView) findViewById(R.id.comments_listview);
        CommentAdapter adapter = new CommentAdapter(this,
                R.layout.comments_listitem, commentsArray);
        comments.setAdapter(adapter);

    }

    private class CommentAdapter extends ArrayAdapter<Comment> {

        Context context;
        int layoutResourceId;
        Comment data[] = null;


        public CommentAdapter(Context context, int layoutResourceId, Comment[] data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            CommentHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new CommentHolder();

                holder.user = (TextView)row.findViewById(R.id.comment_user);
                holder.comment = (TextView)row.findViewById(R.id.comment_textview);


                row.setTag(holder);

            } else {
                holder = (CommentHolder)row.getTag();
            }

            holder.user.setText(data[position].username + ":");
            holder.comment.setText(data[position].comment);


            return row;
        }
        

    }

    static class CommentHolder
    {
        TextView user;
        TextView comment;
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
