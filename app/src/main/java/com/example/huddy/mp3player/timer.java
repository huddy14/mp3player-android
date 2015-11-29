package com.example.huddy.mp3player;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by Student on 2015-11-04.
 */
public class timer extends CountDownTimer{
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     *
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     *                          x = Tmp / 600 000;
     *                          y = ( Tmp % 600 000) / 60 000;
     *                          z = ( Tmp % 60 000) / 10 000;
     *                          t = ( Tmp % 10 000) / 1000;
     */

    private player MyA;
    private TextView Counter;
    public timer(player MyA, long millisInFuture, long countDownInterval) {
        super(millisInFuture , countDownInterval);
        this.MyA = MyA;
    }
    /*
    We calculate the textview output for every second till finish
     */

    private String updateCounter(long mUF)
    {
        return new String(TimeUnit.MILLISECONDS.toMinutes(mUF)+" : "+(mUF%60000)/10000+(mUF%10000)/1000);
    }
    @Override
    public void onTick(long millisUntilFinished) {

        MyA.getTvSongDuration().setText(updateCounter(millisUntilFinished));
        MyA.getSeekBar().setProgress(MyA.getSongDuration()-(int)millisUntilFinished);

    }

    @Override
    public void onFinish() {
        MyA.getTvSongDuration().setText("-");


    }
}