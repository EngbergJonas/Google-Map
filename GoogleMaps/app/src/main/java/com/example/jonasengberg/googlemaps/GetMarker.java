package com.example.jonasengberg.googlemaps;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class GetMarker extends AsyncTask<String, Void, String> {

    public String parsedMarker(String jsonString)
    {
        try {
            JSONObject jsonObject = new JSONObject(jsonString)
                    .getJSONArray("results")
                    .getJSONObject(0);
                    //.getJSONArray("address_components")
                    //.getJSONObject(1);
            return new String(jsonObject.getString("formatted_address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String doInBackground(String... strings) {
        {
            String jsonString = "";
            try
            {
                HttpHandler httpHandler = new HttpHandler();
                jsonString = httpHandler.readUrl(strings[0]);
                return parsedMarker(jsonString);
            }

            catch (Exception e)
            {
                System.out.println("ERROR from doInBackground: " + e.getMessage());
            }

            return null;
        }
    }
}
