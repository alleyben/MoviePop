package com.example.ben.movieapp.adapters;

import android.database.Cursor;

import com.example.ben.movieapp.MovieInfoFavoritesFragment;

public class RecommendationListItem {

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

    public static RecommendationListItem fromCursor(Cursor cursor) {

        RecommendationListItem recListItem = new RecommendationListItem();
        recListItem.setTitle(cursor.getString(MovieInfoFavoritesFragment.COL_RECOMMENDATIONS_SIMILAR_MOVIE_TITLE));
        recListItem.setImageUrl(cursor.getString(MovieInfoFavoritesFragment.COL_RECOMMENDATIONS_SIMILAR_MOVIE_POSTER_URL));
        return recListItem;
    }
}
