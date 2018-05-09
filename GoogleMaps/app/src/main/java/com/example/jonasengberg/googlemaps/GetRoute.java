package com.example.jonasengberg.googlemaps;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetRoute extends AsyncTask<String, Void, Route>
{
    public Route parsedRoutes(String jsonString)
    {
        try {

            JSONObject jsonObject = new JSONObject(jsonString)
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0);


            JSONObject response = new JSONObject(jsonString)
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONObject("overview_polyline");
            String polyString = response.getString("points");

            LatLng startPos = new LatLng(jsonObject.getJSONObject("start_location").getDouble("lat"), jsonObject.getJSONObject("start_location").getDouble("lng"));
            String title = (jsonObject.getString("start_address"));
            String distance = (jsonObject.getJSONObject("distance").getString("text"));
            PolylineDecoder polylineDecoder = new PolylineDecoder();
            ArrayList<LatLng> points = polylineDecoder.decodePolyLine(polyString);

            return new Route(startPos, title, distance, points);
        }
        catch (Exception e)
        {
            System.out.println("ERROR from getCoordinates: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected Route doInBackground(String[] params)
    {
        String jsonString = "";

        try
        {
            HttpHandler httpHandler = new HttpHandler();
            jsonString = httpHandler.readUrl(params[0]);
            return parsedRoutes(jsonString);
        }

        catch (Exception e)
        {
            System.out.println("ERROR from doInBackground: " + e.getMessage());
        }

        return null;
    }
}
