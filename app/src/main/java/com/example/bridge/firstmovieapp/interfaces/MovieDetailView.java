package com.example.bridge.firstmovieapp.interfaces;

import com.example.bridge.firstmovieapp.entities.Movie;

/**
 * Created by bridge on 18/10/2016.
 */

public interface MovieDetailView{

    String ARG_MOVIE = "movie";
    String ARG_MOVIE_ID = "movie_id";

    void showMovie (Movie movie);
}
