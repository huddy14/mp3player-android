package com.example.huddy.mp3player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

/**
 * TODO: BROADCAST! SongAdapter, songList where to put it!?!?!?!?!?!
 */

public class PlayerActivity extends Activity implements MusicPlayerService.CallBacksToPlayerActivity,ServiceConnection {


    /**
     * Variables
     */
    ImageButton btnGoBack, btnStart, btnDownload,btnGenres, btnNext, btnPrevious, btnShuffle;
    ImageView songCover;
    TextView tvArtist,tvTittle, tvTimeToEnd, tvTimeElapsed,tvIndex;
    ArrayList<Song> songList;
    int songIndex;
    boolean isMusicServiceConnected = false;
    MusicPlayerService musicService;
    Intent IMusicService;
    SeekBar seekBar;
    Handler mHandler;


    Runnable mSeekBarUpdater = new Runnable() {
        @Override
        public void run() {
            if (isMusicServiceConnected) {
                seekBar.setMax(musicService.getDuration());
                tvTimeElapsed.setText(updateCounter(musicService.getCurrentPosition()));
                tvTimeToEnd.setText(updateCounter(musicService.getDuration() - musicService.getCurrentPosition()));
                seekBar.setProgress(musicService.getCurrentPosition());
            }
            mHandler.postDelayed(this, 100);
        }
    };

    private String updateCounter(long mUF) {
        return new String(TimeUnit.MILLISECONDS.toMinutes(mUF) + " : " + (mUF % 60000) / 10000 + (mUF % 10000) / 1000);
    }

