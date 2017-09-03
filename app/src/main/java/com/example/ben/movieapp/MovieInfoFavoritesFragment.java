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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ben.movieapp.adapters.RecommendationCursorAdapter;
import com.example.ben.movieapp.adapters.TrailerCursorAdapter;
import com.example.ben.movieapp.adapters.TrailerListItem;
import com.example.ben.movieapp.database.DataContract;
import com.squareup.picasso.Picasso;


public class MovieInfoFavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MovieInfoFavoritesFragment.class.getSimpleName();
    private Uri mMovieUri;
    private Uri mTrailersUri;
    private Uri mRecommendationsUri;
    private MovieData mMovie;
    private boolean mIsFavorite = true;
    static final String DETAIL_URI = "URI";
    private TrailerCursorAdapter mTrailerCursorAdapter;
    private RecommendationCursorAdapter mRecCursorAdapter;

    private static final int DETAILS_LOADER = 0;
    private static final int TRAILERS_LOADER = 1;
    private static final int RECOMMENDATIONS_LOADER = 2;


    private static final String[] FAVORITES_COLUMNS = {
            DataContract.FavoritesContract._ID,
            DataContract.FavoritesContract.COLUMN_MOVIE_ID,
            DataContract.FavoritesContract.COLUMN_TITLE,
            DataContract.FavoritesContract.COLUMN_OVERVIEW,
            DataContract.FavoritesContract.COLUMN_POSTER_URL,
            DataContract.FavoritesContract.COLUMN_SCORE,
            DataContract.FavoritesContract.COLUMN_DATE,
            DataContract.FavoritesContract.COLUMN_RATING,
            DataContract.FavoritesContract.COLUMN_RUNTIME,
            DataContract.FavoritesContract.COLUMN_TAGLINE,
            DataContract.FavoritesContract.COLUMN_GENRES,
            DataContract.FavoritesContract.COLUMN_IMDB_ID
    };

    private static final String[] TRAILERS_COLUMNS = {
            DataContract.TrailersContract._ID,
            DataContract.TrailersContract.COLUMN_MOVIE_ID,
            DataContract.TrailersContract.COLUMN_TRAILER_URL,
            DataContract.TrailersContract.COLUMN_TRAILER_TITLE
    };

    private static final String[] RECOMMENDATIONS_COLUMNS = {
            DataContract.RecommendationsContract._ID,
            DataContract.RecommendationsContract.COLUMN_MOVIE_ID,
            DataContract.RecommendationsContract.COLUMN_SIMILAR_MOVIE_ID,
            DataContract.RecommendationsContract.COLUMN_SIMILAR_MOVIE_TITLE,
            DataContract.RecommendationsContract.COLUMN_SIMILAR_MOVIE_POSTER_URL
    };

    static final int COL_DETAILS_ROW_ID = 0;
    static final int COL_DETAILS_MOVIE_ID = 1;
    static final int COL_DETAILS_TITLE = 2;
    static final int COL_DETAILS_OVERVIEW = 3;
    static final int COL_DETAILS_POSTER_URL = 4;
    static final int COL_DETAILS_SCORE = 5;
    static final int COL_DETAILS_DATE = 6;
    static final int COL_DETAILS_RATING = 7;
    static final int COL_DETAILS_RUNTIME = 8;
    static final int COL_DETAILS_TAGLINE = 9;
    static final int COL_DETAILS_GENRES = 10;
    static final int COL_DETAILS_IMDB_ID = 11;

    public static final int COL_TRAILER_ROW_ID = 0;
    public static final int COL_TRAILER_MOVIE_ID = 1;
    public static final int COL_TRAILER_URL = 2;
    public static final int COL_TRAILER_TITLE = 3;

    public static final int COL_RECOMMENDATIONS_ROW_ID = 0;
    public static final int COL_RECOMMENDATIONS_MOVIE_ID = 1;
    public static final int COL_RECOMMENDATIONS_SIMILAR_MOVIE_ID = 2;
    public static final int COL_RECOMMENDATIONS_SIMILAR_MOVIE_TITLE = 3;
    public static final int COL_RECOMMENDATIONS_SIMILAR_MOVIE_POSTER_URL = 4;

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mScoreView;
    private TextView mDateView;
    private CheckBox mStarFavorite;
    private TextView mRatingView;
    private TextView mRuntimeView;
    private TextView mTaglineView;
    private TextView mGenresView;
    private ImageView mImdbBtnView;
    private ImageView mGoogleBtnView;
    private ImageView mRoTomatBtnView;
    private RecyclerView mTrailersView;
    private RecyclerView mRecsView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            mMovieUri = arguments.getParcelable(DETAIL_URI);
            // "content://com.example.ben.moviepop.app.favorites.<movie_id>"
            Log.d(LOG_TAG, "mMovieUri created: " + mMovieUri);
            String movieId = DataContract.FavoritesContract.getMovieId(mMovieUri);
            mTrailersUri = DataContract.TrailersContract.buildMovieIdUri(movieId);
            mRecommendationsUri = DataContract.RecommendationsContract.buildMovieIdUri(movieId);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);

        mPosterView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_poster);
        mTitleView = (TextView) rootView.findViewById(R.id.fragment_movie_info_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.fragment_movie_info_overview);
        mScoreView = (TextView) rootView.findViewById(R.id.fragment_movie_info_avg_score);
        mDateView = (TextView) rootView.findViewById(R.id.fragment_movie_info_date);
        mStarFavorite = (CheckBox) rootView.findViewById(R.id.star_button);
        mRatingView = (TextView) rootView.findViewById(R.id.fragment_movie_info_rating);
        mRuntimeView = (TextView) rootView.findViewById(R.id.fragment_movie_info_runtime);
        mTaglineView = (TextView) rootView.findViewById(R.id.fragment_movie_info_tagline);
        mGenresView = (TextView) rootView.findViewById(R.id.fragment_movie_info_genres);
        mImdbBtnView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_imdb_button);
        mGoogleBtnView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_google_button);
        mRoTomatBtnView = (ImageView) rootView.findViewById(R.id.fragment_movie_info_rotten_tomatoes_button);
        mTrailersView = (RecyclerView) rootView.findViewById(R.id.fragment_movie_info_yt_videos);
        mRecsView = (RecyclerView) rootView.findViewById(R.id.fragment_movie_info_movie_recs);


        LinearLayoutManager trailersLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTrailerCursorAdapter = new TrailerCursorAdapter(getActivity(), null);
        mTrailersView.setLayoutManager(trailersLayoutManager);
        mTrailersView.setAdapter(mTrailerCursorAdapter);
        LinearLayoutManager recsLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecCursorAdapter = new RecommendationCursorAdapter(getActivity(), null);
        mRecsView.setLayoutManager(recsLayoutManager);
        mRecsView.setAdapter(mRecCursorAdapter);

