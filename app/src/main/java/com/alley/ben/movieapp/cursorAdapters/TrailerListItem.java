package com.alley.ben.movieapp.cursorAdapters;

import android.database.Cursor;

import com.alley.ben.movieapp.MovieInfoFavoritesFragment;

public class TrailerListItem {
    private String title;
    private String imageUrl;

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle()  {
        return title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public static TrailerListItem fromCursor(Cursor cursor) {

        TrailerListItem trailerListItem = new TrailerListItem();
        trailerListItem.setTitle(cursor.getString(MovieInfoFavoritesFragment.COL_TRAILER_TITLE));
        trailerListItem.setImageUrl(cursor.getString(MovieInfoFavoritesFragment.COL_TRAILER_URL));
        return trailerListItem;
    }
}