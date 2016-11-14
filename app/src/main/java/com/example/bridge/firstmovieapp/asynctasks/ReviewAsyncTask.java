package com.example.bridge.firstmovieapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.entities.ReviewList;
import com.example.bridge.firstmovieapp.interfaces.IFetchDataFromMovieDB;
import com.example.bridge.firstmovieapp.interfaces.OnMovieSelectedListener;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewAsyncTask extends AsyncTask<Void, Void, ReviewList> {
    public ReviewList mReviewsList;
    public Movie mMovie;
    public OnMovieSelectedListener mFragmentInterface;
    public Context context;

    public ReviewAsyncTask(OnMovieSelectedListener fragmentInterface, Context context, Movie movie) {
        this.mFragmentInterface = fragmentInterface;
        this.context = context;
        this.mMovie = movie;
    }

    @Override
    protected ReviewList doInBackground(Void... params){
        try{
            if (mMovie!=null) {
                return getReviewFromJson(mMovie.id);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute (ReviewList results){
        mFragmentInterface.updateReviewList(results);
    }

    private ReviewList getReviewFromJson(String id) throws IOException{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IFetchDataFromMovieDB fetchDataFromMovieDB = retrofit.create(IFetchDataFromMovieDB.class);
        Call<ReviewList> reviewCall = fetchDataFromMovieDB.getReviews(id);
        mReviewsList = reviewCall.execute().body();
        return mReviewsList;
    }
}
