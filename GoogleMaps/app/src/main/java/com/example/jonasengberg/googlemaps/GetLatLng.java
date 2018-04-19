package com.example.jonasengberg.googlemaps;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.util.HashMap;

public class GetLatLng extends AsyncTask <Object, String, String> {

    GoogleMap mMap;
    String url;
    String googlePositionData;
    String latitude, longitude;

    @Override
    protected String doInBackground(Object... objects)
    {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];

        HttpHandler httpHandler = new HttpHandler();
        try
        {
            googlePositionData = httpHandler.readUrl(url);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return googlePositionData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        HashMap<String, String> positionList = null;
        DataParser parser = new DataParser();
        positionList = parser.parsePositions(s);
        latitude = positionList.get("latitude");
        longitude = positionList.get("longitude");
    }

}

