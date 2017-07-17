package com.example.ben.movieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchDetailsTask extends AsyncTask<String, Void, List<Map<String, String>>> {
    // TODO fetch details task: youtube trailers, similar movies - list views
    // homepage
    // imdb_id
    // runtime
    // tagline
    // rating
    // runtime
    // google / theater times and locations
    // rotten tomatoes
    // meta critic

    private static final String LOG_TAG = FetchDetailsTask.class.getSimpleName();
    private View mRootView;

    @Override
    protected List<Map<String, String>> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String detailsJsonStr = null;

        String movieId = params[0];

        try {

            //sample url:
            //http://api.themoviedb.org/3/movie/<movie_id>?api_key=<api key>

            final String RESULTS_BASE_URL = "https://api.themoviedb.org/3/movie";
            final String API_KEY = "api_key";

            Uri detailsUri = Uri.parse(RESULTS_BASE_URL).buildUpon()
                    .appendPath(movieId)
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

            detailsJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Details String: " + detailsJsonStr);
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
            return getDetailsFromJson(detailsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private List<Map<String, String>> getDetailsFromJson(String movieJsonStr) throws JSONException {

        final String IMDB_ID = "imdb_id";
        final String RUNTIME = "runtime";
        final String TAGLINE = "tagline";
        final String GENRES = "genres";
        final String GENRE_NAME = "name";


        JSONObject detailsJson = new JSONObject(movieJsonStr);

        String detailsImdb_id = detailsJson.getString(IMDB_ID);
        String detailsRuntime = detailsJson.getString(RUNTIME);
        String detailsTagline = detailsJson.getString(TAGLINE);

        StringBuilder genresStr = new StringBuilder();
        JSONArray genresArr = detailsJson.getJSONArray(GENRES);
        for (int i = 0; i < genresArr.length(); i++) {
            JSONObject genresJSON = genresArr.getJSONObject(i);
            genresStr.append(genresJSON.getString(GENRE_NAME));
            if (i < genresArr.length()-1) {
                genresStr.append(" • ");
            }
        }
        String detailsGenres = genresStr.toString();

        Map<String, String> resultMap = new HashMap();

        resultMap.put(IMDB_ID, detailsImdb_id);
        resultMap.put(RUNTIME, detailsRuntime);
        resultMap.put(GENRES, detailsGenres);
        resultMap.put(TAGLINE, detailsTagline);

        Log.v(LOG_TAG, "Movie entry: " + resultMap.toString());

        List<Map<String, String>> resultList = new ArrayList<>();
        resultList.add(resultMap);

        return resultList;
    }

    @Override
    protected void onPostExecute(List<Map<String, String>> result) {
//        super.onPostExecute(stringStringMap);
        if (result != null) {
//            mDetailsAdapter.clear();
//            mDetailsAdapter.addAll(result);
            Map<String, String> detailsMap = result.get(0);
            Log.v(LOG_TAG, "Results are: " + detailsMap.toString());

            final String IMDB_ID = "imdb_id";
            final String RUNTIME = "runtime";
            final String GENRES = "genres";
            final String TAGLINE = "tagline";

            if (mRootView != null) {

                TextView imdbId = (TextView) mRootView.findViewById(R.id.fragment_movie_info_imdb_id);
                imdbId.setText(detailsMap.get(IMDB_ID));

                TextView runtime = (TextView) mRootView.findViewById(R.id.fragment_movie_info_runtime);
                runtime.setText(detailsMap.get(RUNTIME) + " minutes");

                TextView genres = (TextView) mRootView.findViewById(R.id.fragment_movie_info_genres);
                genres.setText(detailsMap.get(GENRES));

                TextView tagline = (TextView) mRootView.findViewById(R.id.fragment_movie_info_tagline);
                tagline.setText(detailsMap.get(TAGLINE));
            }

            //youtube links buttons, img-btn or ImageView

        }
    }

    public void setView(View view) {
        mRootView = view;
    }
}
