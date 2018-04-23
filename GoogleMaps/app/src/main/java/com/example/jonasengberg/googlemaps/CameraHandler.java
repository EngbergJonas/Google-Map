package com.example.jonasengberg.googlemaps;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CameraHandler {

    private GoogleMap mMap;
    private LatLng latLng;
    private float zoom;
    private String title;
    private boolean draggable;

    public CameraHandler(GoogleMap mMap, LatLng latLng, float zoom, String title, boolean draggable)
    {
        this.mMap = mMap;
        this.latLng = latLng;
        this.zoom = zoom;
        this.title = title;
        this.draggable = draggable;
    }

    public GoogleMap getmMap()
    {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title)
                .draggable(draggable)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(options);
        return mMap;
    }



}
