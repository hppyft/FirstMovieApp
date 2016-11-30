package com.example.bridge.firstmovieapp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.adapters.MovieListCursorAdapter;
import com.example.bridge.firstmovieapp.interfaces.IMovieListPres;
import com.example.bridge.firstmovieapp.interfaces.IMovieListView;
import com.example.bridge.firstmovieapp.presenters.MovieListPresenter;

public class MovieListFragment extends Fragment implements IMovieListView {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private IMovieListPres mPresenter;
    private RecyclerView mRecyclerView;
    private MovieListCursorAdapter mRecyclerCursorAdapter;
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_POSTER_PATH = 2;
    public static final int COL_VOTE_AVERAGE = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_POPULARITY = 6;
    public static final int COL_FAVORITE = 7;

    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.mPresenter = new MovieListPresenter(this, getContext(), getLoaderManager());
        mPresenter.onCreate();
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
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        mPresenter = null;
    }

    @Override
    public void setMovieCursor(Cursor data) {
        mRecyclerCursorAdapter.setMovieCursor(data);
    }
}
