package com.example.ben.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//import com.example.ben.movieapp.data.DataContract.MoviesEntry;
import com.example.ben.movieapp.data.DataContract.FavoritesEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
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

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME +
                " (" + FavoritesEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoritesEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_SCORE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_DATE + " TEXT NOT NULL " +
//                FavoritesEntry.COLUMN_RATING + " TEXT NOT NULL, " +
//                FavoritesEntry.COLUMN_TRAILER_URL + " TEXT NOT NULL " +
                " );";


//        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
