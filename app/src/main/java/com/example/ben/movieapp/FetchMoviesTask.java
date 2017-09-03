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
import java.util.Arrays;

public class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private MoviesAdapter mMovieAdapter;
    private RecommendationsListAdapter mRecommendationsListAdapter;
    private boolean mRecBool = false;

    @Override
    protected MovieData[] doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

//        String popularity = "popular";
        final String PLAYING = "now_playing";
        final String RATING = "top_rated";
        final String UPCOMING = "upcoming";
        final String RECOMMENDATIONS = "similar"; // or "recommendations"

        String sortBy;

        //check for wrong input from params (i.e. from execute method)
        switch (params[0]) {
            case PLAYING:
                sortBy = PLAYING;
                break;
            case RATING:
                sortBy = RATING;
                break;
            case UPCOMING:
                sortBy = UPCOMING;
                break;
            default:
                if (params[0].matches("[0-9]+")) {
                    mRecBool = true;
                    sortBy = params[0];
                } else {
                    Log.e(LOG_TAG, "Incorrect input for sort type.\nParam input = " + params[0] +
                            "\nCould not render movie data");
                    return null;
                }
        }

//        if (params[1] != null) {
            // TODO get by params: dates, genre, rating, etc; (check out available search args for tmdb)
//        }

        // TODO refine by rating, genre (more? country, language)
        // TODO "Top Rated" will be searchable, negative, new fragment tab
        // TODO text searchable vs predefined parameters
        // TODO (maybe) login, create account with tmdb feature, rate a movie, save favs to online db

        try {

            //sample url:
            //http://api.themoviedb.org/3/movie/top_rated?api_key=<api key>

            final String RESULTS_BASE_URL = "https://api.themoviedb.org/3/movie";
            final String API_KEY = "api_key";

            Uri.Builder uriBuilder = Uri.parse(RESULTS_BASE_URL).buildUpon()
                    .appendPath(sortBy);
            if(mRecBool) { //sortBy equals some movie id
                uriBuilder.appendPath(RECOMMENDATIONS);
            }
            Uri builtUri = uriBuilder
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter("region", "US")
                    .build();
            // TODO setting for country and language (but not linked)

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

    private MovieData[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

        final String RESULTS = "results";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String POSTER_URL = "poster_path";
        final String SCORE = "vote_average";
        final String DATE = "release_date";
        final String MOVIEID = "id";


        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        MovieData[] resultMovieArr = new MovieData[movieArray.length()];

        for(int i = 0; i < resultMovieArr.length; i++) {

            JSONObject movieInfo = movieArray.getJSONObject(i);
            String movieTitle = movieInfo.getString(TITLE);
            String movieOverview = movieInfo.getString(OVERVIEW);
            String moviePosterUrl = movieInfo.getString(POSTER_URL);
            String movieRating = movieInfo.getString(SCORE);
            String movieDate = formatDate(movieInfo.getString(DATE));
            String movieId = movieInfo.getString(MOVIEID);

            resultMovieArr[i] = new MovieData(new String[] {
                            movieTitle,
                            movieOverview,
                            moviePosterUrl,
                            movieRating,
                            movieDate,
                            movieId});
        }

        for(MovieData movie : resultMovieArr) {
            Log.v(LOG_TAG, "Movie entry: " + movie.toString() + "\n");
        }

        return resultMovieArr;
    }

    @Override
    protected void onPostExecute(MovieData[] result) {
        if (result != null) {
            if (mRecBool) {
                mRecommendationsListAdapter.addArrayItems(Arrays.asList(result));

            } else {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(result);
            }
        }
    }

    public void setAdapter(MoviesAdapter adapter) {
        mMovieAdapter = adapter;
    }

    public void setRecsAdapter(RecommendationsListAdapter adapter) {
        mRecommendationsListAdapter = adapter;
    }

    private String formatDate(String jsonDate) {
        // takes date from api and changes to dot format
        StringBuilder formattedDate = new StringBuilder();
        String[] dateArr = jsonDate.split("-");
        for (int i = 0; i < dateArr.length; i++) {
            formattedDate.append(dateArr[i]);
            if (i < dateArr.length-1) {
                formattedDate.append("•");
            }
        }
        return formattedDate.toString();
    }
}
