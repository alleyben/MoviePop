package com.example.ben.movieapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ben.movieapp.database.DataContract.TrailersContract;
import com.example.ben.movieapp.database.DataContract.RecommendationsContract;
import com.example.ben.movieapp.database.DataContract.FavoritesContract;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    static final String DATABASE_NAME = "movies.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME +
//                " (" + MoviesEntry._ID + " INTEGER PRIMARY KEY, " +
//                MoviesEntry.COLUMN_MOVIE_ID + "TEXT UNIQUE NOT NULL " + " );";
        //more columns?
        //title, release date, score, description

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                FavoritesContract.TABLE_NAME + " (" +
                FavoritesContract._ID + " INTEGER PRIMARY KEY, " +
                FavoritesContract.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                FavoritesContract.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesContract.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                FavoritesContract.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoritesContract.COLUMN_SCORE + " TEXT NOT NULL, " +
                FavoritesContract.COLUMN_DATE + " TEXT NOT NULL, " +
                FavoritesContract.COLUMN_RATING + " TEXT NOT NULL, " + //nullable
                FavoritesContract.COLUMN_RUNTIME + " TEXT NOT NULL, " +
                FavoritesContract.COLUMN_TAGLINE + " TEXT NOT NULL, " + //nullable
                FavoritesContract.COLUMN_GENRES + " TEXT NOT NULL, " +
                FavoritesContract.COLUMN_IMDB_ID + " TEXT NOT NULL " +
                " );";
        // TODO:[note] may need to make some columns nullable

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                TrailersContract.TABLE_NAME + " (" +
                TrailersContract._ID + " INTEGER PRIMARY KEY, " +
                TrailersContract.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                TrailersContract.COLUMN_TRAILER_URL + " TEXT NOT NULL, " +
                TrailersContract.COLUMN_TRAILER_TITLE + " TEXT NOT NULL " +
                " )";

    final String SQL_CREATE_RECOMMENDATIONS_TABLE = "CREATE TABLE " +
            RecommendationsContract.TABLE_NAME + " (" +
            RecommendationsContract._ID + " INTEGER PRIMARY KEY, " +
            RecommendationsContract.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
            RecommendationsContract.COLUMN_SIMILAR_MOVIE_ID + " TEXT NOT NULL, " +
            RecommendationsContract.COLUMN_SIMILAR_MOVIE_TITLE + " TEXT NOT NULL, " +
            RecommendationsContract.COLUMN_SIMILAR_MOVIE_POSTER_URL + " TEXT NOT NULL" +
            " )";


        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_RECOMMENDATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailersContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecommendationsContract.TABLE_NAME);

        onCreate(db);
    }
}
