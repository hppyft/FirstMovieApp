package com.example.bridge.firstmovieapp.syncservice;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.entities.Review;
import com.example.bridge.firstmovieapp.entities.ReviewList;
import com.example.bridge.firstmovieapp.entities.Trailer;
import com.example.bridge.firstmovieapp.entities.TrailerList;
import com.example.bridge.firstmovieapp.entities.Utility;
import com.example.bridge.firstmovieapp.interfaces.IFetchDataFromMovieDB;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE;
import static com.example.bridge.firstmovieapp.fragments.DetailFragment.REVIEW_CHANGED;
import static com.example.bridge.firstmovieapp.fragments.DetailFragment.TRAILER_CHANGED;

public class ReviewAndTrailerService extends IntentService {

    public TrailerList mTrailerList;
    public ReviewList mReviewList;
    private Movie mMovie;
    private static String mName = "service";

    public ReviewAndTrailerService(){
        super(mName);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(Utility.isInternetAvailable(getBaseContext())) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mMovie = intent.getParcelableExtra(ARG_MOVIE);
            IFetchDataFromMovieDB fetchDataFromMovieDB = retrofit.create(IFetchDataFromMovieDB.class);
            String language = Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry();
            Call<TrailerList> trailerCall = fetchDataFromMovieDB.getTrailerKey(mMovie.id, language);
            try {
                mTrailerList = trailerCall.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Call<ReviewList> reviewCall = fetchDataFromMovieDB.getReviews(mMovie.id, language);
            try {
                mReviewList = reviewCall.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            if (null != mReviewList && !mReviewList.results.isEmpty()) {
                updateReviewsDB(mReviewList);
                Intent ReviewBroadcastIntent = new Intent();
                ReviewBroadcastIntent.setAction(REVIEW_CHANGED);
                getBaseContext().sendBroadcast(ReviewBroadcastIntent);
//            }
//            if (null != mTrailerList && !mTrailerList.results.isEmpty()) {
                updateTrailersDB(mTrailerList);
                Intent TrailerBroadcastIntent = new Intent();
                TrailerBroadcastIntent.setAction(TRAILER_CHANGED);
                getBaseContext().sendBroadcast(TrailerBroadcastIntent);
//            }
        }
    }

    private void updateReviewsDB (ReviewList reviewList){
        if(null!=reviewList && !reviewList.results.isEmpty()) {
            getBaseContext().getContentResolver().delete(MovieContract.ReviewsEntry.CONTENT_URI,
                    MovieContract.ReviewsEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{mMovie.id});
            for (Review iterator : reviewList.results) {
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_ID, iterator.id);
                reviewValues.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_ID, mMovie.id);
                reviewValues.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, iterator.author);
                reviewValues.put(MovieContract.ReviewsEntry.COLUMN_CONTENT, iterator.content);
//            Cursor cursor = getBaseContext().getContentResolver().query(MovieContract.ReviewsEntry.CONTENT_URI,
//                    null,
//                    MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+"=? " +
//                            "AND " +
//                            MovieContract.ReviewsEntry.COLUMN_REVIEW_ID + "=?",
//                    new String[]{mMovie.id, iterator.id},
//                    null);
//            if (cursor.getCount()!=0){
//                getBaseContext().getContentResolver().update(MovieContract.ReviewsEntry.CONTENT_URI,
//                        reviewValues,
//                        MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+"=? " +
//                                "AND " +
//                                MovieContract.ReviewsEntry.COLUMN_REVIEW_ID + "=?",
//                        new String[]{mMovie.id, iterator.id});
//            }
//            else{
                getBaseContext().getContentResolver().insert(MovieContract.ReviewsEntry.CONTENT_URI, reviewValues);
//            }
//            cursor.close();
            }
        }
    }

    private void updateTrailersDB (TrailerList trailerList){
        if(null!=trailerList && !trailerList.results.isEmpty()) {
            getBaseContext().getContentResolver().delete(MovieContract.TrailersEntry.CONTENT_URI,
                    MovieContract.TrailersEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{mMovie.id});
            for (Trailer iterator : trailerList.results) {
                ContentValues trailersValues = new ContentValues();
                trailersValues.put(MovieContract.TrailersEntry.COLUMN_TRAILER_ID, iterator.id);
                trailersValues.put(MovieContract.TrailersEntry.COLUMN_MOVIE_ID, mMovie.id);
                trailersValues.put(MovieContract.TrailersEntry.COLUMN_TRAILER_PATH, iterator.key);
//            Cursor cursor = getBaseContext().getContentResolver().query(MovieContract.TrailersEntry.CONTENT_URI,
//                    null,
//                    MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+"=? " +
//                            "AND " +
//                            MovieContract.TrailersEntry.COLUMN_TRAILER_ID + "=?",
//                    new String[]{mMovie.id, iterator.id},
//                    null);
//            if (cursor.getCount()!=0){
//                getBaseContext().getContentResolver().update(MovieContract.TrailersEntry.CONTENT_URI,
//                        trailersValues,
//                        MovieContract.TrailersEntry.COLUMN_MOVIE_ID+"=? " +
//                                "AND " +
//                                MovieContract.TrailersEntry.COLUMN_TRAILER_ID + "=?",
//                        new String[]{mMovie.id, iterator.id});
//            }
//            else{
                getBaseContext().getContentResolver().insert(MovieContract.TrailersEntry.CONTENT_URI, trailersValues);
//            }
//            cursor.close();
            }
        }
    }
}
