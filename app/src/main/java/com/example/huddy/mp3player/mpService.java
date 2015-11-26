package com.example.huddy.mp3player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Binder;

import java.io.IOException;

public class mpService extends Service {

    private MediaPlayer mp = new MediaPlayer();
    private IBinder mpBinder = new MyBinder();
    public mpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mpBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mp != null)
        {
            mp.stop();
            mp.release();
        }
    }


    public class MyBinder extends Binder {
        mpService getService() {
            return mpService.this;
        }
    }

    public void playSong ()
    {
        if(mp!=null)
           mp.start();
    }

    public void pauseSong()
    {
        if (mp != null)
            if(mp.isPlaying())
                mp.pause();
    }

    public void stopSong()
    {
        if (mp != null) {
            mp.stop();
            mp.reset();
            //mp = null;
        }
    }

    public void getSong (String song) {
        if(mp!=null)
        {
            //mp.stop();
            mp.reset();
            try {
                mp.setDataSource(this, Uri.parse(song));
                mp.prepareAsync();
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public int getDuration() {
        return mp.getDuration();
    }


}
