package com.example.ben.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ben.movieapp.data.DataContract;
import com.squareup.picasso.Picasso;

public class MovieInfoFragment extends Fragment {

    private static final String LOG_TAG = MovieInfoFragment.class.getSimpleName();
    private MovieData mMovie;
    private boolean mIsFavorite;

    // TODO fetch details task: get reviews, youtube trailers, mpaa rating, similar movies
    // TODO google link, rotten tomatoes, meta critic


    public MovieInfoFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);

        ImageView posterView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_poster);
        TextView titleView = (TextView) rootView.findViewById(R.id.fragment_movie_info_title);
        TextView overviewView = (TextView) rootView.findViewById(R.id.fragment_movie_info_overview);
        TextView scoreView = (TextView) rootView.findViewById(R.id.fragment_movie_info_avg_score);
        TextView dateView = (TextView) rootView.findViewById(R.id.fragment_movie_info_date);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("movieInfoTag")) {
            mMovie = intent.getParcelableExtra("movieInfoTag");

            // TODO: make following changes
            //
            // Get movie id and run task to get mpaa rating and trailer urls
            // also more review scores, google link, share link etc.
            // FetchMovieDetailsTask fetchMovies = new FetchMovieDetailsTask();
            // fetchMovies.execute(mMovie.movieId);

            // Set title
            titleView.setText(mMovie.title);

            // Set poster image
            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
            final String SIZE = "w500";
            final String POSTER_PATH = mMovie.posterPath;

            Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                    .appendPath(SIZE)
                    .appendEncodedPath(POSTER_PATH)
                    .build();

            Log.d(LOG_TAG, builtUri.toString());

            Picasso.with(super.getContext()).load(builtUri).into(posterView);

            // Set details (date, avgScore)
            String movieDate = new StringBuilder("Release Date:\n").append(mMovie.date).toString();
            dateView.setText(movieDate);
            scoreView.setText("User Score:\n" + mMovie.avgScore);

            // Set overview
            overviewView.setText(mMovie.overview);
        }

        //CHECK FROM FAVORITES
        String[] projection = {DataContract.FavoritesEntry.COLUMN_MOVIE_ID};
        final String selection = DataContract.FavoritesEntry.TABLE_NAME +
                "." + DataContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";
        final String[] selectionArgs = {mMovie.movieId};

        final Cursor cursor = getContext().getContentResolver().query(
                DataContract.FavoritesEntry.buildMovieIdUri(mMovie.movieId),
                projection,
                selection,
                selectionArgs,
                null);

        mIsFavorite = (cursor.getCount() > 0);

        // Get star favorite button
        CheckBox starFavorite = (CheckBox) rootView.findViewById(R.id.star_button);
        starFavorite.setChecked(mIsFavorite);

        starFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ADD TO FAVORITES
                if (mIsFavorite) {
                    // content uri references whole table, selection references row, selectionArgs provides specific row
                    getContext().getContentResolver().delete(
                            DataContract.FavoritesEntry.CONTENT_URI, selection, selectionArgs);
                    Log.d(LOG_TAG, mMovie.title + " movie deleted from database");
                } else {
                    ContentValues movieInfo = mMovie.getMovieDataCV();
                    getContext().getContentResolver().insert(
                            DataContract.FavoritesEntry.CONTENT_URI, movieInfo);
                }
                mIsFavorite = !mIsFavorite;
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_info, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d(LOG_TAG, "Share action provider is null");
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.title);
        return shareIntent;
    }
}

