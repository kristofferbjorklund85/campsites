package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.UUID;

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
        EditText password       = (EditText) findViewById(R.id.input_password);
        EditText repeatPassword = (EditText) findViewById(R.id.input_repeat_password);

        if(     Utils.checkString(username.getText().toString(), "Username", 3, 30) &&
                Utils.checkString(password.getText().toString(), "Password", 3, 0) &&
                Utils.checkString(repeatPassword.getText().toString(), "Password", 3, 0) &&
                password.getText().toString().equals(repeatPassword.getText().toString())) {

                postUser(new UserModel(UUID.randomUUID().toString(), username.getText().toString(), password.getText().toString()));
        }
    }

    private void postUser(UserModel user) {
        Gson gson = new Gson();
        String jo = gson.toJson(user);

        GenericRequest gr = new GenericRequest(
                Request.Method.POST,
                url + "?type=user",
                String.class,
                jo,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(SessionSingleton.getAppContext(), "User created!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 403) {
                            Utils.toast("That username already exists!", "long");
                        }
                        Utils.toast("Something went wrong!", "short");
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(gr);
        }
}
