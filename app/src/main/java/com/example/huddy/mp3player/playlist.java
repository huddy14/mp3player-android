package com.example.huddy.mp3player;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;

import java.util.ArrayList;

public class playlist extends AppCompatActivity {

    ListView lv;
    String[] songNames;
    Intent playerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Bundle extras = getIntent().getExtras();
        songNames = extras.getStringArray("SONGNAMES");
        lv = (ListView) findViewById(R.id.PlayList);


        //adapter is usefull for filling out listview objects
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,songNames)
        {
            //quick fix for the font colors in list view
            //TODO: reconsider changing it in xml file
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLUE);
                return view;
            }
        };
        lv.setAdapter(adp);
        //TODO: consider changing raw strings to constans delcared in a file
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playerIntent = new Intent(playlist.this, player.class);
                playerIntent.putExtra("INDEX",position);
                setResult(RESULT_OK,playerIntent);
                finish();


            }
        });
    }
}
