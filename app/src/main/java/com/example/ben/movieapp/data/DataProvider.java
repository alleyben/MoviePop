package com.example.ben.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DataProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mOpenHelper;

    static final int ALL_MOVIES = 100;
    static final int MOVIE_BY_ID = 101;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;

    static{
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(DataContract.FavoritesEntry.TABLE_NAME);
    }

    public static final String sMovieSelection = DataContract.FavoritesEntry.TABLE_NAME +
            "." + DataContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMovieByID(Uri uri, String[] projection, String[] selectionArgs) {

        // args descriptions for
        // SQLiteOpenHelper.query(
        // database to be queried,
        // projection: columns to be returned,
        // selection: rows to be returned,
        // selectionArgs: replacement for "?"'s in order
        // groupBy:
        // having:
        // sortOrder:
        // )
        // https://developer.android.com/reference/android/database/sqlite/SQLiteQueryBuilder.html#query(android.database.sqlite.SQLiteDatabase, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String)

        // movie id will be selectionArgs[0]

        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieSelection,
                selectionArgs,
                null,
                null,
                null);
    }

    private Cursor getAllMovies() {
        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                null,
                null,//sMovieSelection?
                null,
                null,
                null,
                null);
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DataContract.PATH_FAVORITES, ALL_MOVIES);
        matcher.addURI(authority, DataContract.PATH_FAVORITES, MOVIE_BY_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIE_BY_ID:
                returnCursor = getMovieByID(uri, projection, selectionArgs);
                break;

            case ALL_MOVIES:
                returnCursor = getAllMovies();
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_BY_ID:
                return DataContract.FavoritesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_BY_ID: {
                long _id = db.insert(DataContract.FavoritesEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DataContract.FavoritesEntry.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (selection == null) selection = "1";
        switch (match) {
            case MOVIE_BY_ID:
                rowsDeleted = db.delete(
                        DataContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE_BY_ID:
                rowsUpdated = db.update(DataContract.FavoritesEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    public boolean isFavorite(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_BY_ID:
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return false;
    }
}
