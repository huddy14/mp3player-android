package com.example.huddy.mp3player;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class GenresPlaylistActivity extends AppCompatActivity {

    private List<YouTubeItem> playlist;
    private String playlistId;
    private ListView lvPlaylist;
    private YouTubeItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        playlistId = intent.getStringExtra("PLAYLIST_ID");
        setContentView(R.layout.activity_genres_playlist);
        lvPlaylist = (ListView)findViewById(R.id.lvPlaylist);
        getPlaylist(playlistId);

    }

    private void getPlaylist(final String playlistId)
    {
        class GetPlaylist extends AsyncTask<Void,Void,Void>
        {
            ProgressDialog loading;
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getActiveNetworkInfo();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GenresPlaylistActivity.this,"Fetching data","Please wait...",false,false);

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loading.dismiss();
                adapter = new YouTubeItemAdapter(GenresPlaylistActivity.this,R.layout.youtube_list_item,playlist);
                lvPlaylist.setAdapter(adapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                //TODO: put some ifs so updateVideosFound wont return null pointer, fix this shit
                //if(mWifi.isConnected() && mWifi.getType() == ConnectivityManager.TYPE_WIFI) {
                YouTubeConnecter yc = new YouTubeConnecter(getApplicationContext());
                playlist = yc.getSpecificChannelData(playlistId);
                return null;
                // }
//                else
//                {
//                    Toast.makeText(getApplicationContext(), "No wifi connection \ncant continue fetching",
//                            Toast.LENGTH_LONG).show();
//                    return null;
//                }
            }
        }
        GetPlaylist mPlaylist = new GetPlaylist();
        mPlaylist.execute();
    }
}
