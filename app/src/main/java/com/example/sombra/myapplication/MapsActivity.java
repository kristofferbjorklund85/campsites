package com.example.sombra.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static GoogleMap mMap;

    private static ArrayList<Marker> markerList = new ArrayList<>();
    private List<CampsiteModel> cml;

    private boolean mLocationPermissionGranted = false;

    private static CampsiteModel newCM = null;
    private static Marker deleteM = null;
    private static boolean markerDelete = false;

    private LatLng defaultLatLng = new LatLng(57.850894, 12.033463);
    private int defaultZoom = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(newCM != null) {
            createMarker(newCM);
            newCM = null;
        }
        if (markerDelete == true && deleteM != null) {
            deleteM.remove();
            markerDelete = false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        cml = extras.getParcelableArrayList("cmList");

        createMarker(cml);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, defaultZoom));

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
                    marker.hideInfoWindow();
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

    private static void createMarker(List<CampsiteModel> list) {
        for (CampsiteModel cm : list) {
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cm.lat, cm.lng))
                    .title(cm.name)
                    .snippet("Type: " + cm.type));
            m.setTag(cm);
        }
    }

    private static void createMarker(CampsiteModel cm) {
        Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cm.lat, cm.lng))
                    .title(cm.name)
                    .snippet("Type: " + cm.type));
            m.setTag(cm);
    }

    public void onSearch(View view) {
        EditText location_tf = (EditText) findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;

        if (Utils.checkString(location, "Search field", 0, 0)) {
            Geocoder gc = new Geocoder(this);
            try {
                addressList = gc.getFromLocationName(location, 1);
                if(addressList.isEmpty()) {
                    Utils.toast("That place doesn't exist", "short");
                    return;
                }
            } catch (IOException | IllegalStateException | IllegalArgumentException e) {
                Utils.toast("That place doesn't exist", "short");
                return;
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(7));
            location_tf.setText("");
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            updateLocationUI();
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
                } else {

                }
            }
        }
        updateLocationUI();
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
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public static void setNewCM(CampsiteModel cm) {
        newCM = cm;
    }

    public static void setDeleteM() {
        markerDelete = true;
    }


}
