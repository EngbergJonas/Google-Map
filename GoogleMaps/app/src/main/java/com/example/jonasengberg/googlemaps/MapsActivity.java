package com.example.jonasengberg.googlemaps;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Boolean myLocationPermissionGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final String EXTRA_MESSAGE = "com.example.jonasengberg.googlemaps.MESSAGE";

    //widgets
    public GoogleMap mMap;
    private EditText searchText;
    private ImageView locateMe;

    //vars
    private double myLat, myLng;

    //for google places
    private String url = "";

    ArrayList<Route> routes;
    InputOutput io;
    Route routeRequest;

    //A list for markers
    ArrayList<LatLng> markerList = new ArrayList<>();
    ArrayList<String> markerTitleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        io = new InputOutput();
        routes = io.loadFile(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchText = findViewById(R.id.input_search);
        locateMe = findViewById(R.id.ic_getlocation);
        getLocationPermission();
        init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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

        //Add a marker by clicking the Map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng marker) {
                url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+marker.latitude+","+marker.longitude+"&key=AIzaSyA1ehJ4zwv4imTO3cgjgbjR6R0BlYNUKoE";
                try {
                    String markerTitle = new GetMarker().execute(url).get();
                    markerTitleList.add(markerTitle+"|");

                    if (markerList.size() >= 8)
                    {
                        return;
                    }

                    markerList.add(marker);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(marker)
                            .title(markerTitle)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    mMap.addMarker(markerOptions);

                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
        });

        //Remove all the markers by holding the map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng marker) {
                mMap.clear();
                markerList.clear();
                markerTitleList.clear();
            }
        });
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
        FusedLocationProviderClient myFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
                        LatLng latLng = new LatLng(myLat, myLng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
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

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addRoute();
                return false;
            }
        });

        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               myLocation();
            }
        });
    }

    //Add the Route Object to a ListView
    public void addNote(View v)
    {
        url = getDirectionsUrl();
        try
        {

            routeRequest = new GetRoute().execute(url).get();
            LatLng startPos = routeRequest.getStartPos();
            String title = routeRequest.getTitle();
            String distance = routeRequest.getDistance();
            ArrayList<LatLng> polyLines = routeRequest.getPolyLines();
            PolylineOptions polylineOptions = new PolylineOptions();
            for(int i = 0; i < polyLines.size(); i++)
            {
                polylineOptions.add(new LatLng(polyLines.get(i).latitude, polyLines.get(i).longitude));
            }
            polylineOptions.color(Color.DKGRAY).width(7);

            //Put all objects into the ArrayList routes
            routes.add(new Route(startPos, title, distance, polyLines));
            if(routes != null)
            {
                io.saveFile(routes, this);
                Toast.makeText(this, "Added note: " + routes.toString(), Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(MapsActivity.this, NoteBookActivity.class);
            intent.putExtra(EXTRA_MESSAGE, routes.get(0).getRouteText());
            startActivity(intent);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //If the list of routes isn't empty - clear the list
    public void removeNotes(View v)
    {
        if(routes != null)
        {
            routes.clear();
            Toast.makeText(MapsActivity.this, "All routes cleared", Toast.LENGTH_LONG).show();
        }
    }

    //Creating the origin and destination
    public void addRoute()
    {
        url = getDirectionsUrl();
        try
        {
            routeRequest = new GetRoute().execute(url).get();
            LatLng startPos = routeRequest.getStartPos();
            String title = routeRequest.getTitle();
            String distance = routeRequest.getDistance();
            ArrayList<LatLng> polyLines = routeRequest.getPolyLines();

            PolylineOptions polylineOptions = new PolylineOptions();
            for(int i = 0; i < polyLines.size(); i++)
            {
                polylineOptions.add(new LatLng(polyLines.get(i).latitude, polyLines.get(i).longitude));
            }
            polylineOptions.color(Color.DKGRAY).width(7);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPos, 11));
            MarkerOptions startMarker = new MarkerOptions()
                    .position(startPos)
                    .title(title)
                    .snippet("Distance: " + distance);
            mMap.addMarker(startMarker);
            mMap.addPolyline(polylineOptions);
            searchText.getText().clear();
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //Get the google directions url
    private String getDirectionsUrl()
    {
        //Create a String of the markerTitleList, and put it into the waypoints= to get multiple destinations
        StringBuilder waypointString = new StringBuilder("");

        for(String s : markerTitleList)
        {
            waypointString.append(s);
        }

        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");

        urlString.append("origin=" + searchText.getText().toString());
        urlString.append("&destination=" + searchText.getText().toString());
        urlString.append("&waypoints=optimize:true|"+waypointString);
        urlString.append("&key=AIzaSyBIIKF-Ab-ETvAjZftPko90Y1YGP7Bk608");
        return urlString.toString();
    }
}



