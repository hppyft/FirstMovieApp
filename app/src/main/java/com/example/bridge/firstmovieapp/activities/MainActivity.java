package com.example.bridge.firstmovieapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.fragments.DetailFragment;
import com.example.bridge.firstmovieapp.fragments.MovieListFragment;
import com.example.bridge.firstmovieapp.interfaces.CallbackMovieClicked;
import com.example.bridge.firstmovieapp.interfaces.MovieDetailView;
import com.example.bridge.firstmovieapp.syncservice.SyncAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements CallbackMovieClicked {

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_detail_container) != null){
            mTwoPane = true;
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else{
            mTwoPane=false;
        }

        MovieListFragment movieListFragment = ((MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie_list));

        SyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onItemSelected(Movie movie){
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailView.ARG_MOVIE, movie);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(MovieDetailView.ARG_MOVIE, movie);
            this.startActivity(intent);
        }
    }

    //TEST
}
