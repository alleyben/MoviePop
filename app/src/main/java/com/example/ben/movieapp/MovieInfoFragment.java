package com.example.ben.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieInfoFragment extends Fragment {

    private static final String LOG_TAG = MovieInfoFragment.class.getSimpleName();
    private MovieData mMovie;
    private boolean isFavorite;

    public MovieInfoFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movieInfoTag")) {
            mMovie = intent.getParcelableExtra("movieInfoTag");

            // TODO: make following changes
            // Get movie id and run task to get mpaa rating and trailer urls
            // also more review scores, google link, share link etc.
            // FetchMovieDetailsTask fetchMovies = new FetchMovieDetailsTask();
            // fetchMovies.execute(mMovie.movieId);

            // Set title
            ((TextView) rootView.findViewById(R.id.fragment_movie_info_title))
                    .setText(mMovie.title);

            // Get star favorite button
            CheckBox starFavorite = (CheckBox) rootView.findViewById(R.id.star_button);
            //CHECK FROM FAVORITES
            String[] projection = {DataContract.FavoritesEntry.COLUMN_MOVIE_ID};
            final String selection = DataContract.FavoritesEntry.TABLE_NAME +
                    "." + DataContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";
            final String[] selectionArgs = {mMovie.movieId};
            Cursor cursor = getContext().getContentResolver().query(
                    DataContract.FavoritesEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );
            isFavorite = (cursor.getCount() != 0);
            starFavorite.setChecked(isFavorite);
            starFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ADD TO FAVORITES
                    if (isFavorite) {
                        // content uri references whole table, selection references row, selectionArgs provides specific row
                        getContext().getContentResolver().delete(
                                DataContract.FavoritesEntry.CONTENT_URI, selection, selectionArgs);
                    } else {
                        ContentValues movieInfo = mMovie.getMovieDataCV();
                        getContext().getContentResolver().insert(
                                DataContract.FavoritesEntry.CONTENT_URI, movieInfo);
                    }
                    isFavorite = !isFavorite;
                }
            });

            // Set poster image
            ImageView posterView =
                    (ImageView) rootView.findViewById(R.id.fragment_movie_info_poster);

            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
            final String SIZE = "w500";
            final String POSTER_PATH = mMovie.posterPath;

            Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                    .appendPath(SIZE)
                    .appendEncodedPath(POSTER_PATH)
                    .build();

            Log.v(LOG_TAG, builtUri.toString());

            Picasso.with(super.getContext()).load(builtUri).into(posterView);

            // Set details (date, avgScore)
            String movieDate = new StringBuilder("Release Date:\n").append(mMovie.date).toString();
            ((TextView) rootView.findViewById(R.id.fragment_movie_info_date))
                    .setText(movieDate);
            ((TextView) rootView.findViewById(R.id.fragment_movie_info_avg_score))
                    .setText("User Score:\n" + mMovie.avgScore);

            // Set overview
            ((TextView) rootView.findViewById(R.id.fragment_movie_info_overview))
                    .setText(mMovie.overview);
        }
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
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.title);
        return shareIntent;
    }

    public class FetchMovieDetailsTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            final String RESULTS = "results";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String POSTER_URL = "poster_path";
            final String SCORE = "vote_average";
            final String DATE = "release_date";
            final String RATING = "rating";
            final String VIDEOS = "videos";
            final String REVIEWS = "reviews";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            String[] resultStrArr = new String[movieArray.length()];

            for(int i = 0; i < resultStrArr.length; i++) {

                JSONObject movieInfo = movieArray.getJSONObject(i);
                String movieTitle = movieInfo.getString(TITLE);
                String movieOverview = movieInfo.getString(OVERVIEW);
                String moviePosterUrl = movieInfo.getString(POSTER_URL);
                String movieRating = movieInfo.getString(SCORE);
                String movieDate = movieInfo.getString(DATE);
                String splitter = "~#~";

                resultStrArr[i] = new StringBuilder(movieTitle)
                        .append(splitter)
                        .append(movieOverview)
                        .append(splitter)
                        .append(moviePosterUrl)
                        .append(splitter)
                        .append(movieRating)
                        .append(splitter)
                        .append(movieDate)
                        .toString();
            }

            for(String s : resultStrArr) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }

            return resultStrArr;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
//                mMovieAdapter.clear();

                for (String s : result) {
                    String[] arr = s.split("~#~");
//                    mMovieAdapter.add(new MovieData(arr));
                }
                //mMovieAdapter.addAll(Movieresult);
            }
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            //if statements to determine which are placed in uri?
            //or get from param[0]
            String popularity = "popular";
            String rating = "top_rated";
            String sortBy = params[0];

            //check for wrong input
            if (!sortBy.equals(popularity) && !sortBy.equals(rating)) {
                Log.e(LOG_TAG, "Incorrect input for sort type.\nCould not render movie data");
                return null;
            }

            try {

                //sample url
                //http://api.themoviedb.org/3/movie/top_rated?api_key=<api key>

                final String RESULTS_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(RESULTS_BASE_URL).buildUpon()
                        .appendPath(sortBy)
                        .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                        .build();

                Log.v(LOG_TAG, builtUri.toString());

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie String: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "ERROR ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "ERROR closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }
}
