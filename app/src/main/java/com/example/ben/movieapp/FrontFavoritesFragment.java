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

public class FrontFavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_TAB_NUMBER = "tab_number";
    private int mTabNumber;
    private FavoritesAdapter mFavoritesAdapter;
    private static final String LOG_TAG = FrontFragment.class.getSimpleName();

    private static final int FAVORITES_LOADER = 0;

    static final int COL_MOVIE_ID = 1;
    // column indeces in order starting with 0:
    // database row id, tmdb movie id, title, overview, poster url, score, date

    public static FrontFavoritesFragment newInstance(int tabNumber) {
        FrontFavoritesFragment fragment = new FrontFavoritesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_NUMBER, tabNumber);
        fragment.setArguments(args);

        Log.d(LOG_TAG, "NEW INSTANCE OF FAVORITEFRAGMENT");

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

    /**@Override
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

        View rootView = inflater.inflate(R.layout.fragment_front, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);

        if (mTabNumber == 3) {

            Log.d(LOG_TAG, "Tab number EQUALS 3!!!\nFavoritesAdapter initiated\nmTabNumber equals "
                    + mTabNumber + "\n");

            gridView.setAdapter(mFavoritesAdapter);
            mFavoritesAdapter = new FavoritesAdapter(getActivity(), null, 0);

        } else {
            Log.d(LOG_TAG, "Tab number EQUAlS " + mTabNumber + "\nhow did this happen\nmTabNumber equals " + mTabNumber);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Uri contentUri = DataContract.FavoritesEntry.buildMovieIdUri(
                            cursor.getString(COL_MOVIE_ID)//instead (of) get position
                    );
                    Log.d(LOG_TAG, "ITEM CLICKED at position: " + position + "\nuri created: " + contentUri.toString());
                    Intent intent = new Intent(getActivity(), MovieInfoActivity.class)
                            .setData(contentUri);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVORITES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

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
        Log.d(LOG_TAG, "onLoadFinished initiated\nCursor has items: " + data.getCount());
        mFavoritesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoritesAdapter.swapCursor(null);
        Log.d(LOG_TAG, "onLoaderReset initiated\ncursor for FavoritesAdapter == null");
    }
}
