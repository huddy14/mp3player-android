package com.example.huddy.mp3player;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Binder;

import java.io.IOException;

public class mpService extends Service {

    private MediaPlayer mp = new MediaPlayer();
    private IBinder mpBinder = new MyBinder();
    private int duration;
    private CallBacks activity = null;
    public mpService() {}

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

    /**
     * this snippet is used to bound together activity and this service
     */
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
            if(mp.isPlaying()) {
                mp.pause();
                duration = mp.getCurrentPosition();
            }
    }

    public void stopSong()
    {
        if (mp != null) {
            mp.stop();
            mp.reset();
            //mp = null;
        }
    }
    //TODO: make sure all ifs, resets and releases are needed
    public void getSong (String song) {
        if(mp!=null)
        {
            mp.reset();
            try {
                mp.setDataSource(this, Uri.parse(song));
                mp.prepareAsync();
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        duration = mp.getDuration();
                        mp.start();
                        if(activity!=null)
                        {
                            activity.updateClient(duration);
                        }
                    }
                });
            //TODO:change it to be more usefull in case of exception
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public int getDuration() {
        return duration;

    }

    /**
     * interfaces created to communicate with activities
     */
    public interface CallBacks{
        void updateClient(long milis);
    }

    /**
     * all activities have to be registred
     * @param activity
     */
    public void registerCallBacksClient(Activity activity)
    {
        this.activity = (CallBacks)activity;
    }


}
