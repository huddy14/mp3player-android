package com.example.huddy.mp3player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huddy on 1/9/16.
 */
public class YouTubeItemAdapter extends ArrayAdapter<YouTubeItem> {

    private List<YouTubeItem> playlist;
    private Context context;

    public YouTubeItemAdapter(Context context, int resource, List<YouTubeItem> objects) {
        super(context, resource, objects);
        playlist = objects;
        this.context=context;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.youtube_list_item, parent, false);
        }
        YouTubeItem item = playlist.get(position);

        if(item!=null) {

            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
            TextView title = (TextView) convertView.findViewById(R.id.video_title);
            TextView description = (TextView) convertView.findViewById(R.id.video_description);

            YouTubeItem searchResult = playlist.get(position);

            Picasso.with(context).load(searchResult.getThumbnailURL()).into(thumbnail);
            title.setText(searchResult.getTitle());
            description.setText(searchResult.getDescription());
        }
        return convertView;
    }
}
