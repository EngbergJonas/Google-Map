package com.example.jonasengberg.googlemaps;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CameraHandler {

    private GoogleMap mMap;
    private LatLng latLng;
    private float zoom;
    private String title;

    public CameraHandler(GoogleMap mMap, LatLng latLng, float zoom, String title)
    {
        this.mMap = mMap;
        this.latLng = latLng;
        this.zoom = zoom;
        this.title = title;
    }

    public GoogleMap getmMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
        return mMap;
    }

    /**
    private GoogleMap mMap;
    private LatLng latLng;
    private float zoom;
    private String title;

    public CameraHandler(GoogleMap mMap, LatLng latLng, float zoom, String title){
        this.mMap = mMap;
        this.latLng = latLng;
        this.zoom = zoom;
        this.title = title;
    }

    public GoogleMap getMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);

        return mMap;
    }
     */
}
