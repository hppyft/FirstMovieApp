package com.example.bridge.firstmovieapp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.broadcastreceivers.SettingsChangedBroadcastReceiver;
import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.interfaces.IMovieListPres;
import com.example.bridge.firstmovieapp.interfaces.IMovieListView;
import com.example.bridge.firstmovieapp.interfaces.OnSettingsChanged;
import com.example.bridge.firstmovieapp.syncservice.SyncAdapter;

public class MovieListPresenter implements IMovieListPres, LoaderManager.LoaderCallbacks<Cursor>, OnSettingsChanged {

    private IMovieListView mView;
    private Context mContext;
    private LoaderManager mLoaderManager;
    private SettingsChangedBroadcastReceiver settingsChangedBroadcastReceiver;
    private static final int MOVIE_LIST_LOADER = 0;
    public static final String[] MOVIE_LIST_COLUMNS = {
            MovieContract.MoviesEntry.TABLE_NAME + "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
            MovieContract.MoviesEntry.COLUMN_TITLE,
            MovieContract.MoviesEntry.COLUMN_POSTER_PATH,
            MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MoviesEntry.COLUMN_OVERVIEW,
            MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_POPULARITY,
            MovieContract.MoviesEntry.COLUMN_FAVORITE};

    public MovieListPresenter(IMovieListView view, Context context, LoaderManager loaderManager) {
        this.mView = view;
        this.mContext = context;
        this.mLoaderManager = loaderManager;
    }

    @Override
    public void onCreate(){
        mLoaderManager.initLoader(MOVIE_LIST_LOADER, null, this);
        settingsChangedBroadcastReceiver = new SettingsChangedBroadcastReceiver(this);
        settingsChangedBroadcastReceiver.register(mContext);
        SyncAdapter.syncImmediately(mContext);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sort = prefs.getString(mContext.getString(R.string.pref_sort_key),
                mContext.getString(R.string.pref_sort_popular));
        String sortOrder = MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " DESC";
        String selection = null;
        String[] selectionArgs = null;
        switch (sort) {
            case "popular": {
                sortOrder = MovieContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
                break;
            }
            case "top_rated": {
                sortOrder = MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
                break;
            }
            case "favorite": {
                selection = MovieContract.MoviesEntry.COLUMN_FAVORITE + " = ?";
                selectionArgs = new String[]{"1"};
                break;
            }
            default:
                sortOrder = MovieContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        }
        return new CursorLoader(mContext,
                MovieContract.MoviesEntry.CONTENT_URI,
                MOVIE_LIST_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mView.setMovieCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mView.setMovieCursor(null);
    }

    @Override
    public void resetMovieList() {
        mLoaderManager.restartLoader(MOVIE_LIST_LOADER, null, this);
    }

    @Override
    public void onDestroy() {
        if (settingsChangedBroadcastReceiver.registered == true) {
            settingsChangedBroadcastReceiver.unregister(mContext);
        }
        mView = null;
    }
}
