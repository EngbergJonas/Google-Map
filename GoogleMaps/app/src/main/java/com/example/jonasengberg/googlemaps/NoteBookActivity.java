package com.example.jonasengberg.googlemaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class NoteBookActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> routeList = new ArrayList<>();
    ArrayList<Route> routes;
    InputOutput io;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        io = new InputOutput();
        routes = io.loadFile(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book);

        listView = (ListView) findViewById(R.id.routeList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routeList);
        listView.setAdapter(adapter);
        updateList();
    }

    public void updateList()
    {
        Intent intent = getIntent();
        String message = intent.getStringExtra(MapsActivity.EXTRA_MESSAGE);
        routeList.add(message);
        System.out.println("DEBUG: " + routeList.get(0));

    }


}
