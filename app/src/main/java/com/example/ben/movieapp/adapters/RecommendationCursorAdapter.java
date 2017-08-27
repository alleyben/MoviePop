package com.example.ben.movieapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ben.movieapp.R;
import com.squareup.picasso.Picasso;

public class RecommendationCursorAdapter extends CursorRecyclerViewAdapter<RecommendationCursorAdapter.ViewHolder> {


    private static final String LOG_TAG = TrailerCursorAdapter.class.getSimpleName();
    private Context mContext;

    public RecommendationCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView posterImageView;
        public final TextView titleTextView;

        public ViewHolder(View view) {
            super(view);
            posterImageView = (ImageView) view.findViewById(R.id.recycler_item_poster_imagebtn);
            titleTextView = (TextView) view.findViewById(R.id.recycler_item_poster_title);
        }
    }

    @Override
    public RecommendationCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_trailer_and_recs, parent, false);
        RecommendationCursorAdapter.ViewHolder vh = new RecommendationCursorAdapter.ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecommendationCursorAdapter.ViewHolder viewHolder, Cursor cursor) {
        RecommendationListItem recListItem = RecommendationListItem.fromCursor(cursor);
        viewHolder.titleTextView.setText(recListItem.getTitle());

        ImageView posterView = viewHolder.posterImageView;

        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE = "w342";
        final String POSTER_PATH = recListItem.getImageUrl();

        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(SIZE)
                .appendEncodedPath(POSTER_PATH)
                .build();

        Log.v(LOG_TAG, builtUri.toString());

        Picasso.with(mContext).load(builtUri).into(posterView);
    }
}
