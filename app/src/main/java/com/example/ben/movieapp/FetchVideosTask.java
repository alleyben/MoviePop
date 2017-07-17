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
import java.util.ArrayList;
import java.util.List;

public class FetchVideosTask extends AsyncTask<String, Void, List<TrailerData>> {

    private final String LOG_TAG = FetchVideosTask.class.getSimpleName();

    private TrailerAdapter mVidAdapter;

    @Override
    protected List<TrailerData> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String videosJsonStr = null;

        String movieId = params[0];

        try {

            //sample url:
            //http://api.themoviedb.org/3/movie/<movie_id>/videos?api_key=<api key>

            final String RESULTS_BASE_URL = "https://api.themoviedb.org/3/movie";
            final String API_KEY = "api_key";
            final String VIDEOS = "videos";

            Uri videosUri = Uri.parse(RESULTS_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(VIDEOS)
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter("region", "US")
                    .build();

            Log.v(LOG_TAG, videosUri.toString());

            URL url = new URL(videosUri.toString());

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

            videosJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Videos String: " + videosJsonStr);
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
            return getVideosFromJson(videosJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private List<TrailerData> getVideosFromJson(String jsonStr) throws JSONException {

        List<TrailerData> resultArr = new ArrayList<TrailerData>();
        final String RESULTS = "results";
        final String KEY = "key";
        final String TYPE = "type";
        final String TITLE = "name";
        final String TRAILER_TYPE = "Trailer";

        JSONObject videoJson = new JSONObject(jsonStr);
        JSONArray jsonArray = videoJson.getJSONArray(RESULTS);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject trailer = jsonArray.getJSONObject(i);
            if (trailer.getString(TYPE).equals(TRAILER_TYPE)) {
                resultArr.add(new TrailerData(
                        new String[]{
                                trailer.getString(TITLE),
                                trailer.getString(KEY)})
                );
            }
        }

        return resultArr;
    }

    @Override
    protected void onPostExecute(List<TrailerData> trailerArr) {

        for (TrailerData trailerData : trailerArr) {
            Log.d(LOG_TAG, trailerData.toString());
        }

        if (mVidAdapter != null) {
            Log.d(LOG_TAG, "Trailer Adapter correctly set");
            mVidAdapter.addArrayItems(trailerArr);
        } else {
            Log.e(LOG_TAG, "You forgot to set the RecommendationsAdapter, you fucktard");
        }
    }

    public void setAdapter(TrailerAdapter adapter) {
        mVidAdapter = adapter;
    }
}