//        mTrailersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                    Uri contentUri = DataContract.FavoritesContract.buildMovieIdUri(
//                            cursor.getString(COL_DETAILS_MOVIE_ID)//instead (of) get position
//                            // "content://com.example.ben.moviepop.app.favorites.<movie_id>"
//                    );
//                    Log.d(LOG_TAG, "ITEM CLICKED at position: " + position + "\nuri created: " + contentUri.toString());
//                    Intent intent = new Intent(getActivity(), MovieInfoActivity.class)
//                            .setData(contentUri);
//                    startActivity(intent);
//                }
//            }
//        });

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
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        getLoaderManager().initLoader(RECOMMENDATIONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DETAILS_LOADER:
                if (mMovieUri != null) {
                    return new CursorLoader(
                            getActivity(),
                            mMovieUri,
                            FAVORITES_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                return null;
            case TRAILERS_LOADER:
                if (mTrailersUri != null) {
                    return new CursorLoader(
                            getActivity(),
                            mTrailersUri,
                            TRAILERS_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
            case RECOMMENDATIONS_LOADER:
                if (mRecommendationsUri != null) {
                    return new CursorLoader(
                            getActivity(),
                            mRecommendationsUri,
                            RECOMMENDATIONS_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
            default:
                Log.e(LOG_TAG, "Loader not found");
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (data != null && data.moveToFirst()) {

            // Get movie id and run task to get mpaa rating and trailer urls
            // also more review scores, google link, share link etc.
            // FetchMovieDetailsTask fetchMovies = new FetchMovieDetailsTask();
            // fetchMovies.execute(mMovie.movieId);

            switch (loader.getId()) {
                case DETAILS_LOADER:
                    final String title = data.getString(COL_DETAILS_TITLE);
                    mTitleView.setText(title);

                    String overview = data.getString(COL_DETAILS_OVERVIEW);
                    mOverviewView.setText(overview);

                    String score = data.getString(COL_DETAILS_SCORE);
                    String scoreStr = new StringBuilder("User Score:\n")
                            .append(data.getString(COL_DETAILS_SCORE))
                            .toString();
                    mScoreView.setText(scoreStr);

                    String date = data.getString(COL_DETAILS_DATE);
                    String dateStr = new StringBuilder("Release Date:\n")
                            .append(data.getString(COL_DETAILS_DATE))
                            .toString();
                    mDateView.setText(dateStr);

                    final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                    final String SIZE = "w500";
                    final String POSTER_PATH = data.getString(COL_DETAILS_POSTER_URL);

                    Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                            .appendPath(SIZE)
                            .appendEncodedPath(POSTER_PATH)
                            .build();

                    Log.v(LOG_TAG, builtUri.toString());

                    Picasso.with(super.getContext()).load(builtUri).into(mPosterView);

                    // rating
                    String rating = data.getString(COL_DETAILS_RATING);
                    mRatingView.setText(rating);

                    // runtime
                    String runtime = data.getString(COL_DETAILS_RUNTIME);
                    mRuntimeView.setText(runtime);

                    // tagline
                    String tagline = data.getString(COL_DETAILS_TAGLINE);
                    mTaglineView.setText(tagline);

                    // genres
                    String genres = data.getString(COL_DETAILS_GENRES);
                    mGenresView.setText(genres);

                    // imdb
                    final String IMDB_ID = data.getString(COL_DETAILS_IMDB_ID);
                    mImdbBtnView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String IMDB_BASE_URL = "http://www.imdb.com/title";
                            // example:
                            // "http://www.imdb.com/title/<imdb_id>"
                            Uri imdbUri = Uri.parse(IMDB_BASE_URL).buildUpon().appendPath(IMDB_ID).build();
                            Log.d(LOG_TAG, imdbUri.toString());
                            startActivity(new Intent(Intent.ACTION_VIEW, imdbUri));
                        }
                    });

                    // google
                    mGoogleBtnView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String GOOGLE_BASE_URL = "http://www.google.com/search";
                            final String QUERY = "q";
                            final String SEARCH_TERM = title.trim();
                            // example:
                            // "http://www.google.com/#q=<movie_title>"
                            Uri googleUri = Uri.parse(GOOGLE_BASE_URL).buildUpon()
                                    .appendQueryParameter(QUERY, SEARCH_TERM)
                                    .build();
                            Log.d(LOG_TAG, googleUri.toString());
                            startActivity(new Intent(Intent.ACTION_VIEW, googleUri));
                        }
                    });

                    // rotten tomatoes
                    mRoTomatBtnView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String RT_BASE_URL = "http://www.rottentomatoes.com/search/";
                            final String SEARCH = "search";
                            final String SEARCH_TERM = title.trim();
                            // example:
                            // "http://www.rottentomatoes.com/search?search=<movie_title>"
                            Uri rtUri = Uri.parse(RT_BASE_URL).buildUpon()
                                    .appendQueryParameter(SEARCH, SEARCH_TERM)
                                    .build();
                            Log.d(LOG_TAG, rtUri.toString());
                            startActivity(new Intent(Intent.ACTION_VIEW, rtUri));
                        }
                    });

                    // SET STAR FAVORITE BUTTON
                    mStarFavorite.setChecked(true);

                    String[] movieArr =
                            {title, overview, POSTER_PATH, score, date, data.getString(COL_DETAILS_MOVIE_ID)};

                    mMovie = new MovieData(movieArr);

                    mStarFavorite.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            //ADD TO / DELETE FROM FAVORITES onDestroyView
                            mIsFavorite = !mIsFavorite;
                        }
                    });

                    break;

                case TRAILERS_LOADER:
                    // trailers
                    if (mTrailerCursorAdapter != null) {
                        Log.d(LOG_TAG, "Trailers Loader ID\nID == " + loader.getId());
                        mTrailerCursorAdapter.swapCursor(data);

                        mTrailerCursorAdapter.setOnItemClickListener(
                                new TrailerCursorAdapter.OnItemClickListener(){
                                    @Override
                                    public void onItemClick(View itemView, int position) {
                                        TrailerListItem trailerListItem =
                                                new TrailerListItem().fromCursor(data);

                                        final String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";
                                        final String TRAILER_PATH = trailerListItem.getImageUrl();

                                        // example:
                                        // http://www.youtube.com/watch?v=<trailer path>
                                        Uri trailerUri = Uri.parse(TRAILER_BASE_URL).buildUpon()
                                                .appendEncodedPath(TRAILER_PATH)
                                                .build();

                                        startActivity(
                                                new Intent(Intent.ACTION_VIEW, trailerUri));
                                    }
                                }
                        );

                    } else {
                        Log.e(LOG_TAG, "\nmFavTrailerAdapter is null");
                    }

                    break;

                case RECOMMENDATIONS_LOADER:
                    // recommendations
                    if (mRecCursorAdapter != null) {
                        Log.d(LOG_TAG, "Recommendations Loader ID\nID == " + loader.getId());
                        mRecCursorAdapter.swapCursor(data);
                    } else {
                        Log.e(LOG_TAG, "\nmFavRecAdapter is null");
                    }

