package com.alley.ben.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alley.ben.movieapp.database.DataContract;
import com.alley.ben.movieapp.fetchers.FetchDetailsTask;
import com.alley.ben.movieapp.fetchers.FetchMoviesTask;
import com.alley.ben.movieapp.fetchers.FetchRatingTask;
import com.alley.ben.movieapp.fetchers.FetchVideosTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieInfoFragment extends Fragment {

    private static final String LOG_TAG = MovieInfoFragment.class.getSimpleName();
    private View mRootView;
    private MovieData mMovie;
    private boolean mIsFavorite;
    private boolean mIsInDatabase;
    private String mFavoritesSelection;
    private String[] mSelectionArgs = new String[1];
    private RecommendationsListAdapter mRecAdapter;
    private TrailerListAdapter mTrailerListAdapter;

    // TODO backdrop_path with title over it, on upward move, backdrop fades or flows up, title flows up to header and sticks, info comes up into foreground


    public MovieInfoFragment() {
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_movie_info, container, false);

        ImageView posterView = (ImageView) mRootView.findViewById(R.id.fragment_movie_info_poster);
        TextView titleView = (TextView) mRootView.findViewById(R.id.fragment_movie_info_title);
        ImageView imdbImageView = (ImageView) mRootView.findViewById(R.id.fragment_movie_info_imdb_button);
        ImageView googleImageView = (ImageView) mRootView.findViewById(R.id.fragment_movie_info_google_button);
        ImageView roTomatoesImageView =
                (ImageView) mRootView.findViewById(R.id.fragment_movie_info_rotten_tomatoes_button);
        final RecyclerView trailersView =
                (RecyclerView) mRootView.findViewById(R.id.fragment_movie_info_yt_videos);
        final RecyclerView recsView =
                (RecyclerView) mRootView.findViewById(R.id.fragment_movie_info_movie_recs);
        CheckBox starFavorite = (CheckBox) mRootView.findViewById(R.id.star_button);

        // close button to return straight to main menu instead of going back through every movie info frag
        ImageView closeBtnView = (ImageView) mRootView.findViewById(R.id.fragment_movie_info_close_button);
        closeBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });


        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("movieInfoTag")) {
            mMovie = intent.getParcelableExtra("movieInfoTag");

            // get overview, user score avg, release date, imdb id, runtime, genres, tagline
            FetchDetailsTask detailsTask = new FetchDetailsTask();
            detailsTask.setView(mRootView);
            detailsTask.setContext(getContext());
            detailsTask.execute(mMovie.movieId);

            // get mpaa rating
            FetchRatingTask ratingTask = new FetchRatingTask();
            ratingTask.setView(mRootView);
            ratingTask.execute(mMovie.movieId);

            // Set title
            titleView.setText(mMovie.title);

            // Set poster image
            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
            final String SIZE = "w780";
            final String POSTER_PATH = mMovie.posterPath;

            Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                    .appendPath(SIZE)
                    .appendEncodedPath(POSTER_PATH)
                    .build();

            Log.d(LOG_TAG, builtUri.toString());

            Picasso.with(super.getContext()).load(builtUri).into(posterView);

            // set imdb button listener
            imdbImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String IMDB_BASE_URL = "http://www.imdb.com/title";
                    TextView tv = (TextView) mRootView.findViewById(R.id.fragment_movie_info_imdb_id);
                    final String IMDB_ID = (String) tv.getText();
                    // example:
                    // "http://www.imdb.com/title/<imdb_id>"
                    Uri imdbUri = Uri.parse(IMDB_BASE_URL).buildUpon().appendPath(IMDB_ID).build();
                    Log.d(LOG_TAG, imdbUri.toString());
                    startActivity(new Intent(Intent.ACTION_VIEW, imdbUri));
                }
            });

            // set google button listener
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

            // set rotten tomatoes button listener
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
            LinearLayoutManager trailersLayoutManager =
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mTrailerListAdapter = new TrailerListAdapter(getContext(), new ArrayList<TrailerData>());
            trailersView.setLayoutManager(trailersLayoutManager);
            FetchVideosTask fetchTrailers = new FetchVideosTask();
            fetchTrailers.setAdapter(mTrailerListAdapter);
            fetchTrailers.execute(mMovie.movieId);
            trailersView.setAdapter(mTrailerListAdapter);

            mTrailerListAdapter.setOnItemClickListener(new TrailerListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    TrailerData trailerData = mTrailerListAdapter.getItem(position);

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
            LinearLayoutManager recsLayoutManager =
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecAdapter = new RecommendationsListAdapter(getContext(), new ArrayList<MovieData>());
            recsView.setLayoutManager(recsLayoutManager);
            FetchMoviesTask fetchMovies = new FetchMoviesTask();
            fetchMovies.setRecsAdapter(mRecAdapter);
            fetchMovies.execute(mMovie.movieId);
            recsView.setAdapter(mRecAdapter);

            mRecAdapter.setOnItemClickListener(new RecommendationsListAdapter.OnItemClickListener() {
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

            //CHECK FROM FAVORITES
            String[] projection = {DataContract.FavoritesContract.COLUMN_MOVIE_ID};
            mFavoritesSelection = DataContract.FavoritesContract.TABLE_NAME +
                    "." + DataContract.FavoritesContract.COLUMN_MOVIE_ID + " = ? ";
            mSelectionArgs[0] = mMovie.movieId;
            // selectionArgs should work for each table as the movie ID
            // will be the same because we're only dealing with one movie

            final Cursor cursor = getContext().getContentResolver().query(
                    DataContract.FavoritesContract.buildMovieIdUri(mMovie.movieId),
                    projection,
                    mFavoritesSelection,
                    mSelectionArgs,
                    null);

            mIsFavorite = (cursor.getCount() > 0);
            mIsInDatabase = mIsFavorite;

            // Get star favorite button
            starFavorite.setChecked(mIsFavorite);

            starFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { mIsFavorite = !mIsFavorite; }
            });
        }

        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsInDatabase && !mIsFavorite) {
            final String trailersSelection = DataContract.TrailersContract.TABLE_NAME + "." +
                    DataContract.TrailersContract.COLUMN_MOVIE_ID + " = ? ";

            final String recommendationsSelection = DataContract.RecommendationsContract.TABLE_NAME + "." +
                    DataContract.TrailersContract.COLUMN_MOVIE_ID + " = ? ";
            // content uri references whole table,
            // favoritesSelection references row depending on column content,
            // selectionArgs provides specific row
            getContext().getContentResolver().delete(
                    DataContract.FavoritesContract.CONTENT_URI,
                    mFavoritesSelection,
                    mSelectionArgs);
            getContext().getContentResolver().delete(
                    DataContract.TrailersContract.CONTENT_URI,
                    trailersSelection,
                    mSelectionArgs);
            getContext().getContentResolver().delete(
                    DataContract.RecommendationsContract.CONTENT_URI,
                    recommendationsSelection,
                    mSelectionArgs);

            Log.d(LOG_TAG, mMovie.title + " movie deleted from database");

        } else if (!mIsInDatabase && mIsFavorite) {

            ContentValues movieInfo = mMovie.getMovieDataCV();

            TextView overviewTextView =
                    (TextView) mRootView.findViewById(R.id.fragment_movie_info_overview);
            String overview = (String) "" + overviewTextView.getText();
//            Log.d(LOG_TAG, "Overview: " + overview);
            // "" is due to text of overview being "SpannableString"
            // not sure why overview is the only text that is that way
            // ad hoc solution implemented for now; come back later to fix it better
            movieInfo.put(DataContract.FavoritesContract.COLUMN_OVERVIEW, overview);

            TextView scoreTextView =
                    (TextView) mRootView.findViewById(R.id.fragment_movie_info_avg_score);
            String score = (String) scoreTextView.getText();
            movieInfo.put(DataContract.FavoritesContract.COLUMN_SCORE, score);

            TextView dateTextView =
                    (TextView) mRootView.findViewById(R.id.fragment_movie_info_date);
            String date = (String) dateTextView.getText();
            movieInfo.put(DataContract.FavoritesContract.COLUMN_DATE, date);

            TextView ratingTextView =
                    (TextView) mRootView.findViewById(R.id.fragment_movie_info_rating);
            String rating = (String) ratingTextView.getText();
            movieInfo.put(DataContract.FavoritesContract.COLUMN_RATING, rating);

            TextView runtimeTextView =
                    (TextView) mRootView.findViewById(R.id.fragment_movie_info_runtime);
            String runtime = (String) runtimeTextView.getText();
            movieInfo.put(DataContract.FavoritesContract.COLUMN_RUNTIME, runtime);

            TextView taglineTextView =
                    (TextView) mRootView.findViewById(R.id.fragment_movie_info_tagline);
            String tagline = (String) taglineTextView.getText();
            movieInfo.put(DataContract.FavoritesContract.COLUMN_TAGLINE, tagline);

            TextView genresTextView =
                    (TextView) mRootView.findViewById(R.id.fragment_movie_info_genres);
            String genres = (String) genresTextView.getText();
            movieInfo.put(DataContract.FavoritesContract.COLUMN_GENRES, genres);

            TextView imdbTextView =
                    (TextView) mRootView.findViewById(R.id.fragment_movie_info_imdb_id);
            String imdbID = (String) imdbTextView.getText();
            movieInfo.put(DataContract.FavoritesContract.COLUMN_IMDB_ID, imdbID);

            // insert movieData via ContentValues to database
            getContext().getContentResolver().insert(
                    DataContract.FavoritesContract.CONTENT_URI, movieInfo);


            // insert trailers to database
            int trailerCount = mTrailerListAdapter.getItemCount();
            ContentValues[] trailersArr = new ContentValues[trailerCount];
            // add getCV method to trailerData object
            for (int i = 0; i < trailerCount; i++) {
                ContentValues trailersCV = new ContentValues();
                TrailerData td = mTrailerListAdapter.getItem(i);
                trailersCV.put(
                        DataContract.TrailersContract.COLUMN_MOVIE_ID,
                        mMovie.movieId
                );
                trailersCV.put(
                        DataContract.TrailersContract.COLUMN_TRAILER_URL,
                        td.trailerPath
                );
                trailersCV.put(
                        DataContract.TrailersContract.COLUMN_TRAILER_TITLE,
                        td.title
                );
                trailersArr[i] = trailersCV;
            }

            getContext().getContentResolver().bulkInsert(
                    DataContract.TrailersContract.CONTENT_URI, trailersArr);


            // insert recommendations to database
            int recCount = mRecAdapter.getItemCount();
            ContentValues[] recsArr = new ContentValues[recCount];
            for (int i = 0; i < recCount; i++) {
                ContentValues recommendationsCV = new ContentValues();
                MovieData md = mRecAdapter.getItem(i);
                Log.d(LOG_TAG, "\nrec added to database: " + md.title);

                recommendationsCV.put(
                        DataContract.RecommendationsContract.COLUMN_MOVIE_ID,
                        mMovie.movieId
                );
                recommendationsCV.put(
                        DataContract.RecommendationsContract.COLUMN_SIMILAR_MOVIE_ID,
                        md.movieId
                );
                recommendationsCV.put(
                        DataContract.RecommendationsContract.COLUMN_SIMILAR_MOVIE_TITLE,
                        md.title
                );
                recommendationsCV.put(
                        DataContract.RecommendationsContract.COLUMN_SIMILAR_MOVIE_POSTER_URL,
                        md.posterPath
                );
                recsArr[i] = recommendationsCV;
            }

            getContext().getContentResolver().bulkInsert(
                    DataContract.RecommendationsContract.CONTENT_URI, recsArr);
        }
    }
}

