package com.example.jonasengberg.googlemaps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class DataParser {

    //Parse the Direction, Distance and Title from the UrlRequest
    private HashMap<String, String> parseDirections(JSONArray googleDirectionsJson) {
        HashMap<String, String> googleDirectionsMap = new HashMap<>();
        String duration = "";
        String distance = "";
        String title = "";

        try {
            duration = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsJson.getJSONObject(0).getJSONObject("distance").getString("text");
            title = googleDirectionsJson.getJSONObject(0).getString("start_address");

            googleDirectionsMap.put("duration", duration);
            googleDirectionsMap.put("distance", distance);
            googleDirectionsMap.put("start_address", title);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleDirectionsMap;
    }

    public HashMap<String, String> getDirections(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseDirections(jsonArray);
    }

    //----------------------------------------------------------------------------------------------//

    //Parse the Latitude & Longitude from the Request
    private HashMap<String, Double> parseCoordinates(JSONArray googleLatLngJson) {
        HashMap<String, Double> googleLatLngMap = new HashMap<>();
        Double latitude = 0.0;
        Double longitude = 0.0;
        Double endLatitude = 0.0;
        Double endLongitude = 0.0;

        try {
            latitude = googleLatLngJson.getJSONObject(0).getJSONObject("start_location").getDouble("lat");
            longitude = googleLatLngJson.getJSONObject(0).getJSONObject("start_location").getDouble("lng");
            endLatitude = googleLatLngJson.getJSONObject(0).getJSONObject("end_location").getDouble("lat");
            endLongitude = googleLatLngJson.getJSONObject(0).getJSONObject("end_location").getDouble("lng");

            googleLatLngMap.put("lat", latitude);
            googleLatLngMap.put("lng", longitude);
            googleLatLngMap.put("endLat", endLatitude);
            googleLatLngMap.put("endLng", endLongitude);

            //Log.d("json response", googleLatLngMap.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleLatLngMap;
    }

    public HashMap<String, Double> getCoordinates(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseCoordinates(jsonArray);
    }
}

