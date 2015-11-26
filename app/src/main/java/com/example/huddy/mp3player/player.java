package com.example.huddy.mp3player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


public class player extends AppCompatActivity {

    /**
     * Variables
     */
    Button btnGoBack, btnStart, btnStop, btnPause, btnNext, btnPrevious;
    TextView tvSongName,tvSongDuration;
    String songUris[],songNames[];
    int songIndex = 0;
    int songCount = 0;
    boolean mpServiceBound = false;
    mpService musicService;
    Intent IMusicService;
    int songDuration;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setVariables();
        initializeViewComponents();



    }

    @Override
    protected void onStart() {
        super.onStart();
        IMusicService = new Intent(this,mpService.class);
        startService(IMusicService);
        bindService(IMusicService, MusicServiceConnection, Context.BIND_AUTO_CREATE);



    }

    private ServiceConnection MusicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mpService.MyBinder myBinder = (mpService.MyBinder) service;
            musicService = myBinder.getService();
            mpServiceBound = true;

            musicService.getSong(songUris[songIndex]);

            tvSongName.setText(songNames[songIndex]);
            tvSongDuration.setText(""+musicService.getDuration()/1000);



        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mpServiceBound =false;
        }
    };


    private void initializeViewComponents()
    {
        btnGoBack = (Button)findViewById(R.id.buttonBack);
        btnStart = (Button)findViewById(R.id.buttonStart);
        btnPause = (Button)findViewById(R.id.buttonPause);
        btnStop = (Button)findViewById(R.id.buttonStop);

        tvSongDuration = (TextView)findViewById(R.id.textViewSongTime);
        tvSongName = (TextView)findViewById(R.id.textViewSongName);

        //....
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaylistActivity();
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mpServiceBound) {
                    musicService.playSong();
                }


            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mpServiceBound)
                    musicService.pauseSong();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mpServiceBound) {
                    musicService.stopSong();
                    startPlaylistActivity();
                }
            }
        });
    }

    private void setVariables()
    {
        Bundle extras = getIntent().getExtras();
        songCount = extras.getInt("COUNT");
        songIndex = extras.getInt("INDEX");
        songUris = new String[songCount];
        songNames = new String[songCount];
        songUris= extras.getStringArray("SELECTED_SONG_URI_STRING");
        songNames = extras.getStringArray("SELECTED_SONG_NAME");
        //isCurrentlyPlaying = extras.getBoolean("ISPLAYING");
        //musicService.getSong(songUris[songIndex]);
    }
    private void startPlaylistActivity() {
        Intent Iplaylist = new Intent(player.this, playlist.class);
        startActivity(Iplaylist);

    }




}