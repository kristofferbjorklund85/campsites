package com.example.sombra.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            return;
        }

        LatLng latLng = extras.getParcelable("latlng");

        double lat = latLng.latitude;
        double lng = latLng.longitude;

        /*TextView textView = (TextView) findViewById(R.id.latlng);
        textView.setText("Latitude: " + Double.toString(lat) + "\nLongitude: " + Double.toString(lng));*/
    }
}
