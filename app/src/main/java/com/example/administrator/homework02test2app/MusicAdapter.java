package com.example.administrator.homework02test2app;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends ArrayAdapter<MusicMedia> {
    private List<MusicMedia> data;
    private int selectedPosition=-1;
    public MusicAdapter(@NonNull Context context, int resource, @NonNull List<MusicMedia> data) {
        super(context, resource, data);
        this.data = data;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition =selectedPosition;
    }

    @Override
    public int getCount() {
        return data.size();
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.songitem_layout,parent,false);
        MusicMedia m = data.get(position);
        Log.i("djp",m.getClass().getName());

        TextView title = view.findViewById(R.id.title);
        TextView artist = view.findViewById(R.id.artist);
        title.setText(m.getTitle());
        artist.setText(m.getArtist());
       if (position==selectedPosition){
            view.setBackgroundColor(Color.RED);
        }
        else {
          view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }
}
