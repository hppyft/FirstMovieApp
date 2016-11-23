package com.example.bridge.firstmovieapp.asynctasks;

import android.os.AsyncTask;

import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.entities.Utility;
import com.example.bridge.firstmovieapp.interfaces.IFetchDataFromMovieDB;
import com.example.bridge.firstmovieapp.interfaces.OnMoviesFound;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesFromQueryAsyncTask extends AsyncTask<String, Void, MovieList> {

    public OnMoviesFound mOnMoviesFound;
    private final String LOG_TAG = MoviesFromQueryAsyncTask.class.getSimpleName();

    public MoviesFromQueryAsyncTask(OnMoviesFound onMoviesFound) {
        super();
        mOnMoviesFound = onMoviesFound;
    }

    @Override
    protected MovieList doInBackground(String... params) {
        if(Utility.isInternetAvailable()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            String query = params[0];
            IFetchDataFromMovieDB fetchDataFromMovieDB = retrofit.create(IFetchDataFromMovieDB.class);
            String language = Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry();
            Call<MovieList> moviesFoundCall = fetchDataFromMovieDB.getMoviesFromQuery(language, query);
            try {
                MovieList moviesFound = moviesFoundCall.execute().body();
                return moviesFound;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(MovieList movieList) {
        mOnMoviesFound.showMoviesFound(movieList);
    }
}
