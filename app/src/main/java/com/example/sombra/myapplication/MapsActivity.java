package com.example.sombra.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static GoogleMap mMap;
    private static ArrayList<Marker> markerList = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation = new LatLng(-17.824858, 31.053028);
    private int DEFAULT_ZOOM = 11;
    private static double currentLat;
    private static double currentLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(this);
    }

    private static void createMarker(ArrayList<CampsiteModel> list) {
        for (CampsiteModel cm : list) {
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cm.lat, cm.lng))
                    .title(cm.location));
            m.setTag(cm);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();
        getDeviceLocation();

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
                if (!markerList.isEmpty()) {
                    markerList.get(0).remove();
                    markerList.remove(0);
                }
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Create new Campsite"));

                markerList.add(m);
                m.showInfoWindow();
            }
        });
    }

    public void onSearch(View view) {
        EditText location_tf = (EditText) findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder gc = new Geocoder(this);
            try {
                addressList = gc.getFromLocationName(location, 1);
            } catch (IOException | IllegalStateException | IllegalArgumentException e) {
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

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
        } else {
            mLocationPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            currentLat = mLastKnownLocation.getLatitude();
                            Log.d("CurrentLat: ", String.valueOf(currentLat));
                            currentLng = mLastKnownLocation.getLongitude();
                            Log.d("CurrentLng: ", String.valueOf(currentLng));
                        } else {
                            Log.d("getDeviceLocation", "Current location is null. Using defaults.");
                            Log.e("getDeviceLocation", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public static LatLng getCurrentLatLng() {
        LatLng latLng = new LatLng(currentLat, currentLng);
        return latLng;
    }
}
