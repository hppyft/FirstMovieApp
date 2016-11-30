package com.example.bridge.firstmovieapp.interfaces;

import android.content.Intent;

import com.example.bridge.firstmovieapp.entities.Movie;

public interface IDetailPres {
    void onCreate();
    void onDestroy();
    void onResume();
    void onPause();
    void setFavorite(Movie movie, boolean favorite);
    Intent createShareIntent();
}
