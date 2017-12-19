package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
        Log.d("Guest ", "Clicked");
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
}
