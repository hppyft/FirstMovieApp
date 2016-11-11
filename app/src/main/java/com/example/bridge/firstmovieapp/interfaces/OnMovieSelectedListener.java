package com.example.bridge.firstmovieapp.interfaces;

import com.example.bridge.firstmovieapp.asynctasks.ReviewAsyncTask;
import com.example.bridge.firstmovieapp.asynctasks.TrailersAsyncTask;

/**
 * Created by bridge on 31/10/2016.
 */

public interface OnMovieSelectedListener {

    void updateTrailerList (TrailersAsyncTask trailersAsyncTask);

    void updateReviewList (ReviewAsyncTask reviewAsyncTask);
}
