package com.example.sombra.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles all methods revolving around logging in and the actual LoginActivity.
 */
public class LoginActivity extends AppCompatActivity {
    Long back_pressed = 0L;

    /**
     * onCreate() sets the view for the activity.
     * SessionSingletons appContext and servlett URL is also set here.
     *
     * @param savedInstanceState The standard Bundle from previous class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SessionSingleton.setAppContext(this.getApplicationContext());
        SessionSingleton.setURL(this.getResources().getString(R.string.apiURL));
    }

    /**
     * If the back button is pressed once the user is warned
     * that one more press will exit the app.
     * A second press within 1500 milliseconds will exit the app.
     */
    @Override
    public void onBackPressed() {
        if (back_pressed + 1500 > System.currentTimeMillis()) {
            super.onBackPressed();
            System.exit(0);
        }
        else {
            Utils.toast("Press once again to exit!", "short");
        }
        back_pressed = System.currentTimeMillis();
    }

    /**
     * register() starts the RegisterActivity.
     *
     * @param view Needed for onClick() usage.
     */
    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * guest() lets the user use the app as a guest.
     * It limits the functions available but still permits looking at campsites.
     * This is handled by setting the Username to 'guest'.
     * It then starts the LandingActivity.
     *
     * @param view Needed for onClick() usage.
     */
    public void guest(View view) {
        if(SessionSingleton.getUsername().equals("guest")) {
            SessionSingleton.setPromptLogin(true);
        }
        Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * checkUser() handles the check of the user input when logging in.
     * It checks the Strings with our checkString() and if succesful
     * it will run login which handles the rest of the login process.
     *
     * @param view Needed for onClick() usage.
     */
    public void checkUser(View view) {
        EditText un = (EditText) findViewById(R.id.input_username);
        EditText pw = (EditText) findViewById(R.id.input_password);

        if (Utils.checkString(un.getText().toString(), "Username", 0 , 0) &&
                Utils.checkString(pw.getText().toString(), "Password", 0, 0)) {
            login(un.getText().toString(), pw.getText().toString(), this);
        }
    }

    /**
     * login() checks whether the username and password exists against the database
     * with a httpGetRequest.
     *
     * If the user exists we set the SessionsSingletons id to the users id and the same
     * with username.
     *
     * Finally we run checkActivity().
     *
     * @param username The username to check.
     * @param pw       The password to check.
     * @param context  The context to send to checkActivity().
     */
    public void login(String username, String pw, Context context) {
        final Context c = context;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                SessionSingleton.getURL() + "?type=user&username='" + username + "'&password='" + pw + "'",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            SessionSingleton.setId(response.getString("id"));
                            SessionSingleton.setUsername(response.getString("username"));
                        } catch(JSONException e) {
                            Utils.toast(e.toString(), "short");
                        }
                        checkActvity(c);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.toast("That user does not exist or the password is incorrect", "short");
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * promptLogin() handles the login function further into the app.
     * If the user tries to use a function not available to a guest
     * the promptLogin method will be triggered.
     *
     * It builds a AlertDialog telling the user it needs to login and asking
     * the user if it wants to do it now.
     * If the answer is yes the loginWindow() will run handling the actual login.
     * If the answer is no the user will no longer be asked to login if it tries
     * another unavailable function.
     *
     * @param activity Where the user is currently in the app.
     * @param context  For the dialog Builder and loginWindow().
     * @return True if the answer is yes and false if it is no.
     */
    public static boolean promptLogin(String activity, Context context) {
        final Context c = context;

        if (SessionSingleton.getUsername().equals("guest") && SessionSingleton.getPromptLogin() == true) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("You are not logged in");
            builder.setMessage("You need to log in to " + activity + ". Do you want to log in?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    LoginActivity la = new LoginActivity();
                    la.loginWindow(c);
                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    SessionSingleton.setPromptLogin(false);
                    return;
                }
            });
            builder.create();
            builder.show();
            return true;
        } else if (SessionSingleton.getUsername().equals("guest")) {
            Utils.toast("You need to log in to " + activity, "short");
            return true;
        } else {
            return false;
        }
    }

    /**
     * loginWindow() is prompted if the user wants to login from the promptLogion() AlertDialog.
     * It creates a new AlertDialog asking for the user info needed for logging in.
     * The same method used for login in the loginActivity is also used here.
     *
     * @param context Context for the dialog Builder.
     */
    public void loginWindow(final Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView tv = new TextView(context);
        tv.setText("Login");
        tv.setTextSize(20);
        layout.addView(tv);

        final EditText titleBox = new EditText(context);
        titleBox.setHint("Username");
        layout.addView(titleBox);

        final EditText descriptionBox = new EditText(context);
        descriptionBox.setHint("Password");
        layout.addView(descriptionBox);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (Utils.checkString(titleBox.getText().toString(), "Username", 0 , 0) &&
                        Utils.checkString(descriptionBox.getText().toString(), "Password", 0, 0)) {
                    login(titleBox.getText().toString(), descriptionBox.getText().toString(), context);
                }
        }});

        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SessionSingleton.setPromptLogin(false);
                return;
        }});

        dialog.setView(layout);
        dialog.show();
    }

    /**
     * checkActivity() is needed because we wanted to only start LandingActivity
     * if the user is logging in from LoginActivity.
     *
     * If the user is logging in from anywhere else we only want to close the
     * AlertDialog and let the user continue from where it was, now logged in.
     *
     * @param context Context to check if the context is an instance of LoginActvity.
     */
    public void checkActvity(Context context) {
        if(context instanceof LoginActivity) {
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
            startActivity(intent);
            finish();
        } else {
            Utils.toast("You are now logged in!", "short");
        }
    }
}
