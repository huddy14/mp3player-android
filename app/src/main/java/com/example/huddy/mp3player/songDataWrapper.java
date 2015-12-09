package com.example.huddy.mp3player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by huddy on 12/3/15.
 */
public class songDataWrapper implements Serializable {
    private ArrayList<song> songList = new ArrayList<>();
    public songDataWrapper(ArrayList<song> songList)
    {
        this.songList = songList;
    }
    public ArrayList<song> getSongList()
    {
        return this.songList;
    }

}
