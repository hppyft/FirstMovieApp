package com.example.bridge.firstmovieapp.interfaces;

import com.example.bridge.firstmovieapp.BuildConfig;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.entities.ReviewList;
import com.example.bridge.firstmovieapp.entities.TrailerList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface IFetchDataFromMovieDB {


    @GET ("movie/{sort}"+"?api_key=" + BuildConfig.THE_MOVIE_DATABASE_API_KEY + "&language")
    Call<MovieList> getMovieList(@Path("sort") String sort, @Query("language") String language);

    @GET ("movie/{movie_id}/videos"+"?api_key=" + BuildConfig.THE_MOVIE_DATABASE_API_KEY + "&language")
    Call<TrailerList> getTrailerKey(@Path ("movie_id") String id, @Query("language") String language);

    @GET ("movie/{movie_id}/reviews"+"?api_key=" + BuildConfig.THE_MOVIE_DATABASE_API_KEY + "&language")
    Call<ReviewList> getReviews(@Path ("movie_id") String id, @Query("language") String language);

    @GET ("movie/{movie_id}"+"?api_key=" + BuildConfig.THE_MOVIE_DATABASE_API_KEY + "&language")
    Call<Movie> getLooseMovie(@Path("movie_id") String id, @Query("language") String language);

    @GET ("/search/movie"+"?api_key=" + BuildConfig.THE_MOVIE_DATABASE_API_KEY + "&language" + "&query")
    Call<MovieList> getMoviesFromQuery(@Query("language") String language, @Query("query") String query);
}
