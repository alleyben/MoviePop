package com.alley.ben.movieapp.fetchers;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alley.ben.movieapp.BuildConfig;
import com.alley.ben.movieapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FetchDetailsTask extends AsyncTask<String, Void, Map<String, String>> {
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

    // new items migrated to fetchDetails rather than fetchMovies:
    // title, overview, score, date

    private static final String LOG_TAG = FetchDetailsTask.class.getSimpleName();
    private View mRootView;
    private Context mContext;
    private final String TITLE = "title";
    private final String OVERVIEW = "overview";
    private final String SCORE = "vote_average";
    private final String DATE = "release_date";
    private final String IMDB_ID = "imdb_id";
    private final String RUNTIME = "runtime";
    private final String TAGLINE = "tagline";
    private final String GENRES = "genres";
    private final String GENRE_NAME = "name";

    @Override
    protected Map<String, String> doInBackground(String... params) {

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

    private Map<String, String> getDetailsFromJson(String movieJsonStr) throws JSONException {

        JSONObject detailsJson = new JSONObject(movieJsonStr);

        //title, overview, score date

        String detailsTitle = detailsJson.getString(TITLE);
        String detailsOverview = detailsJson.getString(OVERVIEW);
        String detailsScore = detailsJson.getString(SCORE);
        String detailsDate = formatDate(detailsJson.getString(DATE));
        String detailsImdb_id = detailsJson.getString(IMDB_ID);
        String detailsRuntime = detailsJson.getString(RUNTIME);
        String detailsTagline = detailsJson.getString(TAGLINE);
        String detailsGenres = formatGenres(detailsJson.getJSONArray(GENRES));

        Map<String, String> resultMap = new HashMap();

        resultMap.put(TITLE, detailsTitle);
        resultMap.put(OVERVIEW, detailsOverview);
        resultMap.put(SCORE, detailsScore);
        resultMap.put(DATE, detailsDate);
        resultMap.put(IMDB_ID, detailsImdb_id);
        resultMap.put(RUNTIME, detailsRuntime);
        resultMap.put(GENRES, detailsGenres);
        resultMap.put(TAGLINE, detailsTagline);

        return resultMap;
    }

    @Override
    protected void onPostExecute(Map<String, String> detailsMap) {
//        super.onPostExecute(stringStringMap);
        if (detailsMap != null) {
//            mDetailsAdapter.clear();
//            mDetailsAdapter.addAll(result);

            if (mRootView != null) {

                //title, overview, score, date

                TextView title = (TextView) mRootView.findViewById(R.id.fragment_movie_info_title);
                title.setText(detailsMap.get(TITLE));

                TextView overview = (TextView) mRootView.findViewById(R.id.fragment_movie_info_overview);
                overview.setText(detailsMap.get(OVERVIEW));

                TextView score = (TextView) mRootView.findViewById(R.id.fragment_movie_info_avg_score);
                String scoreStr = new StringBuilder("User Score:\n")
                        .append(detailsMap.get(SCORE))
                        .toString();
                score.setText(scoreStr);

                TextView date = (TextView) mRootView.findViewById(R.id.fragment_movie_info_date);
                String dateStr = new StringBuilder("Release Date:\n")
                        .append(detailsMap.get(DATE))
                        .toString();
                date.setText(dateStr);

                TextView imdbId = (TextView) mRootView.findViewById(R.id.fragment_movie_info_imdb_id);
                imdbId.setText(detailsMap.get(IMDB_ID));

                TextView runtime = (TextView) mRootView.findViewById(R.id.fragment_movie_info_runtime);
                String rawRuntimeStr = detailsMap.get(RUNTIME);
//                String formattedRuntime = "%s minutes", rawRuntimeStr;
//                runtime.setText(formattedRuntime);
                String minutes = mContext.getString(R.string.details_frag_runtime_minutes);
                runtime.setText(rawRuntimeStr + " " + minutes);

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

    public void setContext(Context context) {
        mContext = context;
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

    private String formatGenres(JSONArray jsonGenres) throws JSONException{
        StringBuilder formattedGenres = new StringBuilder();
        int arrLength = jsonGenres.length();
        int arrLimit = arrLength - 1;
        for (int i = 0; i < arrLength; i++) {
            JSONObject genresJSON = jsonGenres.getJSONObject(i);
            formattedGenres.append(genresJSON.getString(GENRE_NAME));
            if (i < arrLimit) {
                formattedGenres.append(" • ");
            }
        }
        return formattedGenres.toString();
    }
}
