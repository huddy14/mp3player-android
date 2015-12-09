package com.example.huddy.mp3player;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by huddy on 12/9/15.
 */
public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private PlayerActivity mPlayerActivity;
    public GestureListener(PlayerActivity mPlayerActivity)
    {
        this.mPlayerActivity = mPlayerActivity;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            mPlayerActivity.musicService.nextSong();
            return false; // Right to left
        }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            mPlayerActivity.musicService.previousSong();

            return false; // Left to right
        }
        return false;
    }
}
