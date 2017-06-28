package com.example.ben.movieapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ben.movieapp.data.DataContract;

import java.util.ArrayList;

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FrontFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FrontFragment#newInstance} factory method to
 * create an instance of this fragment.
 **/

public class FrontFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_TAB_NUMBER = "tab_number";
    private int mTabNumber;
    private MoviesAdapter mMovieAdapter;
    private FavoritesAdapter mFavoritesAdapter;
    private GridView mGridView;
    private static final String LOG_TAG = FrontFragment.class.getSimpleName();

    private static final int FAVORITES_LOADER = 0;

    private static final String[] FAVORITES_COLUMNS = {
            DataContract.FavoritesEntry.COLUMN_MOVIE_ID,
            DataContract.FavoritesEntry.COLUMN_TITLE,
            DataContract.FavoritesEntry.COLUMN_OVERVIEW,
            DataContract.FavoritesEntry.COLUMN_POSTER_URL,
            DataContract.FavoritesEntry.COLUMN_SCORE,
            DataContract.FavoritesEntry.COLUMN_DATE//,
//            DataContract.FavoritesEntry.COLUMN_TRAILER_URL,
//            DataContract.FavoritesEntry.COLUMN_RATING
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_OVERVIEW = 2;
    static final int COL_POSTER_URL = 3;
    static final int COL_SCORE = 4;
    static final int COL_DATE = 5;
//    static final int COL_TRAILER_URL = 6;
//    static final int COL_RATING = 7;

//    private OnFragmentInteractionListener mListener;

    public static FrontFragment newInstance(int tabNumber) {
        FrontFragment fragment = new FrontFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_NUMBER, tabNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabNumber = getArguments().getInt(ARG_TAB_NUMBER);
            Log.d(LOG_TAG, ARG_TAB_NUMBER + " mTabNumber equals " + mTabNumber);
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
            //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
            String sortBy = "";
            switch (mTabNumber) {
                case 0:
                    sortBy = "popular";
                    break;
                case 1:
                    sortBy = "top_rated";
                    break;
                case 2:
                    sortBy = "upcoming";
                    break;
            }
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
                        getActivity(),
                        new ArrayList<MovieData>());

        View rootView = inflater.inflate(R.layout.fragment_front, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.grid_view_posters);

        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));
        String sortBy = "";
        switch (mTabNumber) {
            case 0:
                sortBy = "popular";
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
        }

        if (sortBy.equals("favorites")) {
            //mOpenHelper = dbHelper which is accessed through provider which is access through content resolver
//            FavoritesDbHandler handler = new FavoritesDbHandler(this);
//            SQLiteDatabase db = handler.getReadableDatabase();
//            Cursor cursor = db.rawQuery("SELECT * FROM " + DataContract.FavoritesEntry.TABLE_NAME, null);
//
//            Cursor cursor = getContext().getContentResolver().query(DataContract.FavoritesEntry.CONTENT_URI, null, null, null, null);
//            cursor.moveToFirst();
//            String title = cursor.getString(COL_TITLE);
            Log.d(LOG_TAG, "sortBy equals favorite ?= " + sortBy +
                    "\nFavoritesAdapter initiated\nmTabNumber equals " + mTabNumber + "\n ");
            mFavoritesAdapter = new FavoritesAdapter(getActivity(), null, 0);
            mGridView.setAdapter(mFavoritesAdapter);
        } else {
            Log.d(LOG_TAG, "sortBy is NOT favorites ?= " + sortBy + "\nMovieAdapter and FetchMovieTask initiated\nmTabNumber equals " + mTabNumber);
            mGridView.setAdapter(mMovieAdapter);
            FetchMoviesTask fetchMovies = new FetchMoviesTask();
            fetchMovies.execute(sortBy);
            fetchMovies.setAdapter(mMovieAdapter);
        }

        //need another on item click listener that reads from database
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (mTabNumber > 2) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        Uri contentUri = DataContract.FavoritesEntry.buildMovieIdUri(
                                cursor.getString(COL_MOVIE_ID)//instead get position
                        );
                        Intent intent = new Intent(getActivity(), MovieInfoActivity.class)
                                .setData(contentUri);
                        startActivity(intent);
                    }
                } else {
                    MovieData movieData = mMovieAdapter.getItem(position);
                    startActivity(
                            new Intent(getActivity(), MovieInfoActivity.class)
                                    .putExtra("movieInfoTag", movieData));
                }
            }
        });

        return rootView;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        getLoaderManager().initLoader(FAVORITES_LOADER, null, this);
//        super.onActivityCreated(savedInstanceState);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                DataContract.FavoritesEntry.CONTENT_URI,
                null,//FAVORITES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mTabNumber == 3) {
            mFavoritesAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoritesAdapter.swapCursor(null);
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
