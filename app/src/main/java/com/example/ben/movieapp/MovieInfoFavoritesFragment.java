package com.example.ben.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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


public class MovieInfoFavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MovieInfoFavoritesFragment.class.getSimpleName();
    private boolean isFavorite;
    private Uri mUri;
    private MovieData mMovie;
    static final String DETAIL_URI = "URI";

    private static final int DETAILS_LOADER = 0;

    // TODO fetch details task: get reviews, youtube trailers, mpaa rating, similar movies
    // TODO google link, rotten tomatoes, meta critic


    private static final String[] DETAILS_COLUMNS = {
            DataContract.FavoritesEntry._ID,
            DataContract.FavoritesEntry.COLUMN_MOVIE_ID,
            DataContract.FavoritesEntry.COLUMN_TITLE,
            DataContract.FavoritesEntry.COLUMN_OVERVIEW,
            DataContract.FavoritesEntry.COLUMN_POSTER_URL,
            DataContract.FavoritesEntry.COLUMN_SCORE,
            DataContract.FavoritesEntry.COLUMN_DATE//,
//            DataContract.FavoritesEntry.COLUMN_TRAILER_URL,
//            DataContract.FavoritesEntry.COLUMN_RATING
    };

    static final int COL_ROW_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_POSTER_URL = 4;
    static final int COL_SCORE = 5;
    static final int COL_DATE = 6;
//    static final int COL_TRAILER_URL = 7;
//    static final int COL_RATING = 8;

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mScoreView;
    private TextView mDateView;
    private CheckBox mStarFavorite;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            mUri = arguments.getParcelable(MovieInfoFavoritesFragment.DETAIL_URI);
            Log.d(LOG_TAG, "mUri created: " + mUri);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);

        mPosterView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_poster);
        mTitleView = (TextView) rootView.findViewById(R.id.fragment_movie_info_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.fragment_movie_info_overview);
        mScoreView = (TextView) rootView.findViewById(R.id.fragment_movie_info_avg_score);
        mDateView = (TextView) rootView.findViewById(R.id.fragment_movie_info_date);
        mStarFavorite = (CheckBox) rootView.findViewById(R.id.star_button);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
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
        Log.d(LOG_TAG, "createShareMovieIntent initiated");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.title);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAILS_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            // Get movie id and run task to get mpaa rating and trailer urls
            // also more review scores, google link, share link etc.
            // FetchMovieDetailsTask fetchMovies = new FetchMovieDetailsTask();
            // fetchMovies.execute(mMovie.movieId);


            final String title = data.getString(COL_TITLE);
            mTitleView.setText(title);

            String overview = data.getString(COL_OVERVIEW);
            mOverviewView.setText(overview);

            String score = data.getString(COL_SCORE);
            String scoreStr = new StringBuilder("User Score:\n")
                    .append(data.getString(COL_SCORE))
                    .toString();
            mScoreView.setText(scoreStr);

            String date = data.getString(COL_DATE);
            String dateStr = new StringBuilder("Release Date:\n")
                    .append(data.getString(COL_DATE))
                    .toString();
            mDateView.setText(dateStr);

            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
            final String SIZE = "w500";
            final String POSTER_PATH = data.getString(COL_POSTER_URL);

            Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                    .appendPath(SIZE)
                    .appendEncodedPath(POSTER_PATH)
                    .build();

            Log.v(LOG_TAG, builtUri.toString());

            Picasso.with(super.getContext()).load(builtUri).into(mPosterView);

            //CHECK FROM FAVORITES
            String[] projection = {DataContract.FavoritesEntry.COLUMN_MOVIE_ID};
            final String selection = DataContract.FavoritesEntry.TABLE_NAME +
                    "." + DataContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";
            final String[] selectionArgs = {data.getString(COL_MOVIE_ID)};

            // Get star favorite button
            isFavorite = true;
            mStarFavorite.setChecked(isFavorite);

            String[] movieArr =
                    {title, overview, POSTER_PATH, score, date, data.getString(COL_MOVIE_ID)};

            mMovie = new MovieData(movieArr);

            mStarFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ADD TO FAVORITES
                    if (isFavorite) {
                        // content uri references whole table, selection references row, selectionArgs provides specific row
                        getContext().getContentResolver().delete(
                                DataContract.FavoritesEntry.CONTENT_URI, selection, selectionArgs);
                        Log.d(LOG_TAG, title + " movie deleted from database");
                    } else {
                        ContentValues movieInfo = mMovie.getMovieDataCV();
                        getContext().getContentResolver().insert(
                                DataContract.FavoritesEntry.CONTENT_URI, movieInfo);
                    }
                    isFavorite = !isFavorite;
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
