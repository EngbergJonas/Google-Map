package com.example.jonasengberg.googlemaps;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

public class GetDistances extends AsyncTask<Object, String, String>{

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance;
    LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
       mMap = (GoogleMap)objects[0];
       url = (String)objects[1];
       latLng = (LatLng)objects[2];

       HttpHandler httpHandler = new HttpHandler();
       try
       {
           googleDirectionsData = httpHandler.readUrl(url);
       }
       catch(IOException e)
       {
           e.printStackTrace();
       }
       return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        HashMap<String, String> directionsList = null;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        duration = directionsList.get("duration");
        distance = directionsList.get("distance");

        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Duration: " + duration);
        markerOptions.snippet("Distance: " + distance);

        mMap.addMarker(markerOptions);
    }
}
