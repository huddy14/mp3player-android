package com.example.huddy.mp3player;


import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by huddy on 12/2/15.
 */
public class song implements Serializable{
    private String title,author,path,id,duration;

    public song(Cursor songCursor)
    {
        title = songCursor.getString(0);
        author = songCursor.getString(1);
        path = songCursor.getString(2);
        duration = songCursor.getString(3);
        id = songCursor.getString(4);
    }

    public String getTitle()
    {
        return this.title;
    }
    public String getAuthor()
    {
        return this.author;
    }
    public String getPath()
    {
        return this.path;
    }
    public String getId()
    {
        return this.id;
    }
    public String getDuration()
    {
        return this.duration;
    }
}
