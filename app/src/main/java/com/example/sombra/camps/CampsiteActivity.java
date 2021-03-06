package com.example.sombra.camps;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.sombra.camps.CommentLoader.CommentChangeListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Activity responsible for displaying the chosen campsite. It also have the options of deleting
 * the campsite if the user is the creator and rate the campsite. The comments
 */
public class CampsiteActivity extends AppCompatActivity {
    TextView upVotes;
    TextView downVotes;
    Button deleteCM;

    CommentLoader cl = null;
    CampsiteModel cm;

    private CommentChangeListener listener;
    private RatingChangeListener ratingListener;

    private List<CommentModel> comments;
    private String url;
    private Context context;
    private Gson gson;

    /**
     * onCreate() sets the view for the activity and gets the URL from resource.
     * We set two listeners, one for comment changes and one for rating changes.
     * We create a Gson object and set context to this for future use in the class.
     * Finally it runs init().
     *
     * @param savedInstanceState The standard Bundle from previous class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite);
        url = SessionSingleton.getURL();

        listener = new CommentChangeListener() {
            @Override
            public void onCommentChangeList(List<CommentModel> cList) {
                Log.d("COMMENTLOADER: ", "RESETTING LIST");
                cl.resetListView(cList);
            }
        };

        ratingListener = new RatingChangeListener() {
            @Override
            public void onRatingChangeList(List<RatingModel> ratingList) {
                Log.d("CAMPSITE ACTIVITY: ", "RESETTING RATINGLIST");
                resetRating(ratingList);
            }
        };

        gson = new Gson();
        context = this;

        init();
    }

    /**
     * If the back button is pressed the activity finishes.
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * init() retrieves the campsiteModel to be displayed from the Bundle.
     * It sets the campsiteView by calling {@link #setCampsiteView(CampsiteModel)}
     * and if there is comments or ratings it also retrieves them from the databse through
     * getComments() and getRating().
     *
     * Finally it sets the toolbar for the view.
     */
    public void init() {
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

    /**
     * setCampsiteView() populates the different views with the data from the campsiteModel.
     * RatingModel and comment button is created and set visible depending on the user, if its a guest or
     * the user that created the campsite.
     *
     * The method also creates the AlertDialog handling the deletion of the campsite.
     *
     * @param cm CampsiteModel with the data about the campsite.
     */
    private void setCampsiteView(final CampsiteModel cm) {
        TextView nameView = (TextView) findViewById(R.id.name);
        TextView locationView = (TextView) findViewById(R.id.location);
        TextView typeView = (TextView) findViewById(R.id.type);
        TextView feeView = (TextView) findViewById(R.id.fee);
        TextView capacityView = (TextView) findViewById(R.id.capacity);
        TextView availabilityView = (TextView) findViewById(R.id.availability);
        TextView descriptionView = (TextView) findViewById(R.id.description);

        nameView.setText(cm.name);
        locationView.setText(cm.location);
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

        if(SessionSingleton.getId() != null && SessionSingleton.getId().equals(cm.userId)) {
            deleteCM.setVisibility(View.VISIBLE);
        } else {
            deleteCM.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * resetRating() sets the rating and if there is no rating in the databse it is set to 0.
     *
     * @param ratingList RatingModel list containing the ratings.
     */
    private void resetRating(List<RatingModel> ratingList) {
        upVotes = (TextView) findViewById(R.id.positiveRating);
        downVotes = (TextView) findViewById(R.id.negativeRating);

        int positive = 0;
        int negative = 0;

        for (RatingModel r : ratingList) {
            if(r.getRating() == 1) {
                positive++;
            } else {
                negative++;
            }
        }

        upVotes.setText("" + positive);
        downVotes.setText("" + negative);
    }

    /**
     * getRating() retrieves the ratings from the databse and puts them in our List of ratings through a HttpGetRequest.
     */
    private void getRating() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url + "?type=rating&campsiteId='" + cm.id + "'",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        List<RatingModel> list = ratingsFromJSON(array);
                        ratingListener.onRatingChangeList(list);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request rat cause: ", error.toString());
                if(error.networkResponse.statusCode == 404) {
                    Toast.makeText(SessionSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);

    }

    /**
     * postRating() handles the posting of a new rating through a HttpPostRequest.
     * If the user is not logged in it is asked to log in with prompLogin().
     *
     * @param rate RatingModel to be posted to the databse.
     */
    private void postRating(int rate) {
        if(LoginActivity.promptLogin("rate", context)) {
            return;
        }

        RatingModel ratingModel = new RatingModel(SessionSingleton.getId(), rate);

        Log.d("UserId: ", ratingModel.getUserId());
        Log.d("RatingModel: ", "" + ratingModel.getRating());

        String jo = gson.toJson(ratingModel);

        GenericRequest gr = new GenericRequest(
                Request.Method.POST,
                url + "?type=ratingModel&campsiteId='" + cm.id + "'",
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

    /**
     * deleteCampsite() deletes the selected campsite from the databse through a HttpDeleteRequest.
     *
     * @param cm Campsite to be deleted.
     */
    private void deleteCampsite(CampsiteModel cm) {
        GenericRequest gr = new GenericRequest(
                Request.Method.DELETE,
                url + "?type=campsite&campsiteId=" + cm.id,
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

    /**
     * updateCampsite() updates the databse with a updated campsite object.
     * We only use it for updating the total views of a campsite but it's dynamic
     * and could be used for an unlimited number of extraordinary campsite updates.
     *
     * @param property The property to be updated.
     * @param cm The campsite to be updated.
     */
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

    /**
     * ratingsFromJSON() translates the ratings retrieved from the databas from JSONArray to ArrayList.
     *
     * @param array JSONArray to be translated.
     * @return The finished list of ratings.
     */
    public List ratingsFromJSON(JSONArray array) {
        List<RatingModel> ratingList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObj = array.getJSONObject(i);
                RatingModel cm = new RatingModel(
                        jsonObj.getString("userId"),
                        jsonObj.getInt("rating"));
                ratingList.add(cm);

            } catch (JSONException e) {
                Log.d("fromJSON Exception: ", e.getMessage());
            }
        }

        return ratingList;
    }

    /**
     * Interface for listening to changes in the ratings list.
     */
    public interface RatingChangeListener {
        void onRatingChangeList(List<RatingModel> members);
    }

}
