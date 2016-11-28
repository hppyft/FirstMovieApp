package com.example.bridge.firstmovieapp.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.entities.Utility;
import com.example.bridge.firstmovieapp.fragments.MovieListFragment;
import com.example.bridge.firstmovieapp.interfaces.CallbackMovieClicked;


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
        final Uri uri = getItemUri(position);
        holder.mPosterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CallbackMovieClicked) activity).onItemSelected(uri);
            }
        });
        Utility ut = new Utility(activity);
        if(ut.isConnectionAvailable(activity)) {
//            Picasso.with(activity.getBaseContext()).load("http://image.tmdb.org/t/p/w185" + mCursor.getString(MovieListFragment.COL_POSTER_PATH)).into(holder.mPosterView);
            Glide.with(activity.getBaseContext()).
                    load("http://image.tmdb.org/t/p/w185" +
                            mCursor.getString(MovieListFragment.COL_POSTER_PATH))
                    .fitCenter()
                    .placeholder(R.drawable.blank_poster)
                    .crossFade()
                    .into(holder.mPosterView);
            holder.mTitleView.setVisibility(View.INVISIBLE);
        }
        else{
            holder.mPosterView.setImageResource(R.drawable.blank_poster);
            holder.mTitleView.setVisibility(View.VISIBLE);
            holder.mTitleView.setText(mCursor.getString(MovieListFragment.COL_TITLE));
        }
    }

    private Uri getItemUri(int position) {
        mCursor.moveToPosition(position);
//        Movie movie = new Movie();
//        movie.id = mCursor.getString(MovieListFragment.COL_MOVIE_ID);
//        movie.original_title = mCursor.getString(MovieListFragment.COL_TITLE);
//        movie.poster_path = mCursor.getString(MovieListFragment.COL_POSTER_PATH);
//        movie.vote_average = mCursor.getFloat(MovieListFragment.COL_VOTE_AVERAGE);
//        movie.overview = mCursor.getString(MovieListFragment.COL_OVERVIEW);
//        movie.release_date = mCursor.getString(MovieListFragment.COL_RELEASE_DATE);
//        movie.popularity = mCursor.getFloat(MovieListFragment.COL_POPULARITY);
//        movie.favorite = mCursor.getInt(MovieListFragment.COL_FAVORITE);
        long id = Long.parseLong(mCursor.getString(MovieListFragment.COL_MOVIE_ID));
        Uri uri = MovieContract.MoviesEntry.buildMovieUri(id);
        return uri;
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
        public final TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            mPosterView = (ImageView) view.findViewById(R.id.poster);
            mTitleView = (TextView) view.findViewById(R.id.title);
        }
    }
}
