package com.example.bridge.firstmovieapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{

    public String id;
    public String title;
    public String poster_path;
    public String overview;
    public String release_date;
    public float vote_average;
    public float popularity;
    public int favorite;


    public Movie(){
    }

    private Movie(Parcel parcel) {
        this.id = parcel.readString();
        this.title = parcel.readString();
        this.poster_path = parcel.readString();
        this.vote_average = parcel.readFloat();
        this.overview = parcel.readString();
        this.release_date = parcel.readString();
        this.popularity = parcel.readFloat();
        this.favorite = parcel.readInt();
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeFloat(vote_average);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeFloat(popularity);
        dest.writeInt(favorite);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };
}
