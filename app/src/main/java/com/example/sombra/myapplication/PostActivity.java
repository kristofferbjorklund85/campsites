package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    static LatLng latLng = null;

    EditText ln;
    EditText type;
    EditText fee;
    EditText cap;
    EditText avail;
    EditText desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Button bt = (Button)findViewById(R.id.ButtonCreateCampsite);

        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createCampsite();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            return;
        }
        latLng = extras.getParcelable("latlng");
    }

    public void createCampsite() {

        VolleyHandler vh = new VolleyHandler();

        ln    = (EditText)findViewById(R.id.EditTextLocationName);
        type  = (EditText)findViewById(R.id.EditTextType);
        fee   = (EditText)findViewById(R.id.EditTextFee);
        cap   = (EditText)findViewById(R.id.EditTextCapacity);
        avail = (EditText)findViewById(R.id.EditTextAvailability);
        desc  = (EditText)findViewById(R.id.EditTextDescription);

        CampsiteModel cm = new CampsiteModel(UUID.randomUUID().toString(),
                                            ln.getText().toString(),
                                            latLng.latitude,
                                            latLng.longitude,
                                            type.getText().toString(),
                                            fee.getText().toString(),
                                            Integer.parseInt(cap.getText().toString()),
                                            avail.getText().toString(),
                                            desc.getText().toString());

        vh.postCampsites(this, cm);
        Intent intent = new Intent(PostActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}
