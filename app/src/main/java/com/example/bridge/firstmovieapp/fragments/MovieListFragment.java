package com.example.bridge.firstmovieapp.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.adapters.MovieListCursorAdapter;
import com.example.bridge.firstmovieapp.broadcastreceivers.SettingsChangedBroadcastReceiver;
import com.example.bridge.firstmovieapp.data.MovieContract.MoviesEntry;
import com.example.bridge.firstmovieapp.interfaces.OnSettingsChanged;
import com.example.bridge.firstmovieapp.syncservice.SyncAdapter;

public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnSettingsChanged {

    private RecyclerView mRecyclerView;
    public MovieListCursorAdapter mRecyclerCursorAdapter;
    public SettingsChangedBroadcastReceiver settingsChangedBroadcastReceiver;
    private final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private static final int MOVIE_LIST_LOADER = 0;
    public static final String[] MOVIE_LIST_COLUMNS = {
            MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_MOVIE_ID,
            MoviesEntry.COLUMN_TITLE,
            MoviesEntry.COLUMN_POSTER_PATH,
            MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesEntry.COLUMN_OVERVIEW,
            MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesEntry.COLUMN_POPULARITY,
            MoviesEntry.COLUMN_FAVORITE};

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_POSTER_PATH = 2;
    public static final int COL_VOTE_AVERAGE = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_POPULARITY = 6;
    public static final int COL_FAVORITE = 7;

    public MovieListFragment() {
        settingsChangedBroadcastReceiver = new SettingsChangedBroadcastReceiver(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LIST_LOADER, null, this);
        settingsChangedBroadcastReceiver.register(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        SyncAdapter.syncImmediately(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_list_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_fragment_main);
        mRecyclerCursorAdapter = new MovieListCursorAdapter(getActivity());
        mRecyclerView.setAdapter(mRecyclerCursorAdapter);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sort = prefs.getString(getContext().getString(R.string.pref_sort_key),
                getContext().getString(R.string.pref_sort_popular));
        String sortOrder = MoviesEntry.COLUMN_MOVIE_ID + " DESC";
        String selection = null;
        String[] selectionArgs = null;
        switch (sort) {
            case "popular": {
                sortOrder = MoviesEntry.COLUMN_POPULARITY + " DESC";
                break;
            }
            case "top_rated": {
                sortOrder = MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
                break;
            }
            case "favorite": {
                selection = MoviesEntry.COLUMN_FAVORITE + " = ?";
                selectionArgs = new String[]{"1"};
                break;
            }
            default:
                sortOrder = MoviesEntry.COLUMN_POPULARITY + " DESC";
        }
        return new CursorLoader(getActivity(),
                MoviesEntry.CONTENT_URI,
                MOVIE_LIST_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecyclerCursorAdapter.setMovieCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mRecyclerCursorAdapter.setMovieCursor(null);
    }

    @Override
    public void resetMovieList() {
        getLoaderManager().restartLoader(MOVIE_LIST_LOADER, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (settingsChangedBroadcastReceiver.registered == true) {
            settingsChangedBroadcastReceiver.unregister(getContext());
        }
    }
}
