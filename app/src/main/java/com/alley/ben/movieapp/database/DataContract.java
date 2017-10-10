package com.alley.ben.movieapp.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {

    public static final String CONTENT_AUTHORITY = "com.example.ben.moviepop.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_RECOMMENDATIONS = "recommendations";


    public static final class FavoritesContract implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";

        // first three items are in the movieData object
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "poster_path";
        // following must be added to database after fetchDetailsTask completes
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_SCORE = "vote_average";
        public static final String COLUMN_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_IMDB_ID = "imdb_id";

        public static Uri buildInsertMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieIdUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
            // "content://com.example.ben.moviepop.app.favorites.<movie_id>"
        }

        public static String getMovieId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TrailersContract implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_URL = "trailer_path";
        public static final String COLUMN_TRAILER_TITLE = "trailer_title";

        public static Uri buildInsertTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieIdUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class RecommendationsContract implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECOMMENDATIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECOMMENDATIONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECOMMENDATIONS;

        public static final String TABLE_NAME = "recommendations";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_SIMILAR_MOVIE_ID = "similar_movie_id";
        public static final String COLUMN_SIMILAR_MOVIE_TITLE = "similar_movie_title";
        public static final String COLUMN_SIMILAR_MOVIE_POSTER_URL = "similar_movie_poster_url";

        public static Uri buildInsertRecommendationsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieIdUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
