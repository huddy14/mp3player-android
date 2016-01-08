package com.example.huddy.mp3player;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.logging.LogRecord;
import android.os.Handler;

import com.squareup.picasso.Picasso;

public class YouTubeSearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private ListView videosFound;
    private Handler handler;
    private Button btn;
    private List<YouTubeItem> searchResults;
    private final String youtubeURLheader = "https://www.youtube.com/watch?v=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_search);
        searchInput = (EditText)findViewById(R.id.editText);
        videosFound = (ListView)findViewById(R.id.listView);
        btn = (Button)findViewById(R.id.button);
        handler = new Handler();
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });

        addClickListener();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOnYoutube(searchInput.getText().toString());
            }
        });

    }

    private void searchOnYoutube(final String keywords)
    {
        new Thread(){
            public void run(){
                YouTubeConnecter yc = new YouTubeConnecter(getApplicationContext());
                searchResults = yc.search(keywords);
                handler.post(new Runnable(){
                    public void run(){
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    private void updateVideosFound() {
        ArrayAdapter<YouTubeItem> adapter = new ArrayAdapter<YouTubeItem>(getApplicationContext(), R.layout.youtube_list_item, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.youtube_list_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                YouTubeItem searchResult = searchResults.get(position);

                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosFound.setAdapter(adapter);
    }

    private void addClickListener(){
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
                SongDownloadManager mSongDownload = new SongDownloadManager(getApplicationContext());
                Object s =  av.getAdapter().getItem(pos);
                YouTubeItem youtubeItem = (YouTubeItem)s;
                mSongDownload.download(youtubeItem.getId(),youtubeItem.getTitle());
            }

        });
    }
}
