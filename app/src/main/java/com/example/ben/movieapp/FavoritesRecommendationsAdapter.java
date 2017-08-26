package com.example.ben.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class FavoritesRecommendationsAdapter extends CursorAdapter {

    private static final String LOG_TAG = FavoritesTrailerAdapter.class.getSimpleName();

    public static class ViewHolder {

        public final ImageView previewImageView;
        public final TextView titleTextView;

        public ViewHolder(View view) {
            previewImageView = (ImageView) view.findViewById(R.id.recycler_item_poster_imagebtn);
            titleTextView = (TextView) view.findViewById(R.id.recycler_item_poster_title);
        }
    }

    public FavoritesRecommendationsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.recycler_item_trailer_and_recs;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        TextView titleView = viewHolder.titleTextView;
        titleView.setText(cursor
                .getString(MovieInfoFavoritesFragment.COL_RECOMMENDATIONS_SIMILAR_MOVIE_TITLE)); // i.e. index is 2

        ImageView posterView = viewHolder.previewImageView;

        // sample url:
        // http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w342";
        final String POSTER_PATH = cursor
                .getString(MovieInfoFavoritesFragment.COL_RECOMMENDATIONS_SIMILAR_MOVIE_ID); // i.e. index is 3

        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(SIZE)
                .appendEncodedPath(POSTER_PATH)
                .build();

        Log.d(LOG_TAG, builtUri.toString());

        Picasso.with(context).load(builtUri).into(posterView);
    }
}
