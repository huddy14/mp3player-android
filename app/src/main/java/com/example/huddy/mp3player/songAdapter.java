package com.example.huddy.mp3player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by huddy on 12/2/15.
 */
public class songAdapter extends ArrayAdapter<song> implements Filterable {
    ArrayList<song> songList;
    final ArrayList<song> originalSongList;
    SongFilter songFilter;

    public songAdapter(Context context, int textViewResourceId, ArrayList<song> objects) {
        super(context, textViewResourceId, objects);
        this.songList = objects;
        this.originalSongList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.song_list_item, null);
        }
        song s = songList.get(position);

        if (s != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView Author = (TextView) view.findViewById(R.id.toptext);
            TextView Tittle = (TextView) view.findViewById(R.id.middletext);
            TextView Duration = (TextView) view.findViewById(R.id.topRightText);

            // check to see if each individual textview is null.
            // if not, assign some text!
            Author.setText(s.getAuthor());
            Tittle.setText(s.getTitle());
            int milis = Integer.parseInt(s.getDuration());
            Duration.setText(convertMillisToMMSS(milis));

        }

        // the view must be returned to our activity
        return view;

    }
    //preventing index going out of range, updates songlist size as expected
    @Override
    public int getCount() {
        return songList != null ? songList.size() : 0;
    }

    private String convertMillisToMMSS(int milis)
    {
        return new String(TimeUnit.MILLISECONDS.toMinutes(milis)+":"+(milis%60000)/10000+(milis%10000)/1000);
    }

    @Override
    public Filter getFilter() {
        if(songFilter == null)
            songFilter = new SongFilter();
        return songFilter;
    }

    private class SongFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null || constraint.length() == 0)
            {
                results.values = originalSongList;
                results.count = originalSongList.size();
            }
            else
            {
                ArrayList<song> filteredSong = new ArrayList<>();

                for(song s : originalSongList)
                {
                    if(s.getTitle().toUpperCase().startsWith(constraint.toString().toUpperCase()) ||
                            s.getAuthor().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        filteredSong.add(s);
                }
                results.values = filteredSong;
                results.count = filteredSong.size();
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.count == 0)
                notifyDataSetInvalidated();
            else{
                songList = (ArrayList<song>)results.values;
                notifyDataSetChanged();
            }

        }
    }

}
