package com.example.ben.movieapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.List;

public class ImageAdapter extends ArrayAdapter<MovieData> {
    private static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    public ImageAdapter(Activity context, List<MovieData> images) {
        super(context, 0, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieData movieData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.grid_item_poster, parent, false);
        }

        ImageView posterView =
                (ImageView) convertView.findViewById(R.id.grid_item_poster_imagebtn);

        //http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w342";
        final String POSTER_PATH = movieData.posterPath;

        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(SIZE)
                .appendEncodedPath(POSTER_PATH)
                .build();

        Log.v(LOG_TAG, builtUri.toString());

        Picasso.with(super.getContext()).load(builtUri).into(posterView);

        return convertView;
        //R.layout.grid_item_poster,
        //        R.id.grid_item_poster_imagebtn,
    }

}
