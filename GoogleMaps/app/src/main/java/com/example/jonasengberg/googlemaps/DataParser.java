package com.example.jonasengberg.googlemaps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DataParser {

    //FOR DUR&DIST
    private HashMap<String, String> getDuration(JSONArray googleDirectionsJson) {
        HashMap<String, String> googleDirectionsMap = new HashMap<>();
        String duration = "";
        String distance = "";

        Log.d("json response", googleDirectionsJson.toString());
        try {
            duration = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionsMap.put("duration", duration);
            googleDirectionsMap.put("distance", distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleDirectionsMap;
    }

    public HashMap<String, String> parseDirections(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getDuration(jsonArray);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------//

    //FOR LAT&LNG
    private HashMap<String, String> getPosition(JSONArray googlePositionJson)
    {
        HashMap<String, String> positionMap = new HashMap<>();
        String latitude;
        String longitude;

        Log.d("json response", googlePositionJson.toString());
        try
        {

            latitude = googlePositionJson.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                    .getJSONObject("lat").toString();
            longitude = googlePositionJson.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                    .getJSONObject("lng").toString();

            positionMap.put("latitude", latitude);
            positionMap.put("longitude", longitude);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return positionMap;
    }

    public HashMap<String, String> parsePositions(String jsonData) {

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
            //lng = ((JSONArray) jsonObj.get("results")).getJSONObject(0).getJSONObject("geometry")
            //.getJSONObject("location").getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPosition(jsonArray);
    }
}

