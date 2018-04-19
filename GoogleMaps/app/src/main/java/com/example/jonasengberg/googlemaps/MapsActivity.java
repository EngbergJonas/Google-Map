package com.example.jonasengberg.googlemaps;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //permissions & FINALS
    private FusedLocationProviderClient myFusedLocationProviderClient;
    private Boolean myLocationPermissionGranted = false;
    public final float DEFAULT_ZOOM = 13.0f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //widgets
    public GoogleMap mMap;
    private EditText searchText;
    private ImageView locateMe;

    //vars
    private double lat, lng;
    private double myLat, myLng;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchText = (EditText) findViewById(R.id.input_search);
        locateMe = (ImageView) findViewById(R.id.ic_getlocation);
        getLocationPermission();
        init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
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
                        Location currentLocation = (Location) task.getResult();
                        myLat = currentLocation.getLatitude();
                        myLng = currentLocation.getLongitude();
                        CameraHandler cameraHandler = new CameraHandler(mMap, new LatLng(myLat, myLng), DEFAULT_ZOOM, "It's me!");
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


    private void init() {
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                try
                {
                    new getData().execute(searchText.getText().toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation();
            }
        });
    }

    private class getData extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPostExecute(String s)
        {
            try
            {
                JSONObject jsonObj = new JSONObject(s);
                lat = ((JSONArray) jsonObj.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").getDouble("lat");
                lng = ((JSONArray) jsonObj.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").getDouble("lng");
                //Toast.makeText(MapsActivity.this, "The result is" + lat + lng, Toast.LENGTH_LONG).show();
                CameraHandler cameraHandler = new CameraHandler(mMap, new LatLng(lat, lng), DEFAULT_ZOOM, searchText.getText().toString());
                cameraHandler.getmMap();

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        protected String doInBackground(String... strings)
        {
            String response;
            try
            {
                String address = strings[0];
                HttpHandler http = new HttpHandler();
                String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyA1ehJ4zwv4imTO3cgjgbjR6R0BlYNUKoE";
                response = http.getHTTPData(url);
                return response;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}

