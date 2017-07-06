package com.example.ben.movieapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsAdapter extends ArrayAdapter<Map<String, String>> {

    public DetailsAdapter(Activity context, List<Map<String, String>> details) {super(context, 0, details);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        Map<String, String> detailsMap = getItem(position);
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext())
//                    .inflate(R.layout.fragment_movie_info, parent, false);
//        }
//
//        final String HOMEPAGE = "homepage";
//        final String IMDB_ID = "imdb_id";
//        final String RUNTIME = "runtime";
//        final String RATING = "rating";
//        final String TAGLINE = "tagline";
//
////        TextView homepage = (TextView) convertView.findViewById(R.id.fragment_movie_info_homepage);
////        homepage.setText(detailsMap.get(HOMEPAGE));
//
//        TextView imdbId = (TextView) convertView.findViewById(R.id.fragment_movie_info_imdb_id);
//        imdbId.setText(detailsMap.get(IMDB_ID));
//
//        TextView runtime = (TextView) convertView.findViewById(R.id.fragment_movie_info_runtime);
//        runtime.setText(detailsMap.get(RUNTIME));
//
//        TextView rating = (TextView) convertView.findViewById(R.id.fragment_movie_info_rating);
//        rating.setText(detailsMap.get(RATING));
//
//        TextView tagline = (TextView) convertView.findViewById(R.id.fragment_movie_info_tagline);
//        tagline.setText(detailsMap.get(TAGLINE));

        return convertView;
    }
}
