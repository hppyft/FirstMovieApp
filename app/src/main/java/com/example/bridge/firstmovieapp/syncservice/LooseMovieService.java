package com.example.bridge.firstmovieapp.syncservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.example.bridge.firstmovieapp.activities.DetailActivity;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.entities.Utility;
import com.example.bridge.firstmovieapp.interfaces.IFetchDataFromMovieDB;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE;
import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE_ID;

public class LooseMovieService extends IntentService {

    public LooseMovieService() {
        super("LooseMovieService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(Utility.isInternetAvailable()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            String movieId = intent.getStringExtra(ARG_MOVIE_ID);
            IFetchDataFromMovieDB fetchDataFromMovieDB = retrofit.create(IFetchDataFromMovieDB.class);
            Call<Movie> movieCall = fetchDataFromMovieDB.getLooseMovie(movieId);
            Movie movie = new Movie();
            try {
                movie = movieCall.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(movie!=null) {
                MovieList movieList = new MovieList();
                movieList.results = new ArrayList<>();
                movieList.results.add(movie);
                SyncAdapter.addMoviesToDB(getBaseContext(), movieList);
                Intent LooseMovieAddedIntent = new Intent(getBaseContext(), DetailActivity.class);
                LooseMovieAddedIntent.putExtra(ARG_MOVIE, movie);
                LooseMovieAddedIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(LooseMovieAddedIntent);
            }
            else {
                // create a handler to post messages to the main thread
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Sorry, but this ID doesn't seem to exist in TheMovieDB database", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        else{
            // create a handler to post messages to the main thread
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "No internet connection! Please, try again later", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
