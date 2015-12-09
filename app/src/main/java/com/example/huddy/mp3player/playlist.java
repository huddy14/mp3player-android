package com.example.huddy.mp3player;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;

public class playlist extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ListView lv;
    String[] songNames;
    Intent playerIntent;
    songAdapter songAdapter;
    ArrayList<song>songList;
    //private SearchView searchView;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //adding search list widget


        Bundle extras = getIntent().getExtras();
        songDataWrapper dw = (songDataWrapper)getIntent().getSerializableExtra("SONGLIST");
        songList = dw.getSongList();
        lv = (ListView) findViewById(R.id.PlayList);


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
                Object s =  parent.getAdapter().getItem(position);
                playerIntent.putExtra("ID", ((song) s).getId());
                setResult(RESULT_OK, playerIntent);
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
