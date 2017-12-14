package com.example.sombra.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sombra on 2017-11-28.
 */

public class CommentLoader {

    private Context context;
    private ListView commentsListView;
    private CampsiteModel cm;
    private String url = String.format("http://87.96.251.140:8080/API");
    CommentChangeListener listener;

    public CommentLoader(Context context, ListView listview, CampsiteModel cm, CommentChangeListener listener) {
        this.context = context;
        commentsListView = listview;
        this.cm = cm;
        listeners = new ArrayList<>();
        this.listener = listener;
    }


    public void resetListView(List<Comment> cList) {
        ListView comments = commentsListView;
        CommentAdapter adapter = new CommentAdapter(context,
                R.layout.comments_listitem, cList);
        comments.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(comments, adapter);
    }


    public void getComments(Context context) {
        Log.d("COMMENTLOADER", "Entering GetComments");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=comment&campsiteid='" + cm.id + "'",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        Log.d("COMMENTLOADER", "on Response: setting comments");
                        List<Comment> list = commentsFromJSON(array);
                        listener.onCommentChangeList(list);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.getCause().getMessage());
                Log.d("COMMENTLOADER: ", "ERROR RESPONSE");
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);

    }

    public List commentsFromJSON(JSONArray array) {
        List<Comment> cList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObj = array.getJSONObject(i);
                Comment cm = new Comment(
                        jsonObj.getString("id"),
                        jsonObj.getString("campsiteid"),
                        jsonObj.getString("date"),
                        jsonObj.getString("username"),
                        jsonObj.getString("commentbody"));
                cList.add(cm);
                Log.d("fromJSON: ", "created object");
            } catch (JSONException e) {
                Log.d("fromJSON Exception: ", e.getMessage());
            }
        }

        Log.d("fromJSON: ", "Returning List of " + cList.size());
        return cList;
    }

    private class CommentAdapter extends ArrayAdapter<Comment> {

        Context context;
        int layoutResourceId;
        List<Comment> data = null;


        public CommentAdapter(Context context, int layoutResourceId, List<Comment> data) {
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

            holder.user.setText(data.get(position).username + ":");
            holder.comment.setText(data.get(position).commentBody);

            return row;
        }

    }

    private class CommentHolder {
        TextView user;
        TextView comment;
    }

    public void justifyListViewHeightBasedOnChildren (ListView listView, CommentAdapter adapter) {

        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    //LISTENERS FROM HENRIK

    private List<CommentChangeListener> listeners;

    public interface CommentChangeListener {
        void onCommentChangeList(List<Comment> members);
    }

    public void addCommentChangeListener(CommentChangeListener l) {
        listeners.add(l);
    }


}
