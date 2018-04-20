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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

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
    private ImageView directions;

    //vars
    private double lat, lng;
    private double myLat, myLng;
    private double endLat, endLng;
    private String title;
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
        locateMe = (ImageView) findViewById(R.id.ic_getlocation);
        directions = (ImageView) findViewById(R.id.ic_directions);
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

    private void init() {
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*
                dataTransfer = new Object[2];
                url = getPositionUrl();
                GetLatLng getLatLng = new GetLatLng();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getLatLng.execute(dataTransfer);
                */
                try {
                    new getData().execute(searchText.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //getDistance();
                dataTransfer = new Object[3];
                url = getDirectionsUrl();
                GetDistances getDistances = new GetDistances();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(endLat, endLng);
                getDistances.execute(dataTransfer);
            }
        });
    }

    private String getDirectionsUrl()
    {
        //WORKS
        //https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyBIIKF-Ab-ETvAjZftPko90Y1YGP7Bk608
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+myLat+","+myLng);
        googleDirectionsUrl.append("&destination="+endLat+","+endLng);
        googleDirectionsUrl.append("&key="+"AIzaSyBIIKF-Ab-ETvAjZftPko90Y1YGP7Bk608");

        return googleDirectionsUrl.toString();
    }

    private String getPositionUrl()
    {
        //WORKS
        //https://maps.googleapis.com/maps/api/geocode/json?address=Helsinki&key=AIzaSyA1ehJ4zwv4imTO3cgjgbjR6R0BlYNUKoE
        StringBuilder positionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?");
        positionUrl.append("address="+searchText.getText().toString());
        positionUrl.append("&key="+"AIzaSyA1ehJ4zwv4imTO3cgjgbjR6R0BlYNUKoE");

        return getDirectionsUrl();
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
                title = ((JSONArray) jsonObj.get("results")).getJSONObject(0).getString("formatted_address");
                endLng = lng;
                endLat = lat;

                //Toast.makeText(MapsActivity.this, "The result is" + lat + lng, Toast.LENGTH_LONG).show();
                CameraHandler cameraHandler = new CameraHandler(mMap, new LatLng(lat, lng), DEFAULT_ZOOM, title, true);
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
                HttpHandler httpHandler = new HttpHandler();
                String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyA1ehJ4zwv4imTO3cgjgbjR6R0BlYNUKoE";
                response = httpHandler.readUrl(url);
                return response;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        endLat = marker.getPosition().latitude;
        endLng = marker.getPosition().longitude;
    }
    @Override
    public void onMarkerDragStart(Marker marker){}
    @Override
    public void onMarkerDrag(Marker marker){}
}

/**TODO: https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyBIIKF-Ab-ETvAjZftPko90Y1YGP7Bk608
 * Read this file with the Directions API
 * Change the origin to myLat & myLng
 * Change the destination to lat & lng
 *
 * -> routes: [
 {
 bounds: {
 northeast: {
 lat: 45.5024461,
 lng: -73.566002
 },
 southwest: {
 lat: 43.6533096,
 lng: -79.3827656
 }
 },
 copyrights: "Karttatiedot ©2018 Google",
 --> legs: [
 {
 ---> distance: {
 ----> text: "547 km",
 value: 547015
 },
 ---> duration: {
 ----> text: "5 h 23 min",
 value: 19361
 },
 * Display by clicking a button
 */
