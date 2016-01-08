package com.example.huddy.mp3player;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

/**
 * Created by huddy on 12/16/15.
 */
public class SongDownloadManager {
    //private String songURL = "http://qypbr.yt-downloader.org/download.php?id=b0ec2be3f201dc88b329f23bca3b0bb8";
    private DownloadManager mDownloadManager;
    private Context mContext;
    private final String youtubeURLheader = "http://www.youtubeinmp3.com/fetch/?video=http://www.youtube.com/watch?v=";

    public SongDownloadManager(Context mContext)
    {
        this.mContext = mContext;
    }

    public void download(String songID, String tittle)
    {
        String youtubeSongUrl = youtubeURLheader + songID;
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri songUri = Uri.parse(youtubeSongUrl);
        DownloadManager.Request request = new DownloadManager.Request(songUri);
        request.setDescription("pobieram");
        request.setTitle(tittle);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tittle+".mp3");
        //request.setVisibleInDownloadsUi(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        mDownloadManager.enqueue(request);
    }
}
