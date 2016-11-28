package com.example.bridge.firstmovieapp.syncservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.entities.Utility;
import com.example.bridge.firstmovieapp.interfaces.IFetchDataFromMovieDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE_ID;

public class LooseMovieService extends IntentService {

    private final String LOG_TAG = LooseMovieService.class.getSimpleName();

    public LooseMovieService() {
        super("LooseMovieService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(Utility.isInternetAvailable(getBaseContext())) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            String movieId = intent.getStringExtra(ARG_MOVIE_ID);
            IFetchDataFromMovieDB fetchDataFromMovieDB = retrofit.create(IFetchDataFromMovieDB.class);
            String language = Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry();
            Call<Movie> movieCall = fetchDataFromMovieDB.getLooseMovie(movieId, language);
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
//                Intent LooseMovieAddedIntent = new Intent(getBaseContext(), DetailActivity.class);
//                LooseMovieAddedIntent.putExtra(ARG_MOVIE, movie);
//                LooseMovieAddedIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                startActivity(LooseMovieAddedIntent);
            }
            else {
                // create a handler to post messages to the main thread
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.movie_id_not_found), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_message), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}