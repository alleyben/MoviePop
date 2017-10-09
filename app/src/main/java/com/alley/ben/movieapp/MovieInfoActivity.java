package com.example.ben.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MovieInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            if (intent.hasExtra("movieInfoTag")) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.activity_movie_info_container, new MovieInfoFragment())
                        .commit();
            } else {
                Bundle arguments = new Bundle();
                arguments.putParcelable(MovieInfoFavoritesFragment.DETAIL_URI, getIntent().getData());
                MovieInfoFavoritesFragment favoritesFrag = new MovieInfoFavoritesFragment();
                favoritesFrag.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.activity_movie_info_container, favoritesFrag)
                        .commit();
            }
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
}
