package com.example.ben.movieapp;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

class MovieData implements Parcelable{

    String title;
    String overview;
    String posterPath;
    String avgScore;
    String date;
    String[] infoArr;

    public MovieData(String[] MovieDataArr){
        this.title = MovieDataArr[0];
        this.overview = MovieDataArr[1];
        this.posterPath = MovieDataArr[2];
        this.avgScore = MovieDataArr[3];
        this.date = MovieDataArr[4];
        this.infoArr = MovieDataArr;
    }

    private MovieData(Parcel in) {
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        avgScore = in.readString();
        date = in.readString();
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(posterPath);
        parcel.writeString(avgScore);
        parcel.writeString(date);
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
