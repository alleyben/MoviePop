package com.example.ben.movieapp;

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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<MovieData> mMovies;
    private Context mContext;
    private final String LOG_TAG = RecyclerAdapter.class.getSimpleName();

    public RecyclerAdapter(Context context, List<MovieData> movies) {
        mContext = context;
        mMovies = movies;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext(); // i.e. get movieinfoactivity from parent which is... movieinfofragment?
        LayoutInflater inflater = LayoutInflater.from(context);

        View recView = inflater.inflate(R.layout.recycler_item_trailer_and_recs, parent, false);

        return new ViewHolder(recView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        MovieData movieData = mMovies.get(position);
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
        return mMovies.size();
    }

    public void addArrayItems(List<MovieData> list) {
        mMovies.addAll(list);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView posterImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.recycler_item_poster_title);
            posterImageView = (ImageView) itemView.findViewById(R.id.recycler_item_poster_imagebtn);
        }
    }
}
