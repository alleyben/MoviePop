package com.example.ben.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FavoritesAdapter extends CursorAdapter {

    public static final String LOG_TAG = FavoritesAdapter.class.getSimpleName();

    public static class ViewHolder {

        public final ImageView posterView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.grid_item_poster_imagebtn);
        }
    }

    public FavoritesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.d(LOG_TAG, "newView initiated");

        int layoutId = R.layout.grid_item_poster;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.d(LOG_TAG, "bindView initiated");

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ImageView posterView = viewHolder.posterView;

        // sample url:
        // http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w342";
        final String POSTER_PATH = cursor.getString(FrontFragment.COL_POSTER_URL);

        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(SIZE)
                .appendEncodedPath(POSTER_PATH)
                .build();

        Log.d(LOG_TAG, builtUri.toString());

        Picasso.with(context).load(builtUri).into(posterView);
    }
}
