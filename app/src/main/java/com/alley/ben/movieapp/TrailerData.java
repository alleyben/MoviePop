package com.example.ben.movieapp;


import android.os.Parcel;
import android.os.Parcelable;

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
//        cv.put(DataContract.FavoritesContract.COLUMN_TRAILER_TITLE, title);
//        cv.put(DataContract.FavoritesContract.COLUMN_PREVIEW_URL, trailerPath);
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
