package com.example.huddy.mp3player;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.List;

public class MusicGenresActivity extends AppCompatActivity {
    private ImageButton hiphopBtn,hiphopplBtn,ragaeBtn,partyBtn,classicBtn,houseBtn,hardstyleBtn,rockBtn,popBtn;
    private final String HIPHOP_ID = "PLH6pfBXQXHECUaIU3bu9rjG2L6Uhl5A2q",
    HIPHOP_PL_ID= "PL4C9EC2F8A7B5E396",
    RAGAEE_ID= "UUlmWQqtCRuI0yLcyp8nXN-w",
    PARTY_ID= "UUP_LNjP1_z4fARv6gbCWNQQ",
    CLASSIC_ID= "UUwm4yAIxID33DBgeRl6javg",
    HOUSE_ID= "UUpDJl2EmP7Oh90Vylx0dZtA",
    HARDSTYLE_ID= "PLvW6BGoZcqf7eaHdoj0OAqjK-G7CR53oW",
    ROCK_ID= "PLhd1HyMTk3f5yqcPXjLo8qroWJiMMFBSk",
    POP_ID= "PLDcnymzs18LVXfO_x0Ei0R24qDbVtyy66";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_genres);
        initializeViewComponents();
    }

    private void initializeViewComponents()
    {
        hiphopBtn = (ImageButton)findViewById(R.id.hiphop);
        hiphopplBtn = (ImageButton)findViewById(R.id.hiphoppl);
        ragaeBtn = (ImageButton)findViewById(R.id.ragaee);
        partyBtn = (ImageButton)findViewById(R.id.party);
        classicBtn = (ImageButton)findViewById(R.id.classic);
        houseBtn = (ImageButton)findViewById(R.id.house);
        hardstyleBtn = (ImageButton)findViewById(R.id.hardstyle);
        rockBtn = (ImageButton)findViewById(R.id.rock);
        popBtn = (ImageButton)findViewById(R.id.pop);

        hiphopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",HIPHOP_ID);
                startActivity(iPlaylist);
            }
        });

        hiphopplBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",HIPHOP_PL_ID);
                startActivity(iPlaylist);
            }
        });

        ragaeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",RAGAEE_ID);
                startActivity(iPlaylist);
            }
        });

        classicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",CLASSIC_ID);
                startActivity(iPlaylist);
            }
        });

        partyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",PARTY_ID);
                startActivity(iPlaylist);
            }
        });

        rockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",ROCK_ID);
                startActivity(iPlaylist);
            }
        });

        popBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",POP_ID);
                startActivity(iPlaylist);
            }
        });

        hardstyleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",HARDSTYLE_ID);
                startActivity(iPlaylist);
            }
        });

        houseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPlaylist = new Intent(MusicGenresActivity.this,GenresPlaylistActivity.class);
                iPlaylist.putExtra("PLAYLIST_ID",HOUSE_ID);
                startActivity(iPlaylist);
            }
        });
    }


}
