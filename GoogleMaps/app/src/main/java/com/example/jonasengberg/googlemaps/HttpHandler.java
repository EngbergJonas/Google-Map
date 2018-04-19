package com.example.jonasengberg.googlemaps;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class HttpHandler {

    public HttpHandler()
    {
    }

    public String getHTTPData(String requestUrl)
    {
        URL url;
        String response = "";
        try
        {
            url = new URL(requestUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(15000);
            con.setConnectTimeout(15000);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Contect-type", "application/x-www-form-urlencoded");

            int responseCode = con.getResponseCode();
            if(responseCode == HTTP_OK)
            {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                while((line = br.readLine()) != null)
                    response += line;
            }
            else
            {
                response = "";
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return response;
    }

}

