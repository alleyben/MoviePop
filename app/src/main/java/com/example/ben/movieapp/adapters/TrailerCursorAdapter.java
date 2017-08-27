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

public class TrailerCursorAdapter extends CursorRecyclerViewAdapter<TrailerCursorAdapter.ViewHolder> {

    private static final String LOG_TAG = TrailerCursorAdapter.class.getSimpleName();
    private Context mContext;

    public TrailerCursorAdapter(Context context, Cursor cursor){
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_trailer_and_recs, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        TrailerListItem trailerListItem = TrailerListItem.fromCursor(cursor);
        viewHolder.titleTextView.setText(trailerListItem.getTitle());

        ImageView posterView = viewHolder.posterImageView;

        final String YT_PREVIEW_BASE_URL = "https://i.ytimg.com/vi";
        final String PREVIEW_PATH = trailerListItem.getImageUrl();
        final String SIZE = "hqdefault.jpg";

        // example:
        // https://i.ytimg.com/vi/<preview path>/default.jpg
        Uri builtUri = Uri.parse(YT_PREVIEW_BASE_URL).buildUpon()
                .appendEncodedPath(PREVIEW_PATH)
                .appendPath(SIZE)//may need to make this encoded
                .build();

        Log.d(LOG_TAG, builtUri.toString());

        Picasso.with(mContext).load(builtUri).into(posterView);
    }
}