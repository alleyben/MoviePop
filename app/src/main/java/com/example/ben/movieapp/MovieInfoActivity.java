package com.example.ben.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieInfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_movie_info_container, new MovieInfoFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: change menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //change menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //if (id == R.id.action_share) {
            //startActivity(new Intent());
            //return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    public static class MovieInfoFragment extends Fragment {

        private static final String LOG_TAG = MovieInfoFragment.class.getSimpleName();
        private MovieData mMovie;

        public MovieInfoFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("movieInfoTag")) {
                mMovie = intent.getParcelableExtra("movieInfoTag");

                // Set title
                ((TextView) rootView.findViewById(R.id.fragment_movie_info_title))
                        .setText(mMovie.title);

                // Set poster image
                ImageView posterView =
                        (ImageView) rootView.findViewById(R.id.fragment_movie_info_poster);

                final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                final String SIZE = "w500";
                final String POSTER_PATH = mMovie.posterPath;

                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendPath(SIZE)
                        .appendEncodedPath(POSTER_PATH)
                        .build();

                Log.v(LOG_TAG, builtUri.toString());

                Picasso.with(super.getContext()).load(builtUri).into(posterView);

                // Set details (date, avgScore)
                String movieDate = new StringBuilder("Release Date:\n").append(mMovie.date).toString();
                ((TextView) rootView.findViewById(R.id.fragment_movie_info_date))
                        .setText(movieDate);
                ((TextView) rootView.findViewById(R.id.fragment_movie_info_avg_score))
                        .setText("User Score:\n" + mMovie.avgScore);

                // Set overview
                ((TextView) rootView.findViewById(R.id.fragment_movie_info_overview))
                        .setText(mMovie.overview);
            }
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_movie_info, menu);

            MenuItem item = menu.findItem(R.id.action_share);

            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            } else {
                Log.d(LOG_TAG, "Share action provider is null");
            }
        }

        private Intent createShareMovieIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.title);
            return shareIntent;
        }
    }
}
