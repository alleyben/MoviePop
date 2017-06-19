package com.example.ben.movieapp;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ben.movieapp.data.DataContract;

import java.util.Date;

class MovieData implements Parcelable{

    String title;
    String overview;
    String posterPath;
    String avgScore;
    String date;
    String movieId;
    String[] infoArr;

    public MovieData(String[] MovieDataArr){
        this.title = MovieDataArr[0];
        this.overview = MovieDataArr[1];
        this.posterPath = MovieDataArr[2];
        this.avgScore = MovieDataArr[3];
        this.date = MovieDataArr[4];
        this.movieId = MovieDataArr[5];
        this.infoArr = MovieDataArr;
    }

    private MovieData(Parcel in) {
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        avgScore = in.readString();
        date = in.readString();
        movieId = in.readString();
    }

    public String dateToString() {
        String calDate = "";
        Date df = new Date("yyyy-MM-dd");
        return calDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {return title;}

    public ContentValues getMovieDataCV() {

        ContentValues cv = new ContentValues();

        cv.put(DataContract.FavoritesEntry.COLUMN_TITLE, title);
        cv.put(DataContract.FavoritesEntry.COLUMN_OVERVIEW, overview);
        cv.put(DataContract.FavoritesEntry.COLUMN_POSTER_URL, posterPath);
        cv.put(DataContract.FavoritesEntry.COLUMN_SCORE, avgScore);
        cv.put(DataContract.FavoritesEntry.COLUMN_DATE, date);
        cv.put(DataContract.FavoritesEntry.COLUMN_MOVIE_ID, movieId);

        return cv;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(posterPath);
        parcel.writeString(avgScore);
        parcel.writeString(date);
        parcel.writeString(movieId);
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel parcel) {
            return new MovieData(parcel);
        }

        @Override
        public MovieData[] newArray(int i) {
            return new MovieData[i];
        }
    };
}
