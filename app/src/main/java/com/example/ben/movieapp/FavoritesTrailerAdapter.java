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

import com.example.ben.movieapp.database.DataContract;
import com.squareup.picasso.Picasso;


public class FavoritesTrailerAdapter extends CursorAdapter {

    private static final String LOG_TAG = FavoritesTrailerAdapter.class.getSimpleName();

    public static class ViewHolder {

        public final ImageView posterImageView;
        public final TextView titleTextView;

        public ViewHolder(View view) {
            posterImageView = (ImageView) view.findViewById(R.id.recycler_item_poster_imagebtn);
            titleTextView = (TextView) view.findViewById(R.id.recycler_item_poster_title);
        }
    }

    public FavoritesTrailerAdapter(Context context, Cursor c, int flags) {
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
        titleView.setText(cursor.getString(MovieInfoFavoritesFragment.COL_TRAILER_TITLE));

        ImageView posterView = viewHolder.posterImageView;

        final String YT_PREVIEW_BASE_URL = "https://i.ytimg.com/vi";
        final String PREVIEW_PATH = cursor.getString(MovieInfoFavoritesFragment.COL_TRAILER_URL); // i.e. index is 1
        final String SIZE = "hqdefault.jpg";

        // example:
        // https://i.ytimg.com/vi/<preview path>/default.jpg
        Uri builtUri = Uri.parse(YT_PREVIEW_BASE_URL).buildUpon()
                .appendEncodedPath(PREVIEW_PATH)
                .appendPath(SIZE)//may need to make this encoded
                .build();

        Log.d(LOG_TAG, builtUri.toString());

        Picasso.with(context).load(builtUri).into(posterView);
    }
}
