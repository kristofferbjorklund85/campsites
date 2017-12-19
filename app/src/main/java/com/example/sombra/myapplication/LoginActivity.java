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

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String url;
    boolean confirmedUser = false;
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
        if (Utils.checkString(un.getText().toString(), "Username") &&
                Utils.checkString(pw.getText().toString(), "Password")) {
            userExists(un.getText().toString(), pw.getText().toString());
            if(confirmedUser) {
                UserSingleton.setUsername(un.getText().toString());
            }
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
                    public void onResponse(JSONObject jo) {
                        Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                        startActivity(intent);
                        finish();
                        confirmedUser = true;
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
