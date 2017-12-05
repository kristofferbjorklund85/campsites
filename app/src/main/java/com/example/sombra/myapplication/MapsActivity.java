package com.example.sombra.myapplication;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private static ArrayList<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private static void createMarker(ArrayList<CampsiteModel> list) {
        for(CampsiteModel cm : list) {
            Marker m =   mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(cm.lat, cm.lng))
                        .title(cm.location));
            m.setTag(cm);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("Extras in Maps: ", " NULL");
            return;
        }

        ArrayList<CampsiteModel> cml = extras.getParcelableArrayList("cmList");

        createMarker(cml);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTitle().equals("Create new Campsite")) {
                    Intent intent = new Intent(MapsActivity.this, PostActivity.class);
                    intent.putExtra("latlng", marker.getPosition());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MapsActivity.this, CampsiteActivity.class);
                    intent.putExtra("cm", (CampsiteModel) marker.getTag());
                    startActivity(intent);
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(!markerList.isEmpty()) {
                    markerList.get(0).remove();
                    markerList.remove(0);
                }
                Marker m =  mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .title("Create new Campsite"));

                markerList.add(m);
                m.showInfoWindow();
            }
        });
    }

    public void onSearch(View view) {
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;

        if(location != null || !location.equals("")) {
            Geocoder gc = new Geocoder(this);
            try {
                addressList = gc.getFromLocationName(location, 1);
            } catch(IOException | IllegalStateException | IllegalArgumentException e) {
                Log.d("Search Maps: ", e.toString());
                Toast toast = Toast.makeText(this, "That place doesn't exist", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(7));
        }
    }
}

