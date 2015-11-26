package com.example.huddy.mp3player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by huddy on 11/26/15.
 */
public class MusicService extends Service {

    private ArrayList<String> songsName;
    private ArrayList<String> songsUris;
    private int songIndex;
    private final IBinder musicBind = new MusicBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        mp.stop();
        mp.release();
        return false;
    }

    private MediaPlayer mp;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mp = new MediaPlayer();
    }

    public void setSongsName(ArrayList<String> songsName)
    {
        this.songsName = songsName;
    }

    public void setSongsUris(ArrayList<String>songsUris)
    {
        this.songsUris=songsUris;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong ()
    {
        //if(mp!=null)
            mp.start();
    }

    public void pauseSong()
    {
        if (mp != null)
            mp.pause();
    }

    public void stopSong()
    {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public void getSong (String song)
    {
        try {
            mp.setDataSource(getApplicationContext(), Uri.parse(song));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
