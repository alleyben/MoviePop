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
    private FetchMoviesTask fetchMovies;
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
            fetchMovies = new FetchMoviesTask();
            fetchMovies.setAdapter(mMovieAdapter);
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

        fetchMovies.setAdapter(mMovieAdapter);

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

}
