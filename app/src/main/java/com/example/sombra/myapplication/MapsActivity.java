package com.example.sombra.myapplication;

import android.Manifest;
import android.content.Context;
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
    private List<CampsiteModel> cml;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private boolean mLocationPermissionGranted = false;

    private Location mLastKnownLocation;
    private LatLng mDefaultLocation = new LatLng(-17.824858, 31.053028);

    private int DEFAULT_ZOOM = 11;

    private static double currentLat;
    private static double currentLng;

    private static CampsiteModel newCM = null;
    private static Marker deleteM = null;
    private static boolean markerDelete = false;

    private boolean vr = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(newCM != null) {
            createMarker(newCM);
        }
        if (markerDelete == true && deleteM != null) {
            deleteM.remove();
        }
    }

    private static void createMarker(List<CampsiteModel> list) {
        int i = 0;
        for (CampsiteModel cm : list) {
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cm.lat, cm.lng))
                    .title(cm.name)
                    .snippet("Type: " + cm.type + " Rating: " + cm.rating));
            m.setTag(cm);
            i++;
            Log.d("Markers ", "created " + i + " markers");
        }
    }

    private static void createMarker(CampsiteModel cm) {
        Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cm.lat, cm.lng))
                    .title(cm.name)
                    .snippet("Type: " + cm.type + " Rating: " + cm.rating));
            m.setTag(cm);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("Extras in Maps: ", " NULL");
            return;
        }

        cml = extras.getParcelableArrayList("cmList");

        Log.d("campsitemodelList size ", String.valueOf(cml.size()));

        createMarker(cml);

        updateLocationUI();
        //getDeviceLocation();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTitle().equals("Create new Campsite")) {
                    Intent intent = new Intent(MapsActivity.this, PostActivity.class);
                    intent.putExtra("latlng", marker.getPosition());
                    startActivity(intent);
                    marker.remove();
                } else {
                    deleteM = marker;
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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (markerList.size() != 0 && marker != markerList.get(0)) {
                    markerList.get(0).remove();
                }
                marker.showInfoWindow();
                return true;
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
            Log.d("Location Permission ", "false");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
        } else {
            Log.d("Location Permission ", "true");
            mLocationPermissionGranted = true;
            updateLocationUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        Log.d("Request Permission", "Doing");
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Log.d("Permission request ", "true");
                } else {
                    Log.d("Permission request", "false");
                }
            }
        }
        updateLocationUI();
    }

    public void getDeviceLocation() {
        Log.d("DeviceLocation ", "Start");
        try {
            if (mLocationPermissionGranted) {
                Log.d("DeviceLocation", "Permission Granted");
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d("DeviceLocation", "onComplete");
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            vr = true;
                        }
                        else {
                            Log.d("DeviceLocation", "Current location is null. Using defaults.");
                            Log.e("DeviceLocation", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            vr = true;
                        }
                    }
                });
            }
        }
            catch(SecurityException e){
                Log.e("Exception: %s", e.getMessage());
            }
            vr = false;
            while(vr == false){}
    }

    void updateLocationUI() {
        Log.d("UpdateLocation ", "Start");
        if (mMap == null) {
            Log.d("mMap ", "NULL");
            return;
        }
        Log.d("UpdateLocation", "Before try");
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                Log.d("Location Enabled ", "true");
            } else {
                mMap.setMyLocationEnabled(false);
                Log.d("Location Enabled ", "false");
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public static LatLng getCurrentLatLng() {
        Log.d("Lat ", String.valueOf(currentLat));
        Log.d("Lng ", String.valueOf(currentLng));

        LatLng latLng = new LatLng(currentLat, currentLng);
        return latLng;
    }

    public static void setNewCM(CampsiteModel cm) {
        newCM = cm;
    }

    public static void setDeleteM() {
        markerDelete = true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivity.this, LandingActivity.class);
        startActivity(intent);
    }

}
