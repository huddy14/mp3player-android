package com.example.huddy.mp3player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * TODO: BROADCAST! songAdapter, songList where to put it!?!?!?!?!?!
 */

public class player extends AppCompatActivity implements mpService.CallBacks{

    /**
     * Variables
     */
    ImageButton btnGoBack, btnStart, btnStop, btnPause, btnNext, btnPrevious, btnShuffle;
    TextView tvSongName, tvTimeToEnd, tvTimeElapsed;
    ArrayList<song>songList;
    String songTittle[],songAuthor[];
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
            if (songList.size() > 0)
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
        if(songList.size() > 0){
            if(!isMusicServiceConnected) {
                IMusicService = new Intent(this, mpService.class);
                IMusicService.putExtra("SONGLIST", new songDataWrapper(songList));
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
            if(songList.size() > 0) {
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
        btnStart = (ImageButton)findViewById(R.id.imageButtonPlayPause);
        //btnPause = (ImageButton)findViewById(R.id.buttonPause);
        //btnStop = (ImageButton)findViewById(R.id.buttonStop);
        btnPrevious = (ImageButton)findViewById(R.id.buttonPrev);
        btnNext = (ImageButton)findViewById(R.id.buttonNext);
        //btnShuffle = (ImageButton)findViewById(R.id.imageButtonShuffle);
        seekBar = (SeekBar)findViewById(R.id.seekBar);


        tvTimeToEnd = (TextView)findViewById(R.id.textViewTimeToEnd);
        tvTimeElapsed = (TextView)findViewById(R.id.textViewTimeElapsed);
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
                        musicService.playSong();
                        btnStart.setImageResource(R.drawable.pausebig);
                    }
                    else
                    {
                        musicService.pauseSong();
                        btnStart.setImageResource(R.drawable.playbig);
                        if(t!=null)
                        {
                            t.cancel();
                            t = new timer(player.this,songDuration-musicService.getDuration(),10);
                        }
                    }
                }
            }
        });
        /**
         * Pause button
         *
         */
//        btnPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isMusicServiceConnected)
//                    musicService.pauseSong();
//                if(t!=null)
//                {
//                    t.cancel();
//                    t = new timer(player.this,songDuration-musicService.getDuration(),10);
//                }
//            }
//        });
//
//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isMusicServiceConnected) {
//                    musicService.stopSong();
//                    startPlaylistActivity();
//                }
//            }
//        });

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

//        btnShuffle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(musicService.isShuffle())
//                {
//                    btnShuffle.setImageResource(R.drawable.shuffleoff);
//                    musicService.shuffle();
//                }
//                else
//                {
//                    btnShuffle.setImageResource(R.drawable.shuffleon);
//                    musicService.shuffle();
//                }
//            }
//        });
    }

    private void setVariables()
    {
        songList = new ArrayList<>();
        Cursor cursor = populateSongQueries(this);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
                songList.add(new song(cursor));
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
                String str = data.getStringExtra("ID");
                for( song s : songList) {
                    if (str.equals(s.getId())) {
                        songIndex = songList.indexOf(s);
                    }
                }
                if(isMusicServiceConnected)
                {
                    musicService.setSongIndex(songIndex);
                    musicService.setSong(songList.get(songIndex).getPath());
                    updateTextViews();
                }
            }
    }


    /**
     * Changing the activity focus to @playlist
     */
    private void startPlaylistActivity() {
        Intent Iplaylist = new Intent(player.this, playlist.class);
        songDataWrapper dataWraper = new songDataWrapper(songList);
        Iplaylist.putExtra("SONGLIST", dataWraper);
        startActivityForResult(Iplaylist, 1);

    }

    private void updateTextViews()
    {
        tvSongName.setText(songList.get(songIndex).getAuthor() + "\n" + songList.get(songIndex).getTitle());

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

    public TextView getTvTimeToEnd()
    {
        return tvTimeToEnd;
    }
    public TextView getTvTimeElapsed()
    {
        return  this.tvTimeElapsed;
    }
    public SeekBar getSeekBar() { return seekBar;}

    private boolean checkIfExternalStorageExists()
    {
        if(Environment.getExternalStorageDirectory() != null)
            return true;
        return false;

    }

    private Cursor populateSongQueries(Context context) {

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

    /**
     * Created by Student on 2015-11-04.
     */
    private class timer extends CountDownTimer {

        private player mPlayer;
        public timer(player mPlayer, long songDuration, long countDownInterval) {
            super(songDuration , countDownInterval);
            this.mPlayer = mPlayer;
        }

        private String updateCounter(long mUF)
        {
            return new String(TimeUnit.MILLISECONDS.toMinutes(mUF)+" : "+(mUF%60000)/10000+(mUF%10000)/1000);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            mPlayer.getTvTimeElapsed().setText(updateCounter(mPlayer.getSongDuration()-(int)millisUntilFinished));
            mPlayer.getTvTimeToEnd().setText(updateCounter(millisUntilFinished));
            mPlayer.getSeekBar().setProgress(mPlayer.getSongDuration()-(int)millisUntilFinished);

        }

        @Override
        public void onFinish() {
            mPlayer.getTvTimeToEnd().setText("-");
            mPlayer.getTvTimeElapsed().setText("");
        }
    }
}