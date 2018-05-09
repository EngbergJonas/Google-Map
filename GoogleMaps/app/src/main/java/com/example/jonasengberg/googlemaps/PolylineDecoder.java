package com.example.jonasengberg.googlemaps;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PolylineDecoder {

    ArrayList<LatLng> decodePolyLine(String polyString)
    {
        ArrayList<LatLng> polyLines = new ArrayList<>();
        int index = 0, length = polyString.length();
        int latitude = 0, longitude = 0;

        while (index < length)
        {
            int b, shift = 0, result = 0;
            do
            {
                b = polyString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            latitude += dlat;

            shift = 0;
            result = 0;
            do {
                b = polyString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            longitude += dlng;

            LatLng p = new LatLng((((double) latitude / 1E5)),(((double) longitude / 1E5)));
            polyLines.add(p);
        }
        return polyLines;
    }
}
