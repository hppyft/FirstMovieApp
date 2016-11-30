package com.example.bridge.firstmovieapp.interfaces;

import android.database.Cursor;

import com.example.bridge.firstmovieapp.entities.Movie;

public interface IDetailView {
    void showMovie (Movie movie);
    void updatersStarted();
    void setUpdatedReviewList(Cursor cursor);
    void setUpdatedTrailerList(Cursor cursor);
}
