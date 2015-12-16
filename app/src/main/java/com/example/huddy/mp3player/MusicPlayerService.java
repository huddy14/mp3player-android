/**
 * TODO: timer should be implemented here, make function shorter, dont repeat code
 */
package com.example.huddy.mp3player;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Binder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MusicPlayerService extends Service {
    private MediaPlayer mp = new MediaPlayer();
    private IBinder mpBinder = new MyBinder();
    private int duration,songIndex=0,songCount=0;
    private CallBacksToPlayerActivity activity = null;
    private NotificationActionReciever myReceiver;
    private boolean shuffle = false,pause = true,isServiceRuning=false;
    private Random rand;
    private NotificationManager notificationManager;
    private ArrayList<Song>songList;

    @Override
    public IBinder onBind(Intent intent) {
        setSong(songList.get(songIndex).getPath());
        return mpBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * this function is loaded only once, when the service starts
     * we get songs uris from PlayerActivity class and service is set to START_NOT_STICKY
     * which means it wont reload when crushed
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SongDataWrapper dw = (SongDataWrapper)intent.getSerializableExtra("SONGLIST");
        callRegisterReceiver();
        songList = dw.getSongList();
        songCount = songList.size();
        isServiceRuning = true;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        isServiceRuning = false;
        notificationManager.cancel(1);
    }

    /**
     * this snippet is used to bound together activity and this service
     */
    public class MyBinder extends Binder {
        MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    public void playSong ()
    {
        mp.start();
        pause = false;
        tryUpdatePlayPauseButton(true);
        currentlyPlayingSongNotification();
    }

    public void pauseSong()
    {
        if(mp.isPlaying()) {
            mp.pause();
            pause = true;
            tryUpdatePlayPauseButton(false);
            currentlyPlayingSongNotification();
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

    public boolean isPlaying()
    {
        return this.mp.isPlaying();
    }

    public void setSong (String song) {
        mp.reset();
        try {
            mp.setDataSource(this, Uri.parse(song));
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    duration = mp.getDuration();
                    if (!pause) {
                        mp.start();
                    }
                    if (activity != null)
                        activity.seekBarUpdatePossible();
                    currentlyPlayingSongNotification();
                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextSong();
                    activity.updateIndex(songIndex);
                }
            });

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
                setSong(songList.get(songIndex).getPath());
                activity.updateIndex(songIndex);

            } else {
                songIndex++;
                setSong(songList.get(songIndex).getPath());
                activity.updateIndex(songIndex);
            }
        }
        else
        {
            songIndex = getRandomIndex();
            setSong(songList.get(songIndex).getPath());
            activity.updateIndex(songIndex);
        }
    }

    public void previousSong()
    {
        if(!shuffle) {
            //making sure index will remain in range
            if (songIndex == 0) {
                songIndex = songCount-1;
                setSong(songList.get(songIndex).getPath());
                activity.updateIndex(songIndex);
            } else {
                songIndex--;
                setSong(songList.get(songIndex).getPath());
                activity.updateIndex(songIndex);
            }
        }
        else
        {
            songIndex = getRandomIndex();
            setSong(songList.get(songIndex).getPath());
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
        if(activity!=null)
            activity.updateShuffleButton(shuffle);
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

    public int getCurrentPosition()
    {
        return this.mp.getCurrentPosition();
    }

    private int getRandomIndex()
    {
        return rand.nextInt(songCount);
    }

    public void seekTo(int time)
    {
        mp.seekTo(time);
    }

    private void tryUpdatePlayPauseButton(boolean isPlaying)
    {
        if(activity!=null)
            activity.updatePlayPauseButton(isPlaying);
    }

    public void currentlyPlayingSongNotification()
    {

        NotificationHelper mHelper = new NotificationHelper(this,this,new Intent(this, PlayerActivity.class));
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mHelper.createCurrentSongNotificationBuilder().build());

    }

    public Song getCurrentSong()
    {
        return songList.get(songIndex);
    }

    /**
     * interfaces created to communicate with activities
     */
    public interface CallBacksToPlayerActivity {
        void updateIndex(int i);
        void seekBarUpdatePossible();
        void updatePlayPauseButton(boolean isPlaying);
        void updateShuffleButton(boolean isShuffle);
    }

    private void callRegisterReceiver()
    {
        //registering MyReciver reference adding action filter
        myReceiver = new NotificationActionReciever(this);
        registerReceiver(myReceiver,new IntentFilter(ActionConstants.ACTION_PLAY));
        registerReceiver(myReceiver,new IntentFilter(ActionConstants.ACTION_NEXT));
        registerReceiver(myReceiver, new IntentFilter(ActionConstants.ACTION_PREVIOUS));
        registerReceiver(myReceiver, new IntentFilter(ActionConstants.ACTION_PAUSE));
        registerReceiver(myReceiver, new IntentFilter(ActionConstants.ACTION_EXIT));
    }

    /**
     * all activities have to be registred
     * @param activity
     */
    public void registerCallBacksClient(Activity activity)
    {
        this.activity = (CallBacksToPlayerActivity)activity;
        this.activity.updateIndex(songIndex);
        this.activity.updatePlayPauseButton(isPlaying());
    }
}