package com.example.huddy.mp3player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by huddy on 12/3/15.
 */
public class SongDataWrapper implements Serializable {
    private ArrayList<Song> songList = new ArrayList<>();

    public SongDataWrapper(ArrayList<Song> songList)
    {
        this.songList = songList;
    }

    public ArrayList<Song> getSongList()
    {
        return this.songList;
    }

}
