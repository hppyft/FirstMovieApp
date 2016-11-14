package com.example.bridge.firstmovieapp.interfaces;

import com.example.bridge.firstmovieapp.entities.ReviewList;
import com.example.bridge.firstmovieapp.entities.TrailerList;

/**
 * Created by bridge on 31/10/2016.
 */

public interface OnMovieSelectedListener {

    void updateTrailerList (TrailerList trailerList);

    void updateReviewList (ReviewList reviewList);
}
