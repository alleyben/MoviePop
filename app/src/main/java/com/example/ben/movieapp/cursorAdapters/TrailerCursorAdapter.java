package com.example.ben.movieapp.cursorAdapters;


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
    private OnItemClickListener mListener;


    public TrailerCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View trailerItemView = inflater
                .inflate(R.layout.recycler_item_trailer_and_recs, parent, false);
        return new ViewHolder(trailerItemView);
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView posterImageView;
        public final TextView titleTextView;

        public ViewHolder(final View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.recycler_item_poster_imagebtn);
            titleTextView = (TextView) itemView.findViewById(R.id.recycler_item_poster_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }
}