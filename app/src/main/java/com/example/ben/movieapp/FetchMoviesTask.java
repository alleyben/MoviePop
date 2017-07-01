package com.example.ben.movieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMoviesTask extends AsyncTask<String, Void, String[][]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private MoviesAdapter mMovieAdapter;

    private String[][] getMovieDataFromJson(String movieJsonStr) throws JSONException {

        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String POSTER_URL = "poster_path";
        final String SCORE = "vote_average";
        final String DATE = "release_date";
        final String MOVIEID = "id";


        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        String[][] resultStrArr = new String[movieArray.length()][];

        for(int i = 0; i < resultStrArr.length; i++) {

            JSONObject movieInfo = movieArray.getJSONObject(i);
            String movieTitle = movieInfo.getString(TITLE);
            String movieOverview = movieInfo.getString(OVERVIEW);
            String moviePosterUrl = movieInfo.getString(POSTER_URL);
            String movieRating = movieInfo.getString(SCORE);
            String movieDate = movieInfo.getString(DATE);
            String movieId = movieInfo.getString(MOVIEID);

            resultStrArr[i] =
                    new String[] {
                            movieTitle,
                            movieOverview,
                            moviePosterUrl,
                            movieRating,
                            movieDate,
                            movieId};
        }

        for(String[] strArr : resultStrArr) {
            Log.v(LOG_TAG, "Movie entry: " + strArr.toString());
        }

        return resultStrArr;
    }

    @Override
    protected void onPostExecute(String[][] result) {
        if (result != null) {
            mMovieAdapter.clear();

            for (String[] strArr : result) {
                mMovieAdapter.add(new MovieData(strArr));
            }
            // mMovieAdapter.addAll(Movieresult);
            // can't use this because need to
            // create moviedata objects
            // analyze parseable
        }
    }

    @Override
    protected String[][] doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        //if statements to determine which are placed in uri?
        //or get from param[0]
        // list pop by year
        String popularity = "popular";
        //can't find analogous top_rated in discover uri mode, maybe eliminate, would not change much anyway
        String rating = "top_rated";
        String newReleases = "new_releases";
        String upcoming = "upcoming";

        String sortBy = params[0];

        //check for wrong input
        if (!sortBy.equals(popularity) && !sortBy.equals(rating) && !sortBy.equals(upcoming)) {
            Log.e(LOG_TAG, "Incorrect input for sort type. Could not render movie data");
            return null;
        }

        try {

            //sample url:
            //http://api.themoviedb.org/3/movie/top_rated?api_key=<api key>

            final String RESULTS_BASE_URL = "https://api.themoviedb.org/3/movie";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(RESULTS_BASE_URL).buildUpon()
                    .appendPath(sortBy)
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter("region", "US")
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

    public void setAdapter(MoviesAdapter adapter) {
        mMovieAdapter = adapter;
    }
}
