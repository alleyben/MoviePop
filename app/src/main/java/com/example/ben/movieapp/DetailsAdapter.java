package com.example.ben.movieapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class DetailsAdapter extends ArrayAdapter<String> {

    public DetailsAdapter(Activity context, List<String> details) {super(context, 0, details);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_movie_info, parent, false);
        }

        //youtube links buttons, img-btn or ImageView

        //reviews, TextView

        //set views

        return convertView;
    }
}
