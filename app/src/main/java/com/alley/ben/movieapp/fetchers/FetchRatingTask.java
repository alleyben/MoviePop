package com.example.ben.movieapp.fetchers;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ben.movieapp.BuildConfig;
import com.example.ben.movieapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchRatingTask extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = FetchRatingTask.class.getSimpleName();
    private View mRootView;

    @Override
    protected String doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String ratingJsonStr = null;

        String movieId = params[0];

        try {

            //sample url:
            //http://api.themoviedb.org/3/movie/top_rated?api_key=<api key>

            final String RESULTS_BASE_URL = "https://api.themoviedb.org/3/movie";
            final String API_KEY = "api_key";

            Uri detailsUri = Uri.parse(RESULTS_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath("release_dates")
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
//                    .appendQueryParameter("region", "US")
                    .build();
            // TODO setting for country and language (but not linked)

            Log.v(LOG_TAG, detailsUri.toString());

            URL url = new URL(detailsUri.toString());

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

            ratingJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Details String: " + ratingJsonStr);
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
            return getRatingFromJson(ratingJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private String getRatingFromJson(String ratingJsonStr) throws JSONException {

        final String RESULTS = "results";
        final String RATING = "certification";
        final String COUNTRYCODE = "iso_3166_1";
        final String COUNTRY = "US";
        final String RELEASEDATE = "release_dates";

        JSONObject ratingJson = new JSONObject(ratingJsonStr);
        JSONArray countriesArray = ratingJson.getJSONArray(RESULTS);

        for(int i = 0; i < countriesArray.length(); i++) {
            JSONObject countryInfo = countriesArray.getJSONObject(i);
            if (countryInfo.getString(COUNTRYCODE).equals(COUNTRY)) {
                JSONArray releasesArray = countryInfo.getJSONArray(RELEASEDATE);
                JSONObject releaseInfo = releasesArray.getJSONObject(0);
                String rating = releaseInfo.getString(RATING);
                Log.d(LOG_TAG, "Rating for movie is "+ rating);

                return rating;
            }
        }

        Log.d(LOG_TAG, "There is no Rating for this movie");

        return "Not Rated";
        // TODO change to xml string value that is translatable to not rated
    }

    @Override
    protected void onPostExecute(String s) {
//        super.onPostExecute(s);

        if (mRootView != null) {
            TextView rating = (TextView) mRootView.findViewById(R.id.fragment_movie_info_rating);
            rating.setText(s);
        }
    }

    public void setView(View view) {
        mRootView = view;
    }
}
