package com.example.bridge.firstmovieapp.interfaces;

import com.example.bridge.firstmovieapp.BuildConfig;
import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.entities.ReviewList;
import com.example.bridge.firstmovieapp.entities.TrailerList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by bridge on 17/10/2016.
 */

public interface IFetchDataFromMovieDB {


    @GET ("movie/{sort}"+"?api_key="+ BuildConfig.THE_MOVIE_DATABASE_API_KEY)
    Call<MovieList> getMovieList(@Path("sort") String sort);

    @GET ("movie/{movie_id}/videos"+"?api_key="+ BuildConfig.THE_MOVIE_DATABASE_API_KEY)
    Call<TrailerList> getTrailerKey(@Path ("movie_id") String id);

    @GET ("movie/{movie_id}/reviews"+"?api_key="+ BuildConfig.THE_MOVIE_DATABASE_API_KEY)
    Call<ReviewList> getReviews(@Path ("movie_id") String id);
}
