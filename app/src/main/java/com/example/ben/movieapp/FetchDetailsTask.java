//package com.example.ben.movieapp;
//
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class FetchDetailsTask extends AsyncTask<String, Void, String[]> {
//    // TODO fetch details task: youtube trailers, similar movies - list views
//    // homepage
//    // imdb_id
//    // runtime
//    // tagline
//    // rating
//    // runtime
//    // google / theater times and locations
//    // rotten tomatoes
//    // meta critic
//
//    private static final String LOG_TAG = FetchDetailsTask.class.getSimpleName();
//
//    @Override
//    protected String[] doInBackground(String... params) {
//
//        if (params.length == 0) {
//            return null;
//        }
//
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String detailsJsonStr = null;
//
//
//        try {
//
//            //sample url:
//            //http://api.themoviedb.org/3/movie/top_rated?api_key=<api key>
//
//            final String RESULTS_BASE_URL = "https://api.themoviedb.org/3/movie";
//            final String API_KEY = "api_key";
//
//            Uri detailsUri = Uri.parse(RESULTS_BASE_URL).buildUpon()
//                    .appendPath(movieId)
//                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
//                    .appendQueryParameter("region", "US")
//                    .build();
//            // TODO setting for country and language (but not linked)
//
//            Log.v(LOG_TAG, detailsUri.toString());
//
//            URL url = new URL(builtUri.toString());
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
//            detailsJsonStr = buffer.toString();
//
//            Log.v(LOG_TAG, "Details String: " + detailsJsonStr);
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
//            return getDetailsFromJson(detailsJsonStr);
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private String[] getDetailsFromJson(String movieJsonStr) throws JSONException {
//
//        final String RESULTS = "results";
//
//
//        JSONObject movieJson = new JSONObject(movieJsonStr);
//        JSONArray movieArray = movieJson.getJSONArray(RESULTS);
//
//        String[] resultStrArr = new String[movieArray.length()][];
//
//        for(int i = 0; i < resultStrArr.length; i++) {
//
//            JSONObject movieInfo = movieArray.getJSONObject(i);
//            String movieTitle = movieInfo.getString(TITLE);
//            String movieOverview = movieInfo.getString(OVERVIEW);
//            String moviePosterUrl = movieInfo.getString(POSTER_URL);
//            String movieRating = movieInfo.getString(SCORE);
//            String movieDate = movieInfo.getString(DATE);
//            String movieId = movieInfo.getString(MOVIEID);
//
//            resultStrArr[i] =
//                    new String[] {
//                            movieTitle,
//                            movieOverview,
//                            moviePosterUrl,
//                            movieRating,
//                            movieDate,
//                            movieId};
//        }
//
//        Log.v(LOG_TAG, "Movie entry: " + resultStrArr.toString());
//
//        return resultStrArr;
//    }
//}
