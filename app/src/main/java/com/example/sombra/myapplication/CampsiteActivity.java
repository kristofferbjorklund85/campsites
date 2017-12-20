package com.example.sombra.myapplication;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Rating;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.sombra.myapplication.CommentLoader.CommentChangeListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CampsiteActivity extends AppCompatActivity {

    TextView upVotes;
    TextView downVotes;
    CommentLoader cl = null;
    CampsiteModel cm;
    private CommentChangeListener listener;
    private RatingChangeListener ratingListener;
    private List<Comment> comments;
    private String url;
    private Context context;
    private Gson gson;

    Button deleteCM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite);
        url = SessionSingleton.getURL();

        listener = new CommentChangeListener() {
            @Override
            public void onCommentChangeList(List<Comment> cList) {
                Log.d("COMMENTLOADER: ", "RESETTING LIST");
                cl.resetListView(cList);
            }
        };

        ratingListener = new RatingChangeListener() {
            @Override
            public void onRatingChangeList(List<com.example.sombra.myapplication.Rating> ratingList) {
                Log.d("CAMPSITE ACTIVITY: ", "RESETTING RATINGLIST");
                resetRating(ratingList);
            }
        };

        gson = new Gson();
        context = this;

        init();
    }

    @Override
    public void onBackPressed() {
        finish();
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

        cl = new CommentLoader(context, (ListView) findViewById(R.id.comments_listview), cm, listener, url);

        comments = new ArrayList<>();
        cl.resetListView(comments);
        cl.getComments();
        updateCampsite("views", cm);

        getRating();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(cm.location);
        setSupportActionBar(toolbar);
    }

    private void setCampsiteView(final CampsiteModel cm) {
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

        ImageButton upVoteButton = (ImageButton) findViewById(R.id.upVoteButton);
        upVoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postRating(1);
            }
        });
        
        ImageButton downVoteButton = (ImageButton) findViewById(R.id.downVoteButton);
        downVoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postRating(0);
            }
        });

        Button commentButton = (Button) findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!LoginActivity.promptLogin("comment", context)) {
                    DialogFragment newFragment = new CommentDialog(SessionSingleton.getAppContext(), cl, cm);
                    newFragment.show(getFragmentManager(), "comment");
                }
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

        if(SessionSingleton.getId() != null && SessionSingleton.getId().equals(cm.id)) {
            deleteCM.setVisibility(View.VISIBLE);
        } else {
            deleteCM.setVisibility(View.INVISIBLE);
        }
    }

    private void resetRating(List<com.example.sombra.myapplication.Rating> ratingList) {
        upVotes = (TextView) findViewById(R.id.positiveRating);
        downVotes = (TextView) findViewById(R.id.negativeRating);

        int positive = 0;
        int negative = 0;

        for (com.example.sombra.myapplication.Rating r : ratingList) {
            if(r.getRating() == 1) {
                positive++;
            } else {
                negative++;
            }
        }

        upVotes.setText("" + positive);
        downVotes.setText("" + negative);
    }

    private void getRating() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=rating&campsiteId='" + cm.id + "'",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        List<com.example.sombra.myapplication.Rating> list = ratingsFromJSON(array);
                        ratingListener.onRatingChangeList(list);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.toString());
                Toast.makeText(SessionSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);

    }

    private void postRating(int rate) {
        if(LoginActivity.promptLogin("rate", context)) {
            return;
        }

        com.example.sombra.myapplication.Rating rating = new com.example.sombra.myapplication.Rating(SessionSingleton.getId(), rate);

        Log.d("UserId: ", rating.getUserId());
        Log.d("Rating: ", "" + rating.getRating());

        String jo = gson.toJson(rating);

        GenericRequest gr = new GenericRequest(
                Request.Method.POST,
                url + "?type=rating&campsiteId='" + cm.id + "'",
                String.class,
                jo,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SessionSingleton.getAppContext(), "Campsite Rated!", Toast.LENGTH_SHORT).show();
                        getRating();
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

    private void deleteCampsite(CampsiteModel cm) {

        GenericRequest gr = new GenericRequest(
                Request.Method.DELETE,
                url + "?type=campsite&campsiteId='" + cm.id + "'",
                String.class,
                "",
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SessionSingleton.getAppContext(), "Campsite Deleted!", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(SessionSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(gr);
    }

    private void updateCampsite(String property, CampsiteModel cm) {

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
                        Toast.makeText(SessionSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(gr);
    }

    public List ratingsFromJSON(JSONArray array) {
        List<com.example.sombra.myapplication.Rating> ratingList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObj = array.getJSONObject(i);
                com.example.sombra.myapplication.Rating cm = new com.example.sombra.myapplication.Rating(
                        jsonObj.getString("userId"),
                        jsonObj.getInt("rating"));
                ratingList.add(cm);

            } catch (JSONException e) {
                Log.d("fromJSON Exception: ", e.getMessage());
            }
        }

        return ratingList;
    }

    public interface RatingChangeListener {
        void onRatingChangeList(List<com.example.sombra.myapplication.Rating> members);
    }

}
