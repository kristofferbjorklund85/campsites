package com.example.sombra.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    Long back_pressed = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SessionSingleton.setAppContext(this.getApplicationContext());
        SessionSingleton.setURL(this.getResources().getString(R.string.apiURL));

        //Remove for production
        if (false) {
            SessionSingleton.setUsername("JanBanan");
            SessionSingleton.setAppContext(this.getApplicationContext());
            Log.d("starting: ", "Landing");
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 1500 > System.currentTimeMillis()){
            super.onBackPressed();
            System.exit(0);
        }
        else{
            Utils.toast("Press once again to exit!", "short");
        }
        back_pressed = System.currentTimeMillis();
    }

    public void login(View view) {
        EditText un = (EditText) findViewById(R.id.input_username);
        EditText pw = (EditText) findViewById(R.id.input_password);
        if (Utils.checkString(un.getText().toString(), "Username", 0 , 0) &&
                Utils.checkString(pw.getText().toString(), "Password", 0, 0)) {
            userExists(un.getText().toString(), pw.getText().toString(), this);
        }
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void guest(View view) {
        if(SessionSingleton.getUsername().equals("guest")) {
            SessionSingleton.setPromptLogin(true);
        }
        Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
        //Continue using app as guest
    }

    public void userExists(String username, String pw, Context context) {
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
                    userExists(titleBox.getText().toString(), descriptionBox.getText().toString(), context);
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
