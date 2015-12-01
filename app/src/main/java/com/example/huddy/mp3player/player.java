package com.example.huddy.mp3player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

/**
 * TODO: REFACTOR! implement exceptions
 */

public class player extends AppCompatActivity implements mpService.CallBacks{

    /**
     * Variables
     */
    ImageButton btnGoBack, btnStart, btnStop, btnPause, btnNext, btnPrevious, btnShuffle;
    TextView tvSongName,tvSongDuration;
    String songUris[],songNames[];
    int songIndex;
    boolean isMusicServiceConnected = false;
    mpService musicService;
    Intent IMusicService;
    private timer t;
    long songDuration;
    SeekBar seekBar;

    //TODO: fix it, to many ifs
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        if(checkIfExternalStorageExists()) {
            setVariables();
            if (songUris.length > 0)
                initializeViewComponents();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No music was found on this device \n U may exit",
                    Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(songUris.length > 0){
            if(!isMusicServiceConnected) {
                IMusicService = new Intent(this, mpService.class);
                IMusicService.putExtra("URIS", songUris);
                IMusicService.putExtra("NAMES", songNames);
                startService(IMusicService);
                bindService(IMusicService, MusicServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No music was found on this device \n U may exit",
                    Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isMusicServiceConnected)
            unbindService(MusicServiceConnection);
    }

    //TODO: dont start the player with music on, same goes for next, previous buttons if paused music shouldnt be playing
    private ServiceConnection MusicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(songUris.length > 0) {
                mpService.MyBinder myBinder = (mpService.MyBinder) service;
                musicService = myBinder.getService();
                isMusicServiceConnected = true;
                musicService.registerCallBacksClient(player.this);
                updateTextViews();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isMusicServiceConnected =false;
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
        seekBar = (SeekBar)findViewById(R.id.seekBar);


        tvSongDuration = (TextView)findViewById(R.id.textViewSongTime);
        tvSongName = (TextView)findViewById(R.id.textViewSongName);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser&&isMusicServiceConnected)
                {
                    if(t!=null)
                    {
                        t.cancel();
                        t = new timer(player.this,songDuration-progress,10);
                    }
                    musicService.seekTo(progress);
                    seekBar.setProgress(progress);
                    if(!musicService.isPaused())t.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                    //musicService.previousSong();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                   // musicService.nextSong();
            }
        });
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaylistActivity();
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMusicServiceConnected) {
                    if(musicService.isPaused()) {
                        if (t != null) t.start();
                    }
                    musicService.playSong();

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
                if(isMusicServiceConnected)
                    musicService.pauseSong();
                if(t!=null)
                {
                    t.cancel();
                    t = new timer(player.this,songDuration-musicService.getDuration(),10);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMusicServiceConnected) {
                    musicService.stopSong();
                    startPlaylistActivity();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMusicServiceConnected)
                {
                    musicService.nextSong();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMusicServiceConnected)
                {
                    musicService.previousSong();
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService.isShuffle())
                {
                    btnShuffle.setImageResource(R.drawable.shuffleoff);
                    musicService.shuffle();
                }
                else
                {
                    btnShuffle.setImageResource(R.drawable.shuffleon);
                    musicService.shuffle();
                }
            }
        });
    }

    private void setVariables()
    {
        final ArrayList<File> mySongs;
        //adding sound from external storage
        mySongs = findSongs(Environment.getExternalStorageDirectory());
        //mySongs.addAll(findSongs(Environment.getRootDirectory()));
        songNames = new String[ mySongs.size() ];
        songUris = new String[ mySongs.size() ];
        for(int i=0;i<mySongs.size();i++)
        {
            songNames[i] = mySongs.get(i).getName().toString();
            songUris[i] = mySongs.get(i).getAbsolutePath();
        }

    }

    /**
     * here we wait for playlist to send us back index of song we should play next
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
            if(resultCode==RESULT_OK)
            {
                songIndex = data.getIntExtra("INDEX",1);
                if(isMusicServiceConnected)
                {
                    musicService.setSongIndex(songIndex);
                    musicService.setSong(songUris[songIndex]);
                    updateTextViews();
                }
            }
    }


    /**
     * Changing the activity focus to @playlist
     */
    private void startPlaylistActivity() {
        Intent Iplaylist = new Intent(player.this, playlist.class);
        Iplaylist.putExtra("SONGNAMES",songNames);
        startActivityForResult(Iplaylist, 1);

    }

    private void updateTextViews()
    {
        tvSongName.setText(songIndex + 1 + ". " + songNames[songIndex]);

    }

    /**
     * Implemented interface, allows us to communicate with service
     * @param milis getting track duration in miliseconds
     */
    @Override
    public void updateClient(long milis) {
        if(t!=null) {
            t.cancel();
            t = new timer(player.this, milis, 10);
            t.start();
        }
        else {
            t = new timer(player.this, milis, 10);
            t.start();
        }
        songDuration = milis;
        seekBar.setMax((int) milis);
    }

    public int getSongDuration()
    {
        return (int)songDuration;
    }

    @Override
    public void updateIndex(int i) {
        this.songIndex = i;
        updateTextViews();
    }

    @Override
    public void updateClient(int songDuration, int songCurrentPosition) {
        if(t!=null) {
            t.cancel();
            t = new timer(player.this, songDuration-songCurrentPosition, 10);
            t.start();
        }
        else {
            t = new timer(player.this, songDuration-songCurrentPosition, 10);
            t.start();
        }
        this.songDuration = songDuration;
        seekBar.setMax(songDuration);
    }

    public TextView getTvSongDuration()
    {
        return tvSongDuration;
    }
    public SeekBar getSeekBar() { return seekBar;}

    private boolean checkIfExternalStorageExists()
    {
        if(Environment.getExternalStorageDirectory() != null)
            return true;
        return false;

    }

    //TODO: add mp3 and wav files from internal storage !
    private ArrayList<File> findSongs(File root)
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