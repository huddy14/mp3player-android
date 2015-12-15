package com.example.huddy.mp3player;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

/**
 * Created by huddy on 12/15/15.
 */
public class PendingIntentHelper {
    private Context mContext;
    private Intent mIntent;
    MusicPlayerService mpService;

//    Intent pauseRecive = new Intent();
//    pauseRecive.setAction("pause");
//    Intent playRecieve = new Intent();
//    playRecieve.setAction("play");
//    PendingIntent pendingIntentPause = PendingIntent.getBroadcast(MusicPlayerService.this,12345,pauseRecive,PendingIntent.FLAG_UPDATE_CURRENT);
//    PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this,12345,playRecieve,PendingIntent.FLAG_UPDATE_CURRENT);



    public PendingIntentHelper(MusicPlayerService mpService,Context mContext, Intent mIntent)
    {
        this.mContext = mContext;
        this.mIntent = mIntent;
        this.mpService =mpService;
    }
    private TaskStackBuilder addIntentToTaskStackBuilder()
    {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(PlayerActivity.class);
        stackBuilder.addNextIntent(mIntent);
        return stackBuilder;
    }

    public PendingIntent createPendingIntent()
    {
        PendingIntent resultPendingIntent = addIntentToTaskStackBuilder().getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        return resultPendingIntent;

    }
    public PendingIntent createActionIntent(String action)
    {
        Intent actionIntent = new Intent();
        actionIntent.setAction(action);
        return PendingIntent.getBroadcast(mpService, 0, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
