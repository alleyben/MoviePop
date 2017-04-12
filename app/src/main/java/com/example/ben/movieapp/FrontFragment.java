package com.example.ben.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FrontFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FrontFragment#newInstance} factory method to
 * create an instance of this fragment.
 **/

public class FrontFragment extends Fragment {

    private ImageAdapter mMovieAdapter;
    // TODO: change "String" to image file... jpeg?
    // negative, string stays, url of image jpeg

    /*# TOD: Rename parameter arguments, choose names that match [needed?]
    * //the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    * private static final String ARG_PARAM1 = "param1";
    * private static final String ARG_PARAM2 = "param2";

    * //# TOD: Rename and change types of parameters [needed?]
    * private String mParam1;
    * private String mParam2;

    * private OnFragmentInteractionListener mListener;
    */

    public FrontFragment() {
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FrontFragment.
     */

    /*# TOD: Rename and change types and number of parameters [needed?]
    public static FrontFragment newInstance(String param1, String param2) {
        FrontFragment fragment = new FrontFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [needed?]
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_front_frag, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            //TODO: FetchMoviesTask
            FetchMoviesTask fetchMovies = new FetchMoviesTask();
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
            fetchMovies.execute(sortBy);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter =
                new ImageAdapter(
                        getActivity(),
                        new ArrayList<MovieData>());

        View rootView = inflater.inflate(R.layout.fragment_front, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));
        FetchMoviesTask fetchMovies = new FetchMoviesTask();
        fetchMovies.execute(sortBy);

        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieData movieData = mMovieAdapter.getItem(position);
                startActivity(
                        new Intent(getActivity(), MovieInfoActivity.class)
                                .putExtra("movieInfoTag", movieData));
            }
        });

        return rootView;
    }



    /* TOD: Rename method, update argument and hook method into UI event [needed?]
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        //# TOD: Update argument type and name [needed?]
        void onFragmentInteraction(Uri uri);
    }*/

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException{

            final String RESULTS = "results";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String POSTER_URL = "poster_path";
            final String RATING = "vote_average";
            final String DATE = "release_date";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            String[] resultStrArr = new String[movieArray.length()];

            for(int i = 0; i < resultStrArr.length; i++) {

                JSONObject movieInfo = movieArray.getJSONObject(i);
                String movieTitle = movieInfo.getString(TITLE);
                String movieOverview = movieInfo.getString(OVERVIEW);
                String moviePosterUrl = movieInfo.getString(POSTER_URL);
                String movieRating = movieInfo.getString(RATING);
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
                mMovieAdapter.clear();

                for (String s : result) {
                    String[] arr = s.split("~#~");
                    mMovieAdapter.add(new MovieData(arr));
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
