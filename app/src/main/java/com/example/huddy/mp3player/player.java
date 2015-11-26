package com.example.huddy.mp3player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class player extends AppCompatActivity {

    /**
     * Variables
     */
    Button btnGoBack, btnStart, btnStop, btnPause;
    TextView tvSongName;
    MediaPlayer mp = null;
    String songUris[],songNames[];
    int songIndex = 0;
    int songCount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        stopSong();
        setVariables();
        initializeViewComponents();

        playSong();


    }

    private void initializeViewComponents()
    {
        btnGoBack = (Button)findViewById(R.id.buttonBack);
        btnStart = (Button)findViewById(R.id.buttonStart);
        btnPause = (Button)findViewById(R.id.buttonPause);
        btnStop = (Button)findViewById(R.id.buttonStop);


        tvSongName = (TextView)findViewById(R.id.textViewSongName);
        tvSongName.setText(songNames[songIndex]);
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

                playSong();


            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseSong();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSong();
                startPlaylistActivity();


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
        getSong(songUris[songIndex]);
    }
    private void startPlaylistActivity() {
        Intent Iplaylist = new Intent(player.this, playlist.class);
        startActivity(Iplaylist);
    }

    private void playSong ()
    {
        if(mp!=null)
            mp.start();
    }

    private void pauseSong()
    {
        if (mp != null)
            mp.pause();
    }

    private void stopSong()
    {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private void getSong (String song)
    {
        mp = MediaPlayer.create(this, Uri.parse(song));
    }


}
