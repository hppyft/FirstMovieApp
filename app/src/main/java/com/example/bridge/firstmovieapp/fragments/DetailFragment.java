package com.example.bridge.firstmovieapp.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.adapters.ReviewListAdapter;
import com.example.bridge.firstmovieapp.adapters.TrailerListAdapter;
import com.example.bridge.firstmovieapp.asynctasks.ReviewAsyncTask;
import com.example.bridge.firstmovieapp.asynctasks.TrailersAsyncTask;
import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.interfaces.MovieDetailView;
import com.example.bridge.firstmovieapp.interfaces.OnMovieSelectedListener;
import com.squareup.picasso.Picasso;

/**
 * Created by bridge on 18/10/2016.
 */

public class DetailFragment extends Fragment implements MovieDetailView, OnMovieSelectedListener {

    public Movie mMovie;
    public RecyclerView mTrailerRecyclerView;
    public TrailerListAdapter mTrailerRecyclerAdapter;
    public RecyclerView mReviewRecyclerView;
    public ReviewListAdapter mReviewRecyclerAdapter;

    public TextView favoriteLabel;
    public CheckBox favoriteCheckBox;
    public TextView title;
    public ImageView poster;
    public TextView overviewLabel;
    public TextView overview;
    public TextView rateLabel;
    public TextView rate;
    public TextView releaseLabel;
    public TextView release;
    public TextView trailerLabel;
    public TextView reviewLavel;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        this.favoriteLabel = (TextView) rootView.findViewById(R.id.detail_favorite_label);
        this.favoriteCheckBox = (CheckBox) rootView.findViewById(R.id.favorite_check_box);
        this.title = (TextView) rootView.findViewById(R.id.detail_title);
//        title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "primeLight.otf"));
        this.poster = ((ImageView) rootView.findViewById(R.id.detail_poster));
        this.overviewLabel = (TextView)rootView.findViewById(R.id.detail_overview_label);
        this.overview = (TextView) rootView.findViewById(R.id.detail_overview);
        this.rateLabel = (TextView) rootView.findViewById(R.id.detail_rate_label);
        this.rate = (TextView) rootView.findViewById(R.id.detail_rate);
        this.releaseLabel = (TextView) rootView.findViewById(R.id.detail_release_label);
        this.release = (TextView) rootView.findViewById(R.id.detail_release);
        this.trailerLabel = (TextView) rootView.findViewById(R.id.detail_trailer_label);

        mTrailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_trailer_list);
        mTrailerRecyclerAdapter = new TrailerListAdapter(getActivity());
        mTrailerRecyclerView.setAdapter(mTrailerRecyclerAdapter);

        this.reviewLavel = (TextView) rootView.findViewById(R.id.detail_review_label);

        mReviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_review_list);
        mReviewRecyclerAdapter = new ReviewListAdapter (getActivity());
        mReviewRecyclerView.setAdapter(mReviewRecyclerAdapter);

        this.favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setFavorite(mMovie, true);
                }
                else{
                    setFavorite(mMovie, false);
                }
            }
        });

        mMovie = getActivity().getIntent().getParcelableExtra(MovieDetailView.ARG_MOVIE);

        showMovie(mMovie);

        return rootView;
    }

    public void setFavorite(Movie movie, boolean favorite){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MoviesEntry.COLUMN_FAVORITE, favorite);
        getContext().getContentResolver().update(MovieContract.MoviesEntry.CONTENT_URI,
                movieValues,
                MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" = ?",
                new String[]{movie.id});
        getContext().getContentResolver().notifyChange(MovieContract.MoviesEntry.CONTENT_URI, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        startUpdaters();
    }

    @Override
    public void showMovie(Movie movie) {
        this.favoriteLabel.setText(R.string.favorite_label);
        this.title.setText(movie.original_title);
        Picasso.with(getActivity().getBaseContext()).load("http://image.tmdb.org/t/p/w185"+movie.poster_path).into(this.poster);
        this.overviewLabel.setText(R.string.overview_label);
        this.overview.setText(movie.overview);
        this.rateLabel.setText(R.string.rate_label);
        this.rate.setText(""+movie.vote_average);
        this.releaseLabel.setText(R.string.release_label);
        this.release.setText(movie.release_date);
        this.trailerLabel.setText(R.string.trailer_label);
        this.reviewLavel.setText(R.string.detail_review_label);
        if (movie.favorite==1){
            this.favoriteCheckBox.setChecked(true);
        }
        else if (movie.favorite==0){
            this.favoriteCheckBox.setChecked(false);
        }
        else{
            System.out.println("O VALOR DE FAVORITE EH"+movie.favorite);
        }
    }

    public void startUpdaters(){
        TrailersAsyncTask trailersAsyncTask = new TrailersAsyncTask(this, getContext(), mMovie);
        trailersAsyncTask.execute();
        ReviewAsyncTask reviewAsyncTask = new ReviewAsyncTask(this, getContext(), mMovie);
        reviewAsyncTask.execute();
    }

    @Override
    public void updateTrailerList(TrailersAsyncTask trailersAsyncTask) {
        mTrailerRecyclerAdapter.setTrailerList(trailersAsyncTask.mTrailerList.results);
    }

    @Override
    public void updateReviewList(ReviewAsyncTask reviewAsyncTask){
        mReviewRecyclerAdapter.setReviewList(reviewAsyncTask.mReviewsList.results);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
