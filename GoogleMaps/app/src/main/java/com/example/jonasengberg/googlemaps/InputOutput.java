package com.example.jonasengberg.googlemaps;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

//For saving the Serialized Route object
public class InputOutput {

    public String[] getRouteRows(ArrayList<Route> routes)
    {
        String[] routeRows = new String[routes.size()];

        for(int i = 0; i < routeRows.length; i++)
        {
            routeRows[i] = String.valueOf(routes.get(i).getRouteText());
        }
        return routeRows;
    }

    public void saveFile(ArrayList<Route> routes, Context c)
    {
        try
        {
            FileOutputStream fOut = c.openFileOutput("myfile", Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fOut);
            out.writeObject(routes);
            out.close();
            fOut.close();
        }
        catch(Exception e)
        {
            System.out.println("ERROR: file could not be saved: " + e.getMessage());
        }
    }

    public ArrayList<Route> loadFile(Context c)
    {
        ArrayList<Route> routes = new ArrayList();

        try
        {
            FileInputStream fIn = c.openFileInput("myfile");
            ObjectInputStream in = new ObjectInputStream(fIn);
            routes = (ArrayList<Route>) in.readObject();
            in.close();
            fIn.close();
        }
        catch(Exception e)
        {
            System.out.println("ERROR: file could not be loaded: " + e.getMessage());
        }

        return routes;
    }
}
