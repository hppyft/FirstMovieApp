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
import android.util.Log;
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

    private static final String INITIAL_POSITION = "initial_position";
    private RecyclerView mRecyclerView;
    public MovieListCursorAdapter mRecyclerCursorAdapter;
    public SettingsChangedBroadcastReceiver settingsChangedBroadcastReceiver;
    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private int mLastFirstVisiblePosition;
    private int mTopView;
    private int currentVisiblePosition;

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
        Log.d(LOG_TAG, "onCreate MovieList CALLED");
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LIST_LOADER, null, this);
        settingsChangedBroadcastReceiver.register(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart MovieList CALLED");
        super.onStart();
        SyncAdapter.syncImmediately(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(LOG_TAG, "onCreateOptionsMenu MovieList CALLED");
        inflater.inflate(R.menu.menu_movie_list, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView MovieList CALLED");
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_fragment_main);
        mRecyclerCursorAdapter = new MovieListCursorAdapter(getActivity());
        mRecyclerView.setAdapter(mRecyclerCursorAdapter);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader MovieList CALLED");
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
        Log.d(LOG_TAG, "onLoadFinished MovieList CALLED");
        mRecyclerCursorAdapter.setMovieCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(LOG_TAG, "onLoaderReset MovieList CALLED");
        mRecyclerCursorAdapter.setMovieCursor(null);
    }

    @Override
    public void resetMovieList() {
        Log.d(LOG_TAG, "resetMovieList MovieList CALLED");
        getLoaderManager().restartLoader(MOVIE_LIST_LOADER, null, this);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy MovieList CALLED");
        super.onDestroy();
        if (settingsChangedBroadcastReceiver.registered == true) {
            settingsChangedBroadcastReceiver.unregister(getContext());
        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
////        mLastFirstVisiblePosition= ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
////        View startView = mRecyclerView.getChildAt(0);
////        mTopView = (startView == null) ? 0 : (startView.getTop() - mRecyclerView.getPaddingTop());
//        currentVisiblePosition = 0;
//        currentVisiblePosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
////        if (mLastFirstVisiblePosition!= -1) {
////            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(mLastFirstVisiblePosition, mTopView);
////        }
//        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(currentVisiblePosition);
//        currentVisiblePosition = 0;
//    }

    //    private static final String BUNDLE_RECYCLER_LAYOUT = "MovieListFragment.mRecyclerView.fragment_movie_list";
//
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//
//        if(savedInstanceState != null)
//        {
//            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
//            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
//    }
}
