package com.example.bridge.firstmovieapp.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.asynctasks.MoviesFromQueryAsyncTask;
import com.example.bridge.firstmovieapp.fragments.DetailFragment;
import com.example.bridge.firstmovieapp.fragments.MovieListFragment;
import com.example.bridge.firstmovieapp.fragments.SearchFragment;
import com.example.bridge.firstmovieapp.interfaces.CallbackMovieClicked;
import com.example.bridge.firstmovieapp.syncservice.SyncAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE_URI;

public class MainActivity extends AppCompatActivity implements CallbackMovieClicked {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
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

//        MovieListFragment movieListFragment = ((MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie_list));

        SyncAdapter.initializeSyncAdapter(this);
        handleSearchIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(), MainActivity.class)));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.home){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie_list, new MovieListFragment())
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onItemSelected(Uri uri){
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(ARG_MOVIE_URI, uri);

//            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
//            detailFragment.setUriAndInitLoader(uri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(uri);
            this.startActivity(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSearchIntent(getIntent());
    }

    private void handleSearchIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle args = new Bundle();
            args.putCharSequence(SearchManager.QUERY, query);
            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie_list, searchFragment)
                    .commit();
            MoviesFromQueryAsyncTask asyncTask = new MoviesFromQueryAsyncTask(searchFragment);
            asyncTask.execute(query);
        }
    }
}
