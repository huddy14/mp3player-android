package com.example.huddy.mp3player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotificationActionReciever extends BroadcastReceiver {
    private MusicPlayerService mpService;
    public NotificationActionReciever(MusicPlayerService mpService)
    {
        this.mpService = mpService;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ActionConstants.ACTION_PLAY:
                if(mpService.isPlaying())
                    mpService.pauseSong();
                else mpService.playSong();
                break;
            case ActionConstants.ACTION_NEXT:
                try {
                    mpService.nextSong();
                }
                catch (Exception e){

                }
                break;
            case ActionConstants.ACTION_PREVIOUS:
                mpService.previousSong();
                break;
            case ActionConstants.ACTION_EXIT:
                android.os.Process.killProcess(android.os.Process.myPid());
                mpService.stopSelf();
                break;
            default:
                break;
        }
    }
}
