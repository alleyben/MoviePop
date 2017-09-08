package com.example.ben.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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


//    The fragment argument representing the section number for this fragment.
    private static final String ARG_TAB_NUMBER = "tab_number";
    private int mTabNumber;
    private MoviesAdapter mMovieAdapter;
    private static final String LOG_TAG = FrontFragment.class.getSimpleName();

//    private OnFragmentInteractionListener mListener;
    //don't know how to use this

    public static FrontFragment newInstance(int tabNumber) {
        FrontFragment fragment = new FrontFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_NUMBER, tabNumber);
        fragment.setArguments(args);
        // bundling seems unnecessary, but maybe this keeps the tab number with its instance/object
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabNumber = getArguments().getInt(ARG_TAB_NUMBER);
        }
        setHasOptionsMenu(false);
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_front_frag, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            fetchMovies = new FetchMoviesTask();
            fetchMovies.setAdapter(mMovieAdapter);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
            fetchMovies.execute(sortBy);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter =
                new MoviesAdapter(
                        getActivity(),// i.e. main activity
                        new ArrayList<MovieData>());

        View rootView = inflater.inflate(R.layout.fragment_front, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);

        String sortBy;
        switch (mTabNumber) {
            case 0:
                sortBy = "now_playing";
                break;
            case 1:
                sortBy = "top_rated";
                break;
            case 2:
                sortBy = "upcoming";
                break;
            case 3:
                sortBy = "favorites";
                break;
            default:
                sortBy = null;
        }

        if (sortBy.equals("favorites")) {
            Log.e(LOG_TAG, "sortBy EQUALS FAVORITE\nmTabNumber equals " + mTabNumber);
        } else {
            gridView.setAdapter(mMovieAdapter);
            FetchMoviesTask fetchMovies = new FetchMoviesTask();
            fetchMovies.execute(sortBy);
            fetchMovies.setAdapter(mMovieAdapter);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Start MovieInfoActivity to show details for selected movie
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

}
