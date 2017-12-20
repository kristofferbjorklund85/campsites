package com.example.sombra.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String url;
    Long back_pressed = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserSingleton.setAppContext(this.getApplicationContext());

        url = this.getResources().getString(R.string.apiURL);

        //Remove for production
        if (false) {
            UserSingleton.setUsername("JanBanan");
            UserSingleton.setAppContext(this.getApplicationContext());
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
            userExists(un.getText().toString(), pw.getText().toString());
        }
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void guest(View view) {
        Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
        //Continue using app as guest
    }

    public void userExists(String username, String pw) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url + "?type=user&username='" + username + "'&password='" + pw + "'",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserSingleton.setId(response.getString("id"));
                            UserSingleton.setUsername(response.getString("username"));
                        } catch(JSONException e) {
                            Utils.toast(e.toString(), "short");
                        }
                        Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET-request cause: ", error.toString());
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void promptLogin(String activity) {
        if(UserSingleton.getUsername().equals("guest") && UserSingleton.getPromptLogin() == true) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(UserSingleton.getAppContext());
            builder.setTitle("You are not logged in");
            builder.setMessage("You need to log in to " + activity + ". Do you want to log in?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    loginWindow();
                }});
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    UserSingleton.setPromptLogin(false);
                    return;
                }});
            builder.create();
        }
    }

    public void loginWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(UserSingleton.getAppContext());
        LinearLayout layout = new LinearLayout(UserSingleton.getAppContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText titleBox = new EditText(UserSingleton.getAppContext());
        titleBox.setHint("Login");
        layout.addView(titleBox);

        final EditText descriptionBox = new EditText(UserSingleton.getAppContext());
        descriptionBox.setHint("Username");
        layout.addView(descriptionBox);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (Utils.checkString(titleBox.getText().toString(), "Username", 0 , 0) &&
                        Utils.checkString(descriptionBox.getText().toString(), "Password", 0, 0)) {
                    userExists(titleBox.getText().toString(), descriptionBox.getText().toString());
                }
        }});

        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
        }});

        dialog.setView(layout);
    }
}
