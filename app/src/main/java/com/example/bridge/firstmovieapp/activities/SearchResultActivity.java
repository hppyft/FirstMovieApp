package com.example.bridge.firstmovieapp.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.adapters.ResultListAdapter;
import com.example.bridge.firstmovieapp.asynctasks.MoviesFromQueryAsyncTask;
import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.interfaces.CallbackMovieClicked;
import com.example.bridge.firstmovieapp.interfaces.OnMoviesFound;

public class SearchResultActivity extends Activity implements OnMoviesFound, CallbackMovieClicked {

    private final String LOG_TAG = SearchResultActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    public ResultListAdapter mRecyclerCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        handleIntent(getIntent());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search_results);
        mRecyclerCursorAdapter = new ResultListAdapter(this);
        mRecyclerView.setAdapter(mRecyclerCursorAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_result_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            MoviesFromQueryAsyncTask asyncTask = new MoviesFromQueryAsyncTask(this);
            asyncTask.execute(query);
        }
    }

    public void showMoviesFound(MovieList movieList){
        if (null==movieList){
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_message), Toast.LENGTH_LONG).show();
        }
        else {
            mRecyclerCursorAdapter.setMovieList(movieList);
        }
    }

    @Override
    public void onItemSelected(Uri uri){
        Intent intent = new Intent(this, DetailActivity.class);
        intent.setData(uri);
        this.startActivity(intent);
    }
}
