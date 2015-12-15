package com.example.huddy.mp3player;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.Serializable;

/**
 * Created by huddy on 12/2/15.
 */
public class Song implements Serializable{
    private String title,author,path,id,duration;
    public Song(Cursor songCursor)
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

    public Bitmap getCover()
    {
        byte[]coverPic;
        MediaMetadataRetriever mMetadataRetriver = new MediaMetadataRetriever();
        try
        {
            mMetadataRetriver.setDataSource(this.path);
            coverPic = mMetadataRetriver.getEmbeddedPicture();
            return BitmapFactory.decodeByteArray(coverPic,0,coverPic.length);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