    //TODO: fix it, to many ifs
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        if (checkIfExternalStorageExists()) {
            setVariables();
            if (songList.size() > 0)
                initializeViewComponents();
        } else {
            Toast.makeText(getApplicationContext(), "No music was found on this device \n U may exit",
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (songList.size() > 0) {
            if (!isMusicServiceConnected) {
                IMusicService = new Intent(this, MusicPlayerService.class);
                IMusicService.putExtra("SONGLIST", new SongDataWrapper(songList));
                startService(IMusicService);
                bindService(IMusicService, this, Context.BIND_AUTO_CREATE);
            }
        } else {
            Toast.makeText(getApplicationContext(), "No music was found on this device \n U may exit",
                    Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new Handler();
        mSeekBarUpdater.run();
        //updatePlayPauseButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mSeekBarUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isMusicServiceConnected)
            unbindService(this);
    }



    private void initializeViewComponents() {
        btnGoBack = (ImageButton) findViewById(R.id.buttonBack);
        btnStart = (ImageButton) findViewById(R.id.imageButtonPlayPause);
        btnPrevious = (ImageButton) findViewById(R.id.buttonPrev);
        btnNext = (ImageButton) findViewById(R.id.buttonNext);
        btnShuffle = (ImageButton)findViewById(R.id.buttonShuffle);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnDownload = (ImageButton)findViewById(R.id.buttonDownload);
        btnGenres = (ImageButton)findViewById(R.id.buttonGenres);
        btnGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent genresActivity = new Intent(PlayerActivity.this,MusicGenresActivity.class);
                startActivity(genresActivity);
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SongDownloadManager mSongDownload = new SongDownloadManager(getApplicationContext());
                //mSongDownload.startDownload();
                Intent youtubeActivity = new Intent(PlayerActivity.this,YouTubeSearchActivity.class);
                startActivity(youtubeActivity);
            }
        });


        songCover = (ImageView)findViewById(R.id.imageViewCover);
        final GestureDetector gestureDetector = new GestureDetector(this.getApplicationContext(),new GestureListener(this));
        songCover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });


        tvTimeToEnd = (TextView) findViewById(R.id.textViewTimeToEnd);
        tvTimeElapsed = (TextView) findViewById(R.id.textViewTimeElapsed);
        tvArtist = (TextView) findViewById(R.id.textViewArtist);
        tvTittle = (TextView)findViewById(R.id.textViewTittle);
        tvIndex = (TextView)findViewById(R.id.textViewIndex);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isMusicServiceConnected) {
                    musicService.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //if(isMusicServiceConnected&&musicService.isPlaying())
                musicService.pauseSong();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //if(isMusicServiceConnected&&!musicService.isPlaying())
                musicService.playSong();
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
                if (isMusicServiceConnected) {
                    if (musicService.isPaused()) {
                        musicService.playSong();
                        //btnStart.setImageResource(R.drawable.pause);
                    } else {
                        musicService.pauseSong();
                        //btnStart.setImageResource(R.drawable.play);
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMusicServiceConnected) {
                    mHandler.removeCallbacks(mSeekBarUpdater);
                    musicService.nextSong();
                    //mHandler = new Handler();
                    //mSeekBarUpdater.run();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMusicServiceConnected) {
                    mHandler.removeCallbacks(mSeekBarUpdater);
                    musicService.previousSong();
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMusicServiceConnected)
                    musicService.shuffle();
            }
        });
    }

    private void setVariables() {
        songList = new ArrayList<>();
        Cursor cursor = populateSongQueries(this);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext())
                songList.add(new Song(cursor));
        }
    }

    /**
     * here we wait for PlaylistActivity to send us back index of Song we should play next
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                String str = data.getStringExtra("ID");
                for (Song s : songList) {
                    if (str.equals(s.getId())) {
                        songIndex = songList.indexOf(s);
                    }
                }
                if (isMusicServiceConnected) {
                    musicService.setSongIndex(songIndex);
                    musicService.setSong(songList.get(songIndex).getPath());
                    updateTextViews();
                    updateImageViewCover();
                }
            }
    }

    /**
     * Changing the activity focus to @PlaylistActivity
     */
    private void startPlaylistActivity() {
        Intent Iplaylist = new Intent(PlayerActivity.this, PlaylistActivity.class);
        SongDataWrapper dataWraper = new SongDataWrapper(songList);
        Iplaylist.putExtra("SONGLIST", dataWraper);
        startActivityForResult(Iplaylist, 1);
    }

    private void updateTextViews() {
        tvArtist.setText(songList.get(songIndex).getAuthor());
        tvTittle.setText(songList.get(songIndex).getTitle());
        tvIndex.setText(songIndex+1 + "/"+songList.size());

    }

    private void updateImageViewCover() {
        Bitmap cover = songList.get(songIndex).getCover();
        if(cover != null) {
            songCover.setImageBitmap(cover);
            songCover.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        else {
            songCover.setImageResource(R.drawable.default_track_icon);
            songCover.setScaleType(ImageView.ScaleType.CENTER);
        }
    }

    @Override
    public void updateIndex(int i) {
        this.songIndex = i;
        updateTextViews();
        updateImageViewCover();
    }

    @Override
    public void seekBarUpdatePossible() {
        mHandler = null;
        mHandler = new Handler();
        mSeekBarUpdater.run();
    }

    @Override
    public void updateShuffleButton(boolean isShuffle)
    {
        if(isShuffle)
            btnShuffle.setImageResource(R.drawable.shuffleon);
        else
            btnShuffle.setImageResource(R.drawable.shuffleoff);
    }
    @Override
    public void updatePlayPauseButton(boolean isPlaying)
    {
        if (isPlaying) {
            btnStart.setImageResource(R.drawable.pause);
        } else
            btnStart.setImageResource(R.drawable.play);
    }

    private boolean checkIfExternalStorageExists() {
        if (Environment.getExternalStorageDirectory() != null)
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (songList.size() > 0) {
            MusicPlayerService.MyBinder myBinder = (MusicPlayerService.MyBinder) service;
            musicService = myBinder.getService();
            isMusicServiceConnected = true;
            musicService.registerCallBacksClient(PlayerActivity.this);
            updateTextViews();
            updateImageViewCover();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isMusicServiceConnected = false;
        musicService = null;
    }
}