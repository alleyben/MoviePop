package com.alley.ben.movieapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ViewHolder> {

    private final String LOG_TAG = TrailerListAdapter.class.getSimpleName();

    private Context mContext;
    private List<TrailerData> mTrailerList;
    private OnItemClickListener mListener;

    public TrailerListAdapter(Context context, List<TrailerData> trailers) {
        mContext = context;
        mTrailerList = trailers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View trailerItemView = inflater
                .inflate(R.layout.recycler_item_trailer_and_recs, parent, false);
        return new ViewHolder(trailerItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TrailerData trailerData = mTrailerList.get(position);
        TextView textView = holder.titleTextView;
        textView.setText(trailerData.title);
        ImageView previewView = holder.previewImageView;

        final String YT_PREVIEW_BASE_URL = "https://i.ytimg.com/vi";
        final String PREVIEW_PATH = trailerData.trailerPath;
        final String SIZE = "hqdefault.jpg";

        // example:
        // https://i.ytimg.com/vi/<preview path>/default.jpg
        Uri builtUri = Uri.parse(YT_PREVIEW_BASE_URL).buildUpon()
                .appendEncodedPath(PREVIEW_PATH)
                .appendPath(SIZE)//may need to make this encoded
                .build();

        Log.v(LOG_TAG, builtUri.toString());

        Picasso.with(mContext).load(builtUri).into(previewView);
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public void addArrayItems(List<TrailerData> list) {
        mTrailerList.addAll(list);
        this.notifyDataSetChanged();
    }

    public TrailerData getItem(int position) {
        return mTrailerList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView previewImageView;

        public ViewHolder(final View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.recycler_item_poster_title);
            previewImageView = (ImageView) itemView.findViewById(R.id.recycler_item_poster_imagebtn);

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
