package com.example.jonasengberg.googlemaps;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //permissions & FINALS
    private FusedLocationProviderClient myFusedLocationProviderClient;
    private Boolean myLocationPermissionGranted = false;
    public final float DEFAULT_ZOOM = 13.0f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final String EXTRA_MESSAGE = "com.example.jonasengberg.googlemaps.MESSAGE";

    //widgets
    public GoogleMap mMap;
    private EditText searchText;
    private ImageView locateMe;
    private EditText searchText1;

    //vars
    private double myLat, myLng;
    Object dataTransfer[];

    //for google places
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchText = (EditText) findViewById(R.id.input_search);
        searchText1 = (EditText) findViewById(R.id.input_search1);
        locateMe = (ImageView) findViewById(R.id.ic_getlocation);
        getLocationPermission();
        init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        if(myLocationPermissionGranted)
        {
            myLocation();
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    //Asking permission to view location
    private void getLocationPermission()
    {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            myLocationPermissionGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //If permission was not granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        myLocationPermissionGranted = false;
        switch(requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
            {
                if(grantResults.length > 0)
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            myLocationPermissionGranted = false;
                            return;
                        }
                    }
                    myLocationPermissionGranted = true;
                }
            }
        }
    }

    //Get my location
    public void myLocation()
    {
        myFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try
        {
            if(myLocationPermissionGranted)
            {
                Task location = myFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener()
                {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        Location myLocation = (Location) task.getResult();
                        myLat = myLocation.getLatitude();
                        myLng = myLocation.getLongitude();
                        CameraHandler cameraHandler = new CameraHandler(mMap, new LatLng(myLat, myLng), DEFAULT_ZOOM, "It's me!", true);
                        cameraHandler.getmMap();
                    }
                });
            }
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }
    }

    //Get destination between two points
    private void init() {
        searchText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                dataTransfer = new Object[2];
                url = getDirectionsUrl();
                GetData getDestination = new GetData(MapsActivity.this);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getDestination.execute(dataTransfer);
                return false;
            }
        });

        //Locate me
        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               myLocation();
            }
        });
    }

    //Add to listView
    public void addNote(View view)
    {
        Intent intent = new Intent(this, NoteBookActivity.class);
        String message = (searchText.getText().toString() + " - " + searchText1.getText().toString());
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public ArrayList<String> noteList(HashMap<String, String> someMap)
    {
        return null;
    }

    //read the directions url
    private String getDirectionsUrl()
    {
        //https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyBIIKF-Ab-ETvAjZftPko90Y1YGP7Bk608
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+searchText.getText().toString());
        googleDirectionsUrl.append("&destination="+searchText1.getText().toString());
        googleDirectionsUrl.append("&key="+"AIzaSyBIIKF-Ab-ETvAjZftPko90Y1YGP7Bk608");

        return googleDirectionsUrl.toString();
    }
}

