package com.example.ben.movieapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {

    public static final String CONTENT_AUTHORITY = "com.example.ben.moviepop.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class WeatherEntry implements BaseColumns {

        //public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
        //        .
    }
}
