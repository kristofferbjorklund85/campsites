package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        url = this.getResources().getString(R.string.apiURL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void registerUser(View view) {
        EditText username       = (EditText) findViewById(R.id.input_username);
        EditText password       = (EditText) findViewById(R.id.input_username);
        EditText repeatPassword = (EditText) findViewById(R.id.input_username);

        if(     Utils.checkString(username.getText().toString(), "Username") &&
                Utils.checkString(password.getText().toString(), "Password") &&
                Utils.checkString(repeatPassword.getText().toString(), "Password") &&
                password.getText().toString().equals(repeatPassword.getText().toString())) {

                postUser(new UserModel(username.getText().toString(), password.getText().toString()));
        }
    }

    private void postUser(UserModel user) {
        JSONObject jo = toJson(user);

        JsonObjectRequest joReq = new JsonObjectRequest(
                Request.Method.POST,
                url + "?type=user",
                jo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(UserSingleton.getAppContext(), "UserSingleton created!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserSingleton.getAppContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(UserSingleton.getAppContext()).addToRequestQueue(joReq);
    }

    private JSONObject toJson(UserModel user) {
        JSONObject jo = new JSONObject();

        try {
            jo.put("id", user.id);
            jo.put("username", user.username);
            jo.put("password", user.password);
        } catch(JSONException e) {
            Log.d("toJSON UserSingleton", e.toString());
        }
        return jo;
    }
}
