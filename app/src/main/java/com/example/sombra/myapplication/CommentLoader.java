package com.example.sombra.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

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
    private String url;
    private Gson gson;

    CommentChangeListener listener;

    public CommentLoader(Context context, ListView listview, CampsiteModel cm, CommentChangeListener listener) {
        this.context = context;
        commentsListView = listview;
        this.cm = cm;
        listeners = new ArrayList<>();
        this.listener = listener;

        gson = new Gson();
        url = context.getResources().getString(R.string.apiURL);
    }


    public void resetListView(List<Comment> cList) {
        ListView comments = commentsListView;
        CommentAdapter adapter = new CommentAdapter(context,
                R.layout.comments_listitem, cList);
        comments.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(comments, adapter);
    }


    public void getComments() {
        Log.d("COMMENTLOADER", "Entering GetComments");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=comment&campsiteid='" + cm.id + "'",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        Log.d("COMMENTLOADER", "on Response: setting comments 1/2");
                        List<Comment> list = commentsFromJSON(array);
                        listener.onCommentChangeList(list);
                        Log.d("COMMENTLOADER", "on Response: setting comments 2/2");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.toString());
                Log.d("COMMENTLOADER: ", "ERROR RESPONSE FROM GETCOMMENTS");
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

    public void postComment(Context context, Comment cm) {
        String jo = gson.toJson(cm);

        GenericRequest gr = new GenericRequest(
                Request.Method.POST,
                url + "?type=comment&campsiteid='" + cm.id + "'",
                String.class,
                jo,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Log.d("COMMENTLOADER", "on Repsonse POST: sent Comment 1/2");
                        getComments();
                        Log.d("COMMENTLOADER", "on Repsonse POST: sent Comment 2/2");
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("POST-request cause", error.toString());
                        Log.d("COMMENTLOADER: ", "ERROR RESPONSE FROM POSTCOMMENTS");
                    }
                });

        VolleySingleton.getInstance(context).addToRequestQueue(gr);

    }


    /*public void postComment(Context context, Comment cm) {
        JSONObject jo = toJSON(cm);

        JsonObjectRequest joReq = new JsonObjectRequest(
                Request.Method.POST,
                url + "?type=comment&campsiteid='" + cm.id + "'",
                jo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("COMMENTLOADER", "Response: " + response.toString());
                        Log.d("COMMENTLOADER", "on Repsonse POST: sent Comment 1/2");
                        getComments();
                        Log.d("COMMENTLOADER", "on Repsonse POST: sent Comment 2/2");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("POST-request cause", error.getCause().getMessage());
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(joReq);
    }*/

    /*public static JSONObject toJSON(Comment c) {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("id", c.id);
            jsonObj.put("campsiteId", c.campsiteId);
            jsonObj.put("date", c.date);
            jsonObj.put("username", c.username);
            jsonObj.put("commentBody", c.commentBody);
        } catch(JSONException e) {
            Log.d("toJSON obj", e.toString());
        }

        return jsonObj;
    }*/


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
                holder.deleteComment = (Button)row.findViewById(R.id.deleteComment);
                holder.deleteComment.setVisibility(View.INVISIBLE);

                if(User.getUsername().equals(data.get(position).username)) {
                    holder.deleteComment.setVisibility(View.VISIBLE);

                    Log.d("GetView: ", "CURRENT USER: " + User.getUsername());
                    Log.d("GetView: ", "DATA.GET USER: " + data.get(position).username);
                }


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
        Button deleteComment;
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
