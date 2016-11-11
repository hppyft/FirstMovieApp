package com.example.bridge.firstmovieapp.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.fragments.MovieListFragment;
import com.example.bridge.firstmovieapp.interfaces.CallbackMovieClicked;
import com.squareup.picasso.Picasso;


public class MovieListCursorAdapter extends RecyclerView.Adapter<MovieListCursorAdapter.ViewHolder>{


    public Cursor mCursor;
    public Activity activity;

    public MovieListCursorAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Movie movie = getItem(position);
        holder.mPosterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CallbackMovieClicked) activity).onItemSelected(movie);
            }
        });
        Picasso.with(activity.getBaseContext()).load("http://image.tmdb.org/t/p/w185"+movie.poster_path).into(holder.mPosterView);
    }

    private Movie getItem(int position) {
        mCursor.moveToPosition(position);
        Movie movie = new Movie();
        movie.id = mCursor.getString(MovieListFragment.COL_MOVIE_ID);
        movie.original_title = mCursor.getString(MovieListFragment.COL_TITLE);
        movie.poster_path = mCursor.getString(MovieListFragment.COL_POSTER_PATH);
        movie.vote_average = mCursor.getFloat(MovieListFragment.COL_VOTE_AVERAGE);
        movie.overview = mCursor.getString(MovieListFragment.COL_OVERVIEW);
        movie.release_date = mCursor.getString(MovieListFragment.COL_RELEASE);
        movie.popularity = mCursor.getFloat(MovieListFragment.COL_POPULARITY);
        movie.favorite = mCursor.getInt(MovieListFragment.COL_FAVORITE);
        return movie;
    }

    @Override
    public int getItemCount() {
        if(mCursor!=null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void setMovieCursor(Cursor cursor){
        this.mCursor=cursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mPosterView;

        public ViewHolder(View view) {
            super(view);
            mPosterView = (ImageView) view.findViewById(R.id.poster);
        }
    }
}