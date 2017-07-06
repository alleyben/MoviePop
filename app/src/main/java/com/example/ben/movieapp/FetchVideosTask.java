package com.example.ben.movieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchVideosTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchVideosTask.class.getSimpleName();

    @Override
    protected String[] doInBackground(String... params) {

//        if (params.length == 0) {
//            return null;
//        }
//
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String videosJsonStr = null;
//
//        String movieId = params[0];
//
//        try {
//
//            //sample url:
//            //http://api.themoviedb.org/3/movie/<movie_id>/videos?api_key=<api key>
//
//            final String RESULTS_BASE_URL = "https://api.themoviedb.org/3/movie";
//            final String API_KEY = "api_key";
//
//            Uri videosUri = Uri.parse(RESULTS_BASE_URL).buildUpon()
//                    .appendPath(movieId)
//                    .appendPath("videos")
//                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
//                    .appendQueryParameter("region", "US")
//                    .build();
//
//            Log.d(LOG_TAG, videosUri.toString());
//
//            URL url = new URL(videosUri.toString());
//
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            InputStream inputStream = urlConnection.getInputStream();
//            StringBuffer buffer = new StringBuffer();
//            if (inputStream == null) {
//                return null;
//            }
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line + "\n");
//            }
//
//            if (buffer.length() == 0) {
//                return null;
//            }
//
//            videosJsonStr = buffer.toString();
//
//            Log.d(LOG_TAG, "Videos String: " + videosJsonStr);
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "ERROR ", e);
//            return null;
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (final IOException e) {
//                    Log.e(LOG_TAG, "ERROR closing stream", e);
//                }
//            }
//        }
//
//        try {
//            return getRecommendationsFromJson(videosJsonStr);
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
//        }

        return null;
    }
}
