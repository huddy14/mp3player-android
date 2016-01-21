package com.example.huddy.mp3player;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huddy on 1/8/16.
 */
public class YouTubeConnecter {
    private YouTube youtube;
    private YouTube.Search.List query;
    private YouTube.PlaylistItems.List playlist;

    // Your developer key goes here
    public static final String KEY
            = "AIzaSyA7SIqqWifjEKZwaGJNj73K4ELyKYLvBCM";

    public YouTubeConnecter(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {}
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try{
            query = youtube.search().list("id,snippet");
            query.setKey(KEY);
            query.setType("video");
            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
        }
        catch(IOException e){
            Log.d("YC", "Could not initialize: " + e);
        }
    }


    public List<YouTubeItem> getPlaylist(String playlistID)
    {
        try {
            playlist = youtube.playlistItems().list("snippet").setPlaylistId(playlistID);
            playlist.setKey(KEY);
            playlist.setMaxResults((long)50);

            //playlist.setId(spininRecordsID);
            playlist.setFields("items(snippet/resourceId/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            PlaylistItemListResponse playlistItems = playlist.execute();
            List<PlaylistItem> results = playlistItems.getItems();
            List<YouTubeItem> items = new ArrayList<>();


            for(PlaylistItem result:results)
            {
                YouTubeItem item = new YouTubeItem(result.getSnippet().getTitle(),result.getSnippet().getDescription(),
                    result.getSnippet().getThumbnails().getDefault().getUrl(),result.getSnippet().getResourceId().getVideoId());
                items.add(item);
            }
            return items;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    public List<YouTubeItem> search(String keywords){
        query.setQ(keywords);
        query.setMaxResults((long)20);
        try{
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            List<YouTubeItem> items = new ArrayList<>();
            for(SearchResult result:results){
                YouTubeItem item = new YouTubeItem(result.getSnippet().getTitle(),result.getSnippet().getDescription(),
                        result.getSnippet().getThumbnails().getDefault().getUrl(),result.getId().getVideoId());
//                item.setTitle(result.getSnippet().getTitle());
//                item.setDescription(result.getSnippet().getDescription());
//                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
//                item.setId(result.getId().getVideoId());

                items.add(item);
            }
            return items;
        }catch(IOException e){
            Log.d("YC", "Could not search: "+e);
            return null;
        }
    }
}