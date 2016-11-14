package com.example.bridge.firstmovieapp.syncservice;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.entities.Review;
import com.example.bridge.firstmovieapp.entities.ReviewList;
import com.example.bridge.firstmovieapp.entities.Trailer;
import com.example.bridge.firstmovieapp.entities.TrailerList;
import com.example.bridge.firstmovieapp.interfaces.IFetchDataFromMovieDB;
import com.example.bridge.firstmovieapp.interfaces.OnMovieSelectedListener;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.bridge.firstmovieapp.interfaces.MovieDetailView.ARG_MOVIE;

public class ReviewAndTrailerService extends IntentService {

    private OnMovieSelectedListener mListener;

    public TrailerList mTrailerList;
    public ReviewList mReviewList;
    private Movie mMovie;

    public ReviewAndTrailerService(String name) {
        super(name);
    }

    public ReviewAndTrailerService(String name, OnMovieSelectedListener listener) {
        super(name);
        mListener = listener;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mMovie = intent.getParcelableExtra(ARG_MOVIE);
        IFetchDataFromMovieDB fetchDataFromMovieDB = retrofit.create(IFetchDataFromMovieDB.class);
        Call<TrailerList> trailerCall = fetchDataFromMovieDB.getTrailerKey(mMovie.id);
        try {
            mTrailerList = trailerCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<ReviewList> reviewCall = fetchDataFromMovieDB.getReviews(mMovie.id);
        try {
            mReviewList = reviewCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * HERE WE NEED TO NOTIFY THAT THE TRAILER+REVIEWS ARE READY
         */

        if (null!=mReviewList) {
            updateReviewsDB(mReviewList);
            mListener.updateReviewList(mReviewList);
        }
        if (null!=mTrailerList) {
            updateTrailersDB(mTrailerList);
            mListener.updateTrailerList(mTrailerList);
        }
    }

    private void updateReviewsDB (ReviewList reviewList){
        for(Review iterator: reviewList.results){
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_ID, iterator.id);
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_ID, mMovie.id);
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, iterator.author);
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_CONTENT, iterator.content);

            Cursor cursor = getBaseContext().getContentResolver().query(MovieContract.ReviewsEntry.CONTENT_URI,
                    null,
                    MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" = ? " +
                            MovieContract.ReviewsEntry.COLUMN_REVIEW_ID + " = ? ",
                    new String[]{mMovie.id, iterator.id},
                    null);
            if (cursor.getCount()!=0){
                getBaseContext().getContentResolver().update(MovieContract.ReviewsEntry.CONTENT_URI,
                        reviewValues,
                        MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" = ? " +
                                MovieContract.ReviewsEntry.COLUMN_REVIEW_ID + " = ? ",
                        new String[]{mMovie.id, iterator.id});
            }
            else{
                getBaseContext().getContentResolver().insert(MovieContract.ReviewsEntry.CONTENT_URI, reviewValues);
            }
            cursor.close();
        }
    }

    private void updateTrailersDB (TrailerList trailerList){
        for(Trailer iterator: trailerList.results){
            ContentValues trailersValues = new ContentValues();
            trailersValues.put(MovieContract.TrailersEntry.COLUMN_TRAILER_ID, iterator.id);
            trailersValues.put(MovieContract.TrailersEntry.COLUMN_MOVIE_ID, mMovie.id);
            trailersValues.put(MovieContract.TrailersEntry.COLUMN_TRAILER_PATH, iterator.key);

            Cursor cursor = getBaseContext().getContentResolver().query(MovieContract.TrailersEntry.CONTENT_URI,
                    null,
                    MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" = ? " +
                            MovieContract.TrailersEntry.COLUMN_TRAILER_ID + " = ? ",
                    new String[]{mMovie.id, iterator.id},
                    null);
            if (cursor.getCount()!=0){
                getBaseContext().getContentResolver().update(MovieContract.TrailersEntry.CONTENT_URI,
                        trailersValues,
                        MovieContract.TrailersEntry.COLUMN_MOVIE_ID+" = ? " +
                                MovieContract.TrailersEntry.COLUMN_TRAILER_ID + " = ? ",
                        new String[]{mMovie.id, iterator.id});
            }
            else{
                getBaseContext().getContentResolver().insert(MovieContract.TrailersEntry.CONTENT_URI, trailersValues);
            }
            cursor.close();
        }
    }
}
