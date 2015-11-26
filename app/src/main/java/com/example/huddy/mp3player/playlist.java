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
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;

import java.util.ArrayList;

public class playlist extends AppCompatActivity {

    ListView lv;
    String[] songNames;
    String[] songUris;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        final ArrayList<File> mySongs;
        mySongs = findSongs(Environment.getExternalStorageDirectory());
        lv = (ListView) findViewById(R.id.PlayList);

        songNames = new String[ mySongs.size() ];
        songUris = new String[ mySongs.size() ];

        for(int i=0;i<mySongs.size();i++)
        {
            songNames[i] = mySongs.get(i).getName().toString();
            songUris[i] = mySongs.get(i).getAbsolutePath();
        }
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
                Intent playerIntent = new Intent(playlist.this, player.class);
                playerIntent.putExtra("SELECTED_SONG_URI_STRING",songUris);
                playerIntent.putExtra("SELECTED_SONG_NAME",songNames);
                playerIntent.putExtra("INDEX",position);
                playerIntent.putExtra("COUNT",mySongs.size());
                startActivity(playerIntent);


            }
        });

    }

    //TODO: add mp3 and wav files from internal storage !
    public ArrayList<File> findSongs(File root)
    {
        ArrayList<File> all = new ArrayList<File>();
        File[] files = root.listFiles();
        for (File singleFile: files)
        {
            if(singleFile.isDirectory() && !singleFile.isHidden())
            {
                all.addAll(findSongs(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".mp3"))
                {
                    all.add(singleFile);
                }
            }
        }
        return all;

    }




}
