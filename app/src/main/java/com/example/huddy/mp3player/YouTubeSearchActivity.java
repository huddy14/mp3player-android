package com.example.huddy.mp3player;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class YouTubeSearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private ListView videosFound;
    private YouTubeItemAdapter youTubeItemAdapter;
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
        class GetYoutubeResults extends AsyncTask<Void,Void,Void>
        {
            ProgressDialog loading;
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getActiveNetworkInfo();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(YouTubeSearchActivity.this,"Fetching data","Please wait...",false,false);

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loading.dismiss();
                updateVideosFound();
            }

            @Override
            protected Void doInBackground(Void... params) {
                //TODO: put some ifs so updateVideosFound wont return null pointer, fix this shit
                //if(mWifi.isConnected() && mWifi.getType() == ConnectivityManager.TYPE_WIFI) {
                    YouTubeConnecter yc = new YouTubeConnecter(getApplicationContext());
                    searchResults = yc.search(keywords);
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
        GetYoutubeResults results = new GetYoutubeResults();
        results.execute();
    }

    private void updateVideosFound() {
        youTubeItemAdapter = new YouTubeItemAdapter(this,R.layout.youtube_list_item,searchResults);
        videosFound.setAdapter(youTubeItemAdapter);
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
