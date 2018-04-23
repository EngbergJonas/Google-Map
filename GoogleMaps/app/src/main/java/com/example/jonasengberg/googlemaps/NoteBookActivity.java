package com.example.jonasengberg.googlemaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteBookActivity extends AppCompatActivity {

    public ListView routeList;
    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MapsActivity.EXTRA_MESSAGE);

        routeList = (ListView) findViewById(R.id.routeList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        routeList.setAdapter(adapter);
        updateList();
    }

    public void updateList()
    {
        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();
        if(bundle != null)
        {
            String message = intent.getStringExtra(MapsActivity.EXTRA_MESSAGE);
            list.add(message);
            System.out.println("TÄSSÄ DEBUGGAS" + list.get(0));
        }
    }
}
