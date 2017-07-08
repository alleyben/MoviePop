package com.example.ben.movieapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends ArrayAdapter<MovieData> {
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Activity context, List<MovieData> images) {
        super(context, 0, images);
//        context, layoutRes Int resource, List<MovieData>
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieData movieData = getItem(position);

        Context context = getContext();

        if (context.equals(MainActivity.class)) {

        }

        if (convertView == null) {
            if (context.getClass().equals(MainActivity.class)) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.grid_item_poster, parent, false);
            } else if (context.getClass().equals(MovieInfoActivity.class)) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.recycler_item_trailer_and_recs, parent, false);
            } else {
                Log.e(LOG_TAG, "Context is " + context.getClass().getSimpleName() +
                        "\nDoes not match required context\n");
            }
        }

        ImageView posterView = null;

        if (context.getClass().equals(MainActivity.class)) {
            posterView = (ImageView) convertView.findViewById(R.id.grid_item_poster_imagebtn);
        } else if (context.getClass().equals(MovieInfoActivity.class)) {
            posterView = (ImageView) convertView.findViewById(R.id.recycler_item_poster_imagebtn);

            TextView titleView = (TextView) convertView.findViewById(R.id.recycler_item_poster_title);
            titleView.setText(movieData.title);

        } else {
            Log.e(LOG_TAG, "Context is " + context.getClass().getSimpleName() +
                    "\nDoes not match required context\n");
        }

        // sample url:
        // http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w342";
        final String POSTER_PATH = movieData.posterPath;

        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(SIZE)
                .appendEncodedPath(POSTER_PATH)
                .build();

        Log.v(LOG_TAG, builtUri.toString());

        if (posterView != null) {
            Picasso.with(super.getContext()).load(builtUri).into(posterView);
        }

        return convertView;
    }

}