//                    mRecsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                            Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                            if (cursor != null) {
//                                Uri contentUri = DataContract.FavoritesContract.buildMovieIdUri(
//                                        cursor.getString(COL_DETAILS_MOVIE_ID)//instead (of) get position
//                                        // "content://com.example.ben.moviepop.app.favorites.<movie_id>"
//                                );
//                                Log.d(LOG_TAG, "ITEM CLICKED at position: " + position + "\nuri created: " + contentUri.toString());
//                                Intent intent = new Intent(getActivity(), MovieInfoActivity.class)
//                                        .setData(contentUri);
//                                startActivity(intent);
//                            }
//                        }
//                    });
//
//                    break;

                default:
                    Log.e(LOG_TAG, "Loader id not recognized\nID == " + loader.getId());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mIsFavorite) {

            String[] projection = {DataContract.FavoritesContract.COLUMN_MOVIE_ID};

            final String selection = DataContract.FavoritesContract.TABLE_NAME +
                    "." + DataContract.FavoritesContract.COLUMN_MOVIE_ID + " = ? ";

            final String trailersSelection = DataContract.TrailersContract.TABLE_NAME + "." +
                    DataContract.TrailersContract.COLUMN_MOVIE_ID + " = ? ";

            final String recommendationsSelection = DataContract.RecommendationsContract.TABLE_NAME + "." +
                    DataContract.TrailersContract.COLUMN_MOVIE_ID + " = ? ";

            final String[] selectionArgs = {mMovie.movieId};//

            // content uri references whole table, selection references row, selectionArgs provides specific row
            getContext().getContentResolver().delete(
                    DataContract.FavoritesContract.CONTENT_URI, selection, selectionArgs);
            Log.d(LOG_TAG, mMovie.title + " movie deleted from database");//
            getContext().getContentResolver().delete(
                    DataContract.TrailersContract.CONTENT_URI,
                    trailersSelection,
                    selectionArgs);
            getContext().getContentResolver().delete(
                    DataContract.RecommendationsContract.CONTENT_URI,
                    recommendationsSelection,
                    selectionArgs);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
