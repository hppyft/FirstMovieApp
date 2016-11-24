package com.example.bridge.firstmovieapp.adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.interfaces.CallbackMovieClicked;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {
    public Activity mActivity;
    public MovieList mMovieList;

    public ResultListAdapter(Activity activity){
        this.mActivity = activity;
    }

    @Override
    public ResultListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_content, parent, false);
        return new ResultListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultListAdapter.ViewHolder holder, int position) {
        final Uri uri = getUriByPosition(position);
        holder.mPosterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CallbackMovieClicked) mActivity).onItemSelected(uri);
            }
        });
//        Picasso.with(mActivity.getBaseContext()).load("http://image.tmdb.org/t/p/w185" + mMovieList.results.get(position).poster_path).into(holder.mPosterView);
        Glide.with(mActivity.getBaseContext()).
                load("http://image.tmdb.org/t/p/w185" +
                        mMovieList.results.get(position).poster_path)
                .fitCenter()
                .placeholder(R.drawable.blank_poster)
                .crossFade()
                .into(holder.mPosterView);
    }

    private Uri getUriByPosition(int position){
        long id = Long.parseLong(mMovieList.results.get(position).id);
        Uri uri = MovieContract.MoviesEntry.buildMovieUri(id);
        return uri;
    }

    @Override
    public int getItemCount() {
        if(mMovieList!=null) {
            return mMovieList.results.size();
        }
        return 0;
    }

    public void setMovieList (MovieList movieList){
        this.mMovieList=movieList;
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
