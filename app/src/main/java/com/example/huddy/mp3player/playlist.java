package com.example.huddy.mp3player;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.TextView;


import java.io.File;

import java.util.ArrayList;

public class playlist extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ListView lv;
    String[] songNames;
    Intent playerIntent;
    songAdapter songAdapter;
    //private SearchView searchView;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //adding search list widget


        Bundle extras = getIntent().getExtras();
        //songNames = extras.getStringArray("SONGNAMES");
        lv = (ListView) findViewById(R.id.PlayList);

        ArrayList<song> songList = new ArrayList<>();
        Cursor cursor = populateGetSongQueries(this);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
                songList.add(new song(cursor));
        }
        //int i=0;
        songNames = new String[songList.size()];
        for(int i=0;i<songList.size();i++)
        {
            songNames[i] = songList.get(i).getTitle();
        }
        //adapter is usefull for filling out listview objects
        songAdapter = new songAdapter(getApplicationContext(),R.layout.song_list_item,songList);
        lv.setAdapter(songAdapter);
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
    //adding search utility to activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //whats going on here, read l0l
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;



    }
    public Cursor populateGetSongQueries(Context context) {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,    // filepath of the audio file
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID,     // context id/ uri id of the file
        };

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE);

        // the last parameter sorts the data alphanumerically

        return cursor;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        songAdapter.getFilter().filter(newText);
        return true;
    }
}
