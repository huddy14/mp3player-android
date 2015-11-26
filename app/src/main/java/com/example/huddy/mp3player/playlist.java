package com.example.huddy.mp3player;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class playlist extends AppCompatActivity {

    ListView lv;
    String[] items;
    String[] songUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        final Bundle extras = getIntent().getExtras();

        lv = (ListView) findViewById(R.id.PlayList);
        final ArrayList<File> mySongs;
        mySongs = findSongs(Environment.getExternalStorageDirectory());
        items = new String[ mySongs.size() ];
        songUris = new String[ mySongs.size() ];
        for(int i=0;i<mySongs.size();i++)
        {
            toast(mySongs.get(i).getName().toString());
            items[i] = mySongs.get(i).getName().toString();
            songUris[i] = mySongs.get(i).getAbsolutePath();
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,items);
        lv.setAdapter(adp);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = ((TextView)view).getText().toString();
                //int i = parent.getSelectedItemPosition();
                String songUriString = mySongs.get(position).toURI().toString();
                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();

                Intent playerIntent = new Intent(playlist.this, player.class);
                playerIntent.putExtra("SELECTED_SONG_URI_STRING",songUris);
                playerIntent.putExtra("SELECTED_SONG_NAME",items);
                playerIntent.putExtra("INDEX",position);
                playerIntent.putExtra("COUNT",mySongs.size());
                //playerIntent.putExtra("ISPLAYING",extras.getBoolean("ISPLAYING"))
                startActivity(playerIntent);
            }
        });
    }





    public ArrayList<File> findSongs(File root)
    {
        ArrayList<File> all = new ArrayList<File>();
        File[] files = root.listFiles();
        for (File singleFile: files)
        {
            if(singleFile.isDirectory() && !singleFile.isHidden())
            {
                all.addAll(findSongs(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".txt") || singleFile.getName().endsWith(".mp3"))
                {
                    all.add(singleFile);
                }
            }
        }
        return all;

    }

    public void toast(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
