package com.example.sombra.myapplication;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);

    private Marker mPerth;
    private Marker mSydney;
    private Marker mBrisbane;

    private static GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private static void createMarkers(ArrayList<CampsiteModel> list) {
        for(CampsiteModel cm : list) {
            Marker m =   mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cm.lat, cm.lng))
                        .title(cm.location));
            m.setTag(cm);
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("Extras in Maps: ", " NULL");
            return;
        }

        ArrayList<CampsiteModel> cml = extras.getParcelableArrayList("cmList");

        for(CampsiteModel cm : cml) {
            Log.d("List before: ", cm.location);
        }

        createMarkers(cml);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(BRISBANE));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, Campsite.class);
                intent.putExtra("cm", (CampsiteModel) marker.getTag());
                startActivity(intent);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Marker m =  mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .title("User created marker"));

                Intent intent = new Intent(MapsActivity.this, PostActivity.class);
                intent.putExtra("latlng", m.getPosition());
                startActivity(intent);
            }
        });
    }
}
