package com.example.jonasengberg.googlemaps;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

//A Serializable class to create a Route object and later save it into the devices internal memory, containing all the essentials
public class Route implements Serializable{

    private LatLng startPos;
    private String title;
    private String distance;

    private ArrayList<LatLng> polylines;

    public Route (LatLng startPos, String title, String distance, ArrayList<LatLng> polylines)
    {
        this.startPos = startPos;
        this.title = title;
        this.distance = distance;
        this.polylines = polylines;
    }

    public LatLng getStartPos()
    {
        return startPos;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDistance()
    {
        return distance;
    }

    public ArrayList<LatLng> getPolyLines()
    {
        return polylines;
    }

    public String getRouteText()
    {
        String[] list = {title, " - ", distance};
        String result = "";

        StringBuilder sb = new StringBuilder();
        for (String str : list)
            sb.append(str).append(result);

        return sb.substring(0, sb.length());
    }
}