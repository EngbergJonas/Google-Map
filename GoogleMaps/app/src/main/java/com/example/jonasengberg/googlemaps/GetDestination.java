package com.example.jonasengberg.googlemaps;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

public class GetDestination extends AsyncTask<Object, String, String>{

    private GoogleMap mMap;
    private String url;
    private String googleDirectionsData;
    private String duration, distance;
    private Double latitude, longitude;
    private Double endLatitude, endLongitude;
    String title;

    private static Context context;

    GetDestination(Context c){
        context = c;
    }

    private static void showToast(){
        Toast.makeText(context, "You can't drive through the oceans!", Toast.LENGTH_LONG).show();
    }

    public HashMap<String, String> directionList(String jsonData)
    {
        DataParser parser = new DataParser();
        HashMap<String, String> directionList= null;
        directionList = parser.parseDirections(jsonData);
        duration = directionList.get("duration");
        distance = directionList.get("distance");
        title = directionList.get("start_address");
        return directionList;
    }

    public HashMap<String, Double> positionList(String jsonData)
    {
        DataParser parser = new DataParser();
        HashMap<String, Double> positionList = null;
        positionList = parser.parseLatLng(jsonData);
        latitude = positionList.get("lat");
        longitude = positionList.get("lng");
        endLatitude = positionList.get("endLat");
        endLongitude = positionList.get("endLng");
        return positionList;
    }

    @Override
    protected String doInBackground(Object... objects) {
       mMap = (GoogleMap)objects[0];
       url = (String)objects[1];

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
        try {

            directionList(s);
            positionList(s);

            LatLng latLng = (new LatLng(latitude, longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            MarkerOptions startMarker = new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title(title);
            startMarker.snippet("Distance: " + distance + ", " + "Duration: " + duration);
            mMap.addMarker(startMarker);

            LatLng endLatLng = (new LatLng(endLatitude, endLongitude));
            MarkerOptions endMarker = new MarkerOptions()
                    .position(endLatLng);
            mMap.addMarker(endMarker);
        }
        catch (Exception e)
        {
            showToast();
            e.printStackTrace();
        }
    }
}
