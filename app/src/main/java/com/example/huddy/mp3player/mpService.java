/**
 * TODO: timer should be implemented here, make function shorter, dont repeat code
 */
package com.example.huddy.mp3player;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Binder;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.util.Random;

public class mpService extends Service {

    private MediaPlayer mp = new MediaPlayer();
    private IBinder mpBinder = new MyBinder();
    private int duration,songIndex=0,songCount=0;
    private CallBacks activity = null;
    String songUris[],songNames[];
    boolean shuffle = false,pause = false,isServiceRuning=false;
    Random rand;
    Notification note;
    NotificationManager notificationManager;

    public mpService() {}

    @Override
    public IBinder onBind(Intent intent) {
        setSong(songUris[songIndex]);
        return mpBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    /**
     * this function is loaded only once, when the service starts
     * we get songs uris from player class and service is set to START_NOT_STICKY
     * which means it wont reload when crushed
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        songUris=intent.getStringArrayExtra("URIS");
        songNames=intent.getStringArrayExtra("NAMES");
        songCount = songUris.length;
        isServiceRuning = true;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        isServiceRuning = false;
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
        mp.start();
        pause = false;
    }

    public void pauseSong()
    {
        if(mp.isPlaying()) {
            mp.pause();
            duration = mp.getCurrentPosition();
            pause = true;
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
    public void setSong (String song) {
        mp.reset();
        try {
            mp.setDataSource(this, Uri.parse(song));
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    duration = mp.getDuration();
                    mp.start();
                    if (activity != null) {
                        activity.updateClient(duration);
                    }
                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextSong();
                    activity.updateIndex(songIndex);
                }
            });
            currentlyPlayingSongNotification();
        }
        catch (IOException e) {
                e.printStackTrace();
        }


    }

    public void nextSong()
    {
        if(!shuffle) {
            //making sure index will remain in range
            if (songIndex == songCount - 1) {
                songIndex = 0;
                setSong(songUris[songIndex]);
                activity.updateIndex(songIndex);

            } else {
                songIndex++;
                setSong(songUris[songIndex]);
                activity.updateIndex(songIndex);
            }
        }
        else
        {
            songIndex = getRandomIndex();
            setSong(songUris[songIndex]);
            activity.updateIndex(songIndex);
        }
    }

    public void previousSong()
    {
        if(!shuffle) {
            //making sure index will remain in range
            if (songIndex == 0) {
                songIndex = songCount-1;
                setSong(songUris[songIndex]);
                activity.updateIndex(songIndex);
                //updateTextViews();
            } else {
                songIndex--;
                setSong(songUris[songIndex]);
                activity.updateIndex(songIndex);
                //updateTextViews();
            }
        }
        else
        {
            songIndex = getRandomIndex();
            setSong(songUris[songIndex]);
            activity.updateIndex(songIndex);
        }
    }

    public void shuffle()
    {
        if(shuffle)
        {
            shuffle = false;
        }
        else
        {
            shuffle = true;
            rand = new Random();
        }
    }

    public int getCurrentSongPosition()
    {
        return this.mp.getCurrentPosition();
    }

    public boolean isShuffle()
    {
        return this.shuffle;
    }

    public boolean isPaused()
    {
        return this.pause;
    }

    public void setSongIndex(int songIndex)
    {
        this.songIndex = songIndex;
    }

    public int getDuration() {
        return this.duration;

    }

    private int getRandomIndex()
    {
        return rand.nextInt(songCount);

    }

    public void seekTo(int time)
    {
        if(pause) {
            mp.pause();
            mp.seekTo(time);
        }
        else
        {
            mp.pause();
            mp.seekTo(time);
            mp.start();
        }
    }

    public void currentlyPlayingSongNotification()
    {
        //NotificationCompat.Action.Builder mActionBuilder = new NotificationCompat.Action.Builder();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.icon);

        mBuilder.setContentTitle("Currently playing: ");
        mBuilder.setContentText(songNames[songIndex]);
        mBuilder.setContentIntent(createPendingIntent());
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,mBuilder.build());

    }

    private TaskStackBuilder addIntentToTaskStackBuilder()
    {
        Intent resultIntent = new Intent(this, player.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(player.class);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder;
    }

    private PendingIntent createPendingIntent()
    {
        PendingIntent resultPendingIntent = addIntentToTaskStackBuilder().getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;

    }

    /**
     * interfaces created to communicate with activities
     */
    public interface CallBacks{
        void updateClient(long milis);
        //void updateClient(boolean onSongComplition);
        void updateIndex(int i);
        void updateClient(int songDuration, int songCurrentPosition);
    }

    /**
     * all activities have to be registred
     * @param activity
     */
    public void registerCallBacksClient(Activity activity)
    {
        this.activity = (CallBacks)activity;
        this.activity.updateIndex(songIndex);
        this.activity.updateClient(getDuration(), getCurrentSongPosition());
    }


}
