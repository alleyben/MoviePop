package com.alley.ben.movieapp.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class DataProvider extends ContentProvider{

    private static final String LOG_TAG = DataProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mOpenHelper;

    static final int FAVORITES = 100;
    static final int FAVORITE_BY_ID = 101;
    static final int TRAILERS = 200;
    static final int TRAILERS_BY_ID = 201;
    static final int RECOMMENDATIONS = 300;
    static final int RECOMMENDATIONS_BY_ID = 301;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    private static final SQLiteQueryBuilder sTrailersQueryBuilder;
    private static final SQLiteQueryBuilder sRecommendationsQueryBuilder;


    static{
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(DataContract.FavoritesContract.TABLE_NAME);
    }

    static{
        sTrailersQueryBuilder = new SQLiteQueryBuilder();
        sTrailersQueryBuilder.setTables(DataContract.TrailersContract.TABLE_NAME);
    }

    static {
        sRecommendationsQueryBuilder = new SQLiteQueryBuilder();
        sRecommendationsQueryBuilder.setTables(DataContract.RecommendationsContract.TABLE_NAME);
    }

    public static final String sMovieSelection = DataContract.FavoritesContract.TABLE_NAME +
            "." + DataContract.FavoritesContract.COLUMN_MOVIE_ID + " = ? ";

    public static final String sTrailersSelection = DataContract.TrailersContract.TABLE_NAME +
            "." + DataContract.TrailersContract.COLUMN_MOVIE_ID + " = ? ";

    public static final String sRecommendationsSelection = DataContract.RecommendationsContract.TABLE_NAME +
            "." + DataContract.RecommendationsContract.COLUMN_MOVIE_ID + " = ? ";

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
                null,
                null,
                null,
                null,
                null);
    }

    private Cursor getTrailers(String[] projection, String[] selectionArgs) {
        return sTrailersQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTrailersSelection,
                selectionArgs,
                null,
                null,
                null);
    }

    private Cursor getRecommendations(String[] projection, String[] selectionArgs) {
        return sRecommendationsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sRecommendationsSelection,
                selectionArgs,
                null,
                null,
                null);
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DataContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, DataContract.PATH_FAVORITES + "/*", FAVORITE_BY_ID);
        // DataContract.PATH_FAVORITES + "/*"
        // com.example.ben.moviepop.app/favorites/*

        matcher.addURI(authority, DataContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, DataContract.PATH_TRAILERS + "/*", TRAILERS_BY_ID);

        matcher.addURI(authority, DataContract.PATH_RECOMMENDATIONS, RECOMMENDATIONS);
        matcher.addURI(authority, DataContract.PATH_RECOMMENDATIONS + "/*", RECOMMENDATIONS_BY_ID);

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
        Log.d(LOG_TAG, "URI TO MATCH: " + uri.toString() + "\n");


        switch (sUriMatcher.match(uri)) {

            case FAVORITE_BY_ID:
                String movieId = DataContract.FavoritesContract.getMovieId(uri);
                returnCursor = getMovieByID(uri, projection, new String[] {movieId});
                break;

            case FAVORITES:
                returnCursor = getAllMovies();
                Log.d(LOG_TAG, "URI MATCHER MATCHES FAVS");
                break;

            case TRAILERS_BY_ID:
                movieId = DataContract.TrailersContract.getMovieId(uri);
                returnCursor = getTrailers(projection, new String[] {movieId});
                Log.d(LOG_TAG, "URI MATCHER MATCHES TRAILERS");
                break;

            case RECOMMENDATIONS_BY_ID:
                movieId = DataContract.RecommendationsContract.getMovieId(uri);
                returnCursor = getRecommendations(projection, new String[] {movieId});
                Log.d(LOG_TAG, "URI MATCHER MATCHES RECS");
                break;

            default:
                Log.d(LOG_TAG, "URI MATCHER DOES NOT MATCH");
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
            case FAVORITES:
                return DataContract.FavoritesContract.CONTENT_TYPE;
            case TRAILERS:
                return DataContract.TrailersContract.CONTENT_TYPE;
            case RECOMMENDATIONS:
                return DataContract.RecommendationsContract.CONTENT_TYPE;
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
            case FAVORITES:
                long _id = db.insert(DataContract.FavoritesContract.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DataContract.FavoritesContract.buildInsertMovieUri(_id);
                    Log.d(LOG_TAG, "Inserted movie URI: " + returnUri.toString() + "\n" +
                            "VALUES: " + values.toString());
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // TRAILERS and RECOMMENDATIONS are added to database via bulkInsert()
//            case TRAILERS:
//                _id = db.insert(DataContract.TrailersContract.TABLE_NAME, null, values);
//                if (_id > 0) {
//                    returnUri = DataContract.TrailersContract.buildInsertTrailersUri(_id);
//                    Log.d(LOG_TAG, "Inserted TRAILERS uri: " + returnUri.toString() + "\n" +
//                            "VALUES: " + values.toString());
//                } else {
//                    throw new SQLException("Failed to insert row into " + uri);
//                }
//                break;
//            case RECOMMENDATIONS:
//                _id = db.insert(DataContract.RecommendationsContract.TABLE_NAME, null, values);
//                if (_id > 0) {
//                    returnUri = DataContract.RecommendationsContract.buildInsertRecommendationsUri(_id);
//                    Log.d(LOG_TAG, "Inserted RECOMMENDATIONS uri: " + returnUri.toString() + "\n" +
//                            "VALUES: " + values.toString());
//                } else {
//                    throw new SQLException("Failed to insert row into " + uri);
//                }
//                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        long _id;

        switch (match) {
            case TRAILERS:
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        _id = db.insert(DataContract.TrailersContract.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                            Log.d(LOG_TAG, "Successful TRAILER insertion:\n" + value.toString());
                        } else {
                            Log.e(LOG_TAG, "Error inserting:\n" + value.toString());
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case RECOMMENDATIONS:
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        _id = db.insert(DataContract.RecommendationsContract.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                            Log.d(LOG_TAG, "Successful RECOMMENDATIONS insertion:\n" + value.toString());
                        } else {
                            Log.e(LOG_TAG, "Error inserting:\n" + value.toString());
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (selection == null) selection = "1";
        switch (match) {
            case FAVORITES:
                rowsDeleted = db.delete(
                        DataContract.FavoritesContract.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(
                        DataContract.TrailersContract.TABLE_NAME, selection, selectionArgs);
                break;
            case RECOMMENDATIONS:
                rowsDeleted = db.delete(
                        DataContract.RecommendationsContract.TABLE_NAME, selection, selectionArgs);
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
            case FAVORITES:
                rowsUpdated = db.update(DataContract.FavoritesContract.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsUpdated = db.update(DataContract.TrailersContract.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case RECOMMENDATIONS:
                rowsUpdated = db.update(DataContract.RecommendationsContract.TABLE_NAME,
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
}
