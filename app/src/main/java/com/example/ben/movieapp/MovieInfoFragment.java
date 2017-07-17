package com.example.ben.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.example.ben.movieapp.database.DataContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieInfoFragment extends Fragment {

    private static final String LOG_TAG = MovieInfoFragment.class.getSimpleName();
    private MovieData mMovie;
    private boolean mIsFavorite;
    private RecommendationsAdapter mRecAdapter;
    private TrailerAdapter mTrailerAdapter;

    // TODO fetch details task: get reviews, youtube trailers, mpaa rating, similar movies
    // TODO google link, rotten tomatoes, meta critic
    // TODO backdrop_path with title over it, on upward move, backdrop fades or flows up, title flows up, info comes up into foreground


    public MovieInfoFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);

        ImageView posterView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_poster);
        TextView titleView = (TextView) rootView.findViewById(R.id.fragment_movie_info_title);
        TextView overviewView = (TextView) rootView.findViewById(R.id.fragment_movie_info_overview);
        TextView scoreView = (TextView) rootView.findViewById(R.id.fragment_movie_info_avg_score);
        TextView dateView = (TextView) rootView.findViewById(R.id.fragment_movie_info_date);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("movieInfoTag")) {
            mMovie = intent.getParcelableExtra("movieInfoTag");

            // get imdb, runtime, genres, tagline
            // decided not to move fetching of all details here in order to space out fetching
            // MovieData structure stays intact for now
            FetchDetailsTask detailsTask = new FetchDetailsTask();
            detailsTask.setView(rootView);
            detailsTask.execute(mMovie.movieId);

            // get mpaa rating
            FetchRatingTask ratingTask = new FetchRatingTask();
            ratingTask.setView(rootView);
            ratingTask.execute(mMovie.movieId);

            // TODO: make following changes
            //
            // Get movie trailer urls
            // also more review scores, google link, share link etc.

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

        // set imdb and google links
        ImageView imdbImageView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_imdb_button);
        imdbImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String IMDB_BASE_URL = "http://www.imdb.com/title";
                TextView tv = (TextView) rootView.findViewById(R.id.fragment_movie_info_imdb_id);
                final String IMDB_ID = (String) tv.getText();
                // example:
                // "http://www.imdb.com/title/<imdb_id>"
                Uri imdbUri = Uri.parse(IMDB_BASE_URL).buildUpon().appendPath(IMDB_ID).build();
                Log.d(LOG_TAG, imdbUri.toString());
                startActivity(new Intent(Intent.ACTION_VIEW, imdbUri));
            }
        });

        ImageView googleImageView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_google_button);
        googleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String GOOGLE_BASE_URL = "http://www.google.com/search";
                final String QUERY = "q";
                final String SEARCH_TERM = mMovie.title.trim();
                // example:
                // "http://www.google.com/#q=<movie_title>"
                Uri googleUri = Uri.parse(GOOGLE_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY, SEARCH_TERM)
                        .build();
                Log.d(LOG_TAG, googleUri.toString());
                startActivity(new Intent(Intent.ACTION_VIEW, googleUri));
            }
        });

        ImageView roTomatoesImageView =
                (ImageView) rootView.findViewById(R.id.fragment_movie_info_rotten_tomatoes_button);
        roTomatoesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String RT_BASE_URL = "http://www.rottentomatoes.com/search/";
                final String SEARCH = "search";
                final String SEARCH_TERM = mMovie.title.trim();
                // example:
                // "http://www.rottentomatoes.com/search?search=<movie_title>"
                Uri rtUri = Uri.parse(RT_BASE_URL).buildUpon()
                        .appendQueryParameter(SEARCH, SEARCH_TERM)
                        .build();
                Log.d(LOG_TAG, rtUri.toString());
                startActivity(new Intent(Intent.ACTION_VIEW, rtUri));
            }
        });

        // get trailers
        final RecyclerView trailersView =
                (RecyclerView) rootView.findViewById(R.id.fragment_movie_info_yt_videos);
        LinearLayoutManager trailersLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTrailerAdapter = new TrailerAdapter(getContext(), new ArrayList<TrailerData>());
        trailersView.setLayoutManager(trailersLayoutManager);
        FetchVideosTask fetchTrailers = new FetchVideosTask();
        fetchTrailers.setAdapter(mTrailerAdapter);
        fetchTrailers.execute(mMovie.movieId);
        trailersView.setAdapter(mTrailerAdapter);

        mTrailerAdapter.setOnItemClickListener(new TrailerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                TrailerData trailerData = mTrailerAdapter.getItem(position);
                Log.d(LOG_TAG, "Youtube video has been clicked: " + trailerData.toString() +
                        "\nat position: " + position + "\n");

                final String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";
                final String TRAILER_PATH = trailerData.trailerPath;

                // example:
                // http://www.youtube.com/watch?v=<trailer path>
                Uri trailerUri = Uri.parse(TRAILER_BASE_URL).buildUpon()
                        .appendEncodedPath(TRAILER_PATH)
                        .build();

                startActivity(
                        new Intent(Intent.ACTION_VIEW, trailerUri));
            }
        });


        // get similar movie recommendations
        final RecyclerView recsView =
                (RecyclerView) rootView.findViewById(R.id.fragment_movie_info_movie_recs);
        LinearLayoutManager recsLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecAdapter = new RecommendationsAdapter(getContext(), new ArrayList<MovieData>());
        recsView.setLayoutManager(recsLayoutManager);
        FetchMoviesTask fetchMovies = new FetchMoviesTask();
        fetchMovies.setRecsAdapter(mRecAdapter);
        fetchMovies.execute(mMovie.movieId);
        recsView.setAdapter(mRecAdapter);

        mRecAdapter.setOnItemClickListener(new RecommendationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                MovieData movieData = mRecAdapter.getItem(position);
                Log.d(LOG_TAG, "Rec movie has been clicked!!!! ::: " + movieData.toString() +
                        "\nat position: " + position + "\n");
                startActivity(
                        new Intent(getActivity(), MovieInfoActivity.class)
                                .putExtra("movieInfoTag", movieData));
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

