package com.example.huddy.mp3player;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

/**
 * Created by huddy on 12/15/15.
 */
public class NotificationHelper {
    private Context mContext;
    private Intent mIntent;
    MusicPlayerService mpService;

    public NotificationHelper(MusicPlayerService mpService, Context mContext, Intent mIntent)
    {
        this.mContext = mContext;
        this.mIntent = mIntent;
        this.mpService =mpService;
    }
    public NotificationCompat.Builder createCurrentSongNotificationBuilder()
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mpService);
        mBuilder.setSmallIcon(R.drawable.icon);
        try{
            Bitmap cover = mpService.getCurrentSong().getCover();
            int height = (int)mpService.getApplicationContext().getResources().getDimension(R.dimen.notification_large_icon_height);
            int width = (int)mpService.getApplicationContext().getResources().getDimension(R.dimen.notification_large_icon_width);
            cover=cover.createScaledBitmap(cover,height,width,false);
            mBuilder.setLargeIcon(cover);
        }
        catch (Exception e)
        {
            //mBuilder.setSmallIcon(R.drawable.icon);
        }

        mBuilder.setContentTitle("Currently playing: ")
                .setContentText(mpService.getCurrentSong().getAuthor() + "\n" + mpService.getCurrentSong().getTitle())
                .setContentIntent(createPendingIntent())
                .setDeleteIntent(createActionPendingIntent(ActionConstants.ACTION_EXIT))
                .addAction(R.drawable.previous, "", createActionPendingIntent(ActionConstants.ACTION_PREVIOUS));
        //cant close notification
        //.setOngoing(true);
        if(mpService.isPaused())
        {
            mBuilder.addAction(R.drawable.play, "", createActionPendingIntent(ActionConstants.ACTION_PLAY));
        }
        else mBuilder.addAction(R.drawable.pause, "", createActionPendingIntent(ActionConstants.ACTION_PLAY));
        mBuilder.addAction(R.drawable.next, "", createActionPendingIntent(ActionConstants.ACTION_NEXT));
        return mBuilder;

    }

    private TaskStackBuilder addIntentToTaskStackBuilder()
    {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(PlayerActivity.class);
        stackBuilder.addNextIntent(mIntent);
        return stackBuilder;
    }

    private PendingIntent createPendingIntent()
    {
        PendingIntent resultPendingIntent = addIntentToTaskStackBuilder().getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        return resultPendingIntent;

    }
    private PendingIntent createActionPendingIntent(String action)
    {
        Intent actionIntent = new Intent();
        actionIntent.setAction(action);
        return PendingIntent.getBroadcast(mpService, 0, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
