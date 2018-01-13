package com.example.sombra.camps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling comments for campsites.
 */
public class CommentLoader {

    private Context context;
    private ListView commentsListView;
    private CampsiteModel cm;
    private String url;
    private Gson gson;
    private CommentChangeListener listener;

    /**
     * Initializes the commentloader with necessary data to handle comments.
     *
     * @param context the context of the CampsiteActivity.
     * @param listview the listview that will hold all comments.
     * @param cm the Campsite to which the comments are posted
     * @param listener used for updating the list of comments
     * @param url the url to the API
     */
    public CommentLoader(Context context, ListView listview, CampsiteModel cm, CommentChangeListener listener, String url) {
        this.context = context;
        commentsListView = listview;
        this.cm = cm;
        this.listener = listener;
        this.url = url;

        gson = new Gson();
        //listeners = new ArrayList<>();
    }

    /**
     * Creates/Updates the Listview with comments.
     *
     * @param cList list of {@link CommentModel}-objects.
     */
    public void resetListView(List<CommentModel> cList) {
        ListView comments = commentsListView;
        CommentAdapter adapter = new CommentAdapter(context,
                R.layout.comments_listitem, cList);
        comments.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(comments, adapter);
    }

    /**
     * Get all comments posted on the Campsite the user is viewing.
     *
     * When the comments have been retrieved, the response is converted from JSON to a list of
     * CommentModel-objects and adds list to CommentChangeListener.
     *
     * Toasts the user if there is a problem getting comments. If the request is good, but there are
     * no comments in the database, the server responds with 404 and user is not toasted.
     */
    public void getComments() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=comment&campsiteid='" + cm.id + "'",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                            List<CommentModel> list = commentsFromJSON(array);
                            listener.onCommentChangeList(list);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request com cause: ", error.toString());
                if(error.networkResponse.statusCode != 404) {
                    Toast.makeText(SessionSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);

    }

    /**
     * Converts JSONarray of Comments to list with {@link CommentModel}-objects.
     *
     * @param array the JSONarray to be converted.
     * @return list of {@link CommentModel}-objects.
     */
    public List commentsFromJSON(JSONArray array) {
        List<CommentModel> cList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObj = array.getJSONObject(i);
                CommentModel cm = new CommentModel(
                        jsonObj.getString("id"),
                        jsonObj.getString("campsiteid"),
                        jsonObj.getString("date"),
                        jsonObj.getString("userId"),
                        jsonObj.getString("username"),
                        jsonObj.getString("commentbody"));
                cList.add(cm);

            } catch (JSONException e) {
                Log.d("fromJSON Exception: ", e.getMessage());
            }
        }

        return cList;
    }

    /**
     * Post-method for posting a comment to the API. Converts a {@link CommentModel}-object to JSON
     * using Google's {@link Gson}.
     *
     * Informs the user if the post was successful or not by toast.
     *
     * @param context the context of the CampsiteActivity.
     * @param cm the comment to be posted to the API.
     */
    public void postComment(Context context, CommentModel cm) {
        String jo = gson.toJson(cm);

        GenericRequest gr = new GenericRequest(
                Request.Method.POST,
                url + "?type=comment&campsiteid='" + cm.id + "'",
                String.class,
                jo,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SessionSingleton.getAppContext(), "CommentModel Posted!", Toast.LENGTH_SHORT).show();
                        getComments();
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("POST-request cause", error.toString());
                        Toast.makeText(SessionSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(context).addToRequestQueue(gr);

    }

    /**
     * Deletes a comment from the API.
     *
     * Informs the user if the post was successful or not by toast.
     *
     * @param c the comment to be deleted.
     */
    public void deleteComment(CommentModel c) {

        GenericRequest gr = new GenericRequest(
                url + "?type=comment&commentId=" + c.id + "",
                Request.Method.DELETE,
                String.class,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SessionSingleton.getAppContext(), "Deleted CommentModel!", Toast.LENGTH_SHORT).show();
                        getComments();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DELETE-request cause", error.toString());
                Toast.makeText(SessionSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(gr);

    }

    /**
     * Adapter for holding creating the list of comments as separate listitems. Class is instantiated
     * from {@link #resetListView(List)}.
     *
     * If the logged in user is the same as the author of a comment, a delete-button will be visible
     * in the listitem.
     */
    private class CommentAdapter extends ArrayAdapter<CommentModel> {

        Context context;
        int layoutResourceId;
        List<CommentModel> data = null;


        public CommentAdapter(Context context, int layoutResourceId, List<CommentModel> data) {
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
                builder.setTitle("Delete CommentModel");
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

                if(SessionSingleton.getUsername().equals(data.get(position).username)) {
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

    /**
     * Class for a comment-listitem.
     */
    private class CommentHolder {
        TextView user;
        TextView comment;
        Button deleteComment;
    }

    /**
     * Adjusts the height of the listview in order to enable scrolling of the entire page.
     * @param listView the listview that holds all comments.
     * @param adapter the adapter is needed to get the size of all individual listitems.
     */
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

    /**
     * Interface for listening to changes in the list of comments.
     */
    public interface CommentChangeListener {
        void onCommentChangeList(List<CommentModel> members);
    }

    /*
    private List<CommentChangeListener> listeners;

    public void addCommentChangeListener(CommentChangeListener l) {
        listeners.add(l);
    }*/
}
