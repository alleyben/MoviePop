package com.example.ben.movieapp;


import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ben.movieapp.data.DataContract;

import java.util.Date;

public class TrailerData implements Parcelable{

    String title;
    String trailerPath;
    String[] infoArr;

    public TrailerData(String[] TrailerDataArr){
        this.title = TrailerDataArr[0];
        this.trailerPath = TrailerDataArr[1];
        this.infoArr = TrailerDataArr;
    }

    private TrailerData(Parcel in) {
        title = in.readString();
        trailerPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {return title;}

//    public ContentValues getMovieDataCV() {
//
//        ContentValues cv = new ContentValues();
//
//        cv.put(DataContract.FavoritesEntry.COLUMN_TITLE, title);
//        cv.put(DataContract.FavoritesEntry.COLUMN_OVERVIEW, overview);
//        cv.put(DataContract.FavoritesEntry.COLUMN_POSTER_URL, posterPath);
//        cv.put(DataContract.FavoritesEntry.COLUMN_SCORE, avgScore);
//        cv.put(DataContract.FavoritesEntry.COLUMN_DATE, date);
//        cv.put(DataContract.FavoritesEntry.COLUMN_MOVIE_ID, movieId);
//
//        return cv;
//    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(trailerPath);
    }

    public static final Parcelable.Creator<TrailerData> CREATOR = new Parcelable.Creator<TrailerData>() {
        @Override
        public TrailerData createFromParcel(Parcel parcel) {
            return new TrailerData(parcel);
        }

        @Override
        public TrailerData[] newArray(int i) {
            return new TrailerData[i];
        }
    };
}
