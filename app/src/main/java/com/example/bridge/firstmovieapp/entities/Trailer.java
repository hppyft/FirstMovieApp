package com.example.bridge.firstmovieapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    public String key;

    public Trailer(){
    }

    public Trailer (Parcel parcel){
        this.key = parcel.readString();
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {

        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }

    };
}
