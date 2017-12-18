package com.example.sombra.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=comment&campsiteid='" + cm.id + "'",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        List<Comment> list = commentsFromJSON(array);
                        listener.onCommentChangeList(list);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.toString());
                Toast.makeText(User.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
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

            } catch (JSONException e) {
                Log.d("fromJSON Exception: ", e.getMessage());
            }
        }

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
                        Toast.makeText(User.getAppContext(), "Comment Posted!", Toast.LENGTH_SHORT).show();
                        getComments();
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("POST-request cause", error.toString());
                        Toast.makeText(User.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(context).addToRequestQueue(gr);

    }

    public void deleteComment(Comment c) {

        GenericRequest gr = new GenericRequest(
                url + "?type=comment&commentId=" + c.id + "",
                Request.Method.DELETE,
                String.class,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(User.getAppContext(), "Deleted Comment!", Toast.LENGTH_SHORT).show();
                        getComments();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DELETE-request cause", error.toString());
                Toast.makeText(User.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(gr);

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            CommentHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new CommentHolder();

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Comment");
                builder.setMessage("Are you sure you want to delete your comment ?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteComment(data.get(position));
                    }});

                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }});

                builder.create();


                holder.user = (TextView)row.findViewById(R.id.comment_user);
                holder.comment = (TextView)row.findViewById(R.id.comment_textview);
                holder.deleteComment = (Button)row.findViewById(R.id.deleteComment);
                holder.deleteComment.setVisibility(View.INVISIBLE);

                holder.deleteComment.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        builder.show();
                    }
                });

                if(User.getUsername().equals(data.get(position).username)) {
                    holder.deleteComment.setVisibility(View.VISIBLE);
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

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
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
