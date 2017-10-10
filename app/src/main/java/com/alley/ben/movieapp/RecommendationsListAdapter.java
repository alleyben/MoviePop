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

public class RecommendationsListAdapter extends RecyclerView.Adapter<RecommendationsListAdapter.ViewHolder> {

    private final String LOG_TAG = RecommendationsListAdapter.class.getSimpleName();

    private Context mContext;
    private OnItemClickListener mListener;
    private List<MovieData> mMovieList;

    public RecommendationsListAdapter(Context context, List<MovieData> movies) {
        mContext = context;
        mMovieList = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext(); // i.e. get movieinfoactivity from parent which is... movieinfofragment?
        LayoutInflater inflater = LayoutInflater.from(context);

        View recView = inflater.inflate(R.layout.recycler_item_trailer_and_recs, parent, false);

        return new ViewHolder(recView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieData movieData = mMovieList.get(position);
        TextView textView = holder.titleTextView;
        textView.setText(movieData.title);
        ImageView posterView = holder.posterImageView;

        // TODO: change resolution of poster to w185
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

        Picasso.with(mContext).load(builtUri).into(posterView);


    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void addArrayItems(List<MovieData> list) {
        mMovieList.addAll(list);
        this.notifyDataSetChanged();
    }

    public MovieData getItem(int position) {
        //switch case for
        return mMovieList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView posterImageView;

        public ViewHolder(final View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.recycler_item_poster_title);
            posterImageView = (ImageView) itemView.findViewById(R.id.recycler_item_poster_imagebtn);

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
