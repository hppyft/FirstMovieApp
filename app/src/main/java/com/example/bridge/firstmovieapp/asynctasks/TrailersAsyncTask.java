package com.example.bridge.firstmovieapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.bridge.firstmovieapp.interfaces.IFetchDataFromMovieDB;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.interfaces.OnMovieSelectedListener;
import com.example.bridge.firstmovieapp.entities.TrailerList;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrailersAsyncTask extends AsyncTask<Void, Void, TrailerList> {
    public TrailerList mTrailerList;
    public Movie mMovie;
    public OnMovieSelectedListener mFragmentInterface;
    public Context mContext;

    public TrailersAsyncTask(OnMovieSelectedListener fragmentInterface, Context context, Movie movie){
        this.mFragmentInterface = fragmentInterface;
        this.mContext = context;
        this.mMovie = movie;
    }

    @Override
    protected TrailerList doInBackground(Void... params) {
        try {
            if(mMovie!=null) {
                return getTrailerFromJson(mMovie.id);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute (TrailerList results){
        mFragmentInterface.updateTrailerList(results);
    }

    private TrailerList getTrailerFromJson(String id) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IFetchDataFromMovieDB fetchDataFromMovieDB = retrofit.create(IFetchDataFromMovieDB.class);
        Call<TrailerList> trailerCall = fetchDataFromMovieDB.getTrailerKey(id);
        mTrailerList = trailerCall.execute().body();
        return mTrailerList;
    }
}
