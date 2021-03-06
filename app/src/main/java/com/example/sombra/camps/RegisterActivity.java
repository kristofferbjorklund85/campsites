package com.example.sombra.camps;

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

/**
 * Handles all methods revoling around registration of a new user and
 * the actual RegisterActivity.
 */
public class RegisterActivity extends AppCompatActivity {
    String url;

    /**
     * onCreate() sets the view for the activity and gets the URL from resource.
     *
     * @param savedInstanceState The standard Bundle from previous class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        url = this.getResources().getString(R.string.apiURL);
    }

    /**
     * If the back button is pressed LoginActivity is started.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * checkRegistration() checks the required fields for registration with our checkString().
     * If everying is ok we run register() handling the rest of the registration process.
     *
     * @param view Needed for onClick() usage.
     */
    public void checkRegistration(View view) {
        EditText username       = (EditText) findViewById(R.id.input_username);
        EditText password       = (EditText) findViewById(R.id.input_password);
        EditText repeatPassword = (EditText) findViewById(R.id.input_repeat_password);

        if(     Utils.checkString(username.getText().toString(), "Username", 3, 30) &&
                Utils.checkString(password.getText().toString(), "Password", 3, 0) &&
                Utils.checkString(repeatPassword.getText().toString(), "Password", 3, 0) &&
                password.getText().toString().equals(repeatPassword.getText().toString())) {
                    register(new UserModel( UUID.randomUUID().toString(), username.getText().toString(),
                                            password.getText().toString()));
        }
    }

    /**
     * register() posts the new user to the database with a httpPostRequest.
     * If everything is okay we start the loginActivity and the user can login with the new account.
     *
     * @param user User that will be registered
     */
    private void register(UserModel user) {
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
