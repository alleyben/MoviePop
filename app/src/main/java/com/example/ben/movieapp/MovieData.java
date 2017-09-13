package com.example.ben.movieapp;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ben.movieapp.database.DataContract;

public class MovieData implements Parcelable{

    String title;
    String movieId;
    String posterPath;
//    String overview;
//    String avgScore;
//    String date;

    public MovieData(String title, String movieId, String posterPath){
        this.title = title;
        this.movieId = movieId;
        this.posterPath = posterPath;
//        this.title = MovieDataArr[0];
//        this.overview = MovieDataArr[1];
//        this.avgScore = MovieDataArr[3];
//        this.date = MovieDataArr[4];
    }

    private MovieData(Parcel in) {
        title = in.readString();
        movieId = in.readString();
        posterPath = in.readString();
//        overview = in.readString();
//        avgScore = in.readString();
//        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

//    public String toString() {return title;}

    public ContentValues getMovieDataCV() {

        ContentValues cv = new ContentValues();
        cv.put(DataContract.FavoritesContract.COLUMN_TITLE, title);
        cv.put(DataContract.FavoritesContract.COLUMN_MOVIE_ID, movieId);
        cv.put(DataContract.FavoritesContract.COLUMN_POSTER_URL, posterPath);
//        cv.put(DataContract.FavoritesContract.COLUMN_OVERVIEW, overview);
//        cv.put(DataContract.FavoritesContract.COLUMN_SCORE, avgScore);
//        cv.put(DataContract.FavoritesContract.COLUMN_DATE, date);

        return cv;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(movieId);
        parcel.writeString(posterPath);
//        parcel.writeString(overview);
//        parcel.writeString(avgScore);
//        parcel.writeString(date);
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
