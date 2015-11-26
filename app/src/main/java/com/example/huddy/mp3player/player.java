package com.example.huddy.mp3player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;


public class player extends AppCompatActivity implements mpService.CallBacks{

    /**
     * Variables
     */
    ImageButton btnGoBack, btnStart, btnStop, btnPause, btnNext, btnPrevious, btnShuffle;
    TextView tvSongName,tvSongDuration;
    String songUris[],songNames[];
    int songIndex = 0;
    int songCount = 0;
    boolean mpServiceBound = false;
    mpService musicService;
    Intent IMusicService;
    private timer t;
    long songDuration;
    boolean isPaused = false;
    boolean isShuffleOn = false;
    Random rand;




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
            musicService.registerCallBacksClient(player.this);
            musicService.getSong(songUris[songIndex]);
            updateTextViews();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mpServiceBound =false;
        }
    };


    private void initializeViewComponents()
    {
        btnGoBack = (ImageButton)findViewById(R.id.buttonBack);
        btnStart = (ImageButton)findViewById(R.id.buttonStart);
        btnPause = (ImageButton)findViewById(R.id.buttonPause);
        btnStop = (ImageButton)findViewById(R.id.buttonStop);
        btnPrevious = (ImageButton)findViewById(R.id.buttonPrev);
        btnNext = (ImageButton)findViewById(R.id.buttonNext);
        btnShuffle = (ImageButton)findViewById(R.id.imageButtonShuffle);


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
                    if(isPaused) {
                        if (t != null) t.start();
                        isPaused = false;
                    }
                }



            }
        });
        /**
         * Pause button
         *
         */
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mpServiceBound)
                    musicService.pauseSong();
                if(t!=null)
                {
                    t.cancel();
                    t = new timer(player.this,songDuration-musicService.getDuration(),1000);
                    isPaused =true;
                }
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

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mpServiceBound)
                {
                    if(!isShuffleOn) {
                        //making sure index will remain in range
                        if (songIndex == songCount - 1) {
                            songIndex = 0;
                            musicService.getSong(songUris[songIndex]);
                            updateTextViews();
                        } else {
                            songIndex++;
                            musicService.getSong(songUris[songIndex]);
                            updateTextViews();
                        }
                    }
                    else
                    {
                        songIndex = getRandomIndex();
                        musicService.getSong(songUris[songIndex]);
                        updateTextViews();
                    }
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mpServiceBound)
                {
                    if(!isShuffleOn) {
                        if (songIndex == 0) {
                            songIndex = songCount - 1;
                            musicService.getSong(songUris[songIndex]);
                            updateTextViews();
                        } else {
                            songIndex--;
                            musicService.getSong(songUris[songIndex]);
                            updateTextViews();
                        }
                    }
                    else
                    {
                        songIndex = getRandomIndex();
                        musicService.getSong(songUris[songIndex]);
                        updateTextViews();
                    }
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShuffleOn)
                {
                    btnShuffle.setImageResource(R.drawable.shuffleoff);
                    isShuffleOn = false;
                }
                else
                {
                    btnShuffle.setImageResource(R.drawable.shuffleon);
                    isShuffleOn = true;
                    rand = new Random();
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

    }

    /**
     * Changing the activity focus to @playlist
     */
    private void startPlaylistActivity() {
        Intent Iplaylist = new Intent(player.this, playlist.class);
        startActivity(Iplaylist);

    }

    private void updateTextViews()
    {
        tvSongName.setText(songIndex + 1 + ". " + songNames[songIndex]);
        //tvSongDuration.setText(""+musicService.getDuration()/1000);
    }

    /**
     * Implemented interface, allows us to communicate with service
     * @param milis getting track duration in miliseconds
     */
    @Override
    public void updateClient(long milis) {
        if(t!=null) {
            t.cancel();
            t = new timer(player.this, milis, 1000);
            t.start();
        }
        else {
            t = new timer(player.this, milis, 1000);
            t.start();
        }
        songDuration = milis;
    }

    /**
     * changing song when mpService notify that the track has ended
     * @param onSongComplition
     */
    @Override
    public void updateClient(boolean onSongComplition) {
        if(mpServiceBound && onSongComplition)
        {
            if(!isShuffleOn) {
                //making sure index will remain in range
                if (songIndex == songCount - 1) {
                    songIndex = 0;
                    musicService.getSong(songUris[songIndex]);
                    updateTextViews();
                } else {
                    songIndex++;
                    musicService.getSong(songUris[songIndex]);
                    updateTextViews();
                }
            }
            else
            {
                songIndex = getRandomIndex();
                musicService.getSong(songUris[songIndex]);
                updateTextViews();
            }
        }

    }

    public TextView getTvSongDuration()
    {
        return tvSongDuration;
    }

    private int getRandomIndex()
    {
        return rand.nextInt(songCount);

    }
}