package com.example.bridge.firstmovieapp.fragments;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.adapters.ResultListAdapter;
import com.example.bridge.firstmovieapp.asynctasks.MoviesFromQueryAsyncTask;
import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.interfaces.OnMoviesFound;


public class SearchFragment extends Fragment implements OnMoviesFound {

    private RecyclerView mRecyclerView;
    public ResultListAdapter mRecyclerCursorAdapter;
    private TextView moviesFound;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_search_results);
        mRecyclerCursorAdapter = new ResultListAdapter(getActivity());
        mRecyclerView.setAdapter(mRecyclerCursorAdapter);
        this.moviesFound = (TextView) rootView.findViewById(R.id.movies_found_label);
        Bundle args = getArguments();
        if(args!=null && args.containsKey(SearchManager.QUERY)) {
            String query = args.getString(SearchManager.QUERY);
            moviesFound.setText(getString(R.string.movies_found_with_query) + " " + query);
            MoviesFromQueryAsyncTask asyncTask = new MoviesFromQueryAsyncTask(this, getContext());
            asyncTask.execute(query);
        }
        else{
            moviesFound.setText(getString(R.string.movies_found));
        }
        return rootView;
    }

    @Override
    public void showMoviesFound(MovieList movieList) {
        if (null!=movieList){
            mRecyclerCursorAdapter.setMovieList(movieList);
        }
    }
}
