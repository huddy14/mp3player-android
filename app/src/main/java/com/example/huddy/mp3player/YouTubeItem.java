package com.example.huddy.mp3player;

/**
 * Created by huddy on 1/8/16.
 */
public class YouTubeItem {
    private String title;
    private String description;
    private String thumbnailURL;
    private String id;

    public YouTubeItem(String tittle, String description, String thumbnailURL, String id)
    {
        this.id=id;
        this.thumbnailURL=thumbnailURL;
        this.description=description;
        this.title=tittle;
    }
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }
}
