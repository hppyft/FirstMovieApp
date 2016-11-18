package com.example.bridge.firstmovieapp.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.example.bridge.firstmovieapp.adapters.ReviewListCursorAdapter;
import com.example.bridge.firstmovieapp.adapters.TrailerListCursorAdapter;
import com.example.bridge.firstmovieapp.broadcastreceivers.ReviewListChangedBroadcastReceiver;
import com.example.bridge.firstmovieapp.broadcastreceivers.TrailerListChangedBroadcastReceiver;
import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.entities.Utility;
import com.example.bridge.firstmovieapp.interfaces.OnReviewListChanged;
import com.example.bridge.firstmovieapp.interfaces.OnTrailerListChanged;
import com.example.bridge.firstmovieapp.syncservice.ReviewAndTrailerService;
import com.squareup.picasso.Picasso;

import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE;

/**
 * Created by bridge on 18/10/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnTrailerListChanged, OnReviewListChanged {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    public TrailerListChangedBroadcastReceiver mTrailerListChangedBroadcastReceiver;
    public static final String TRAILER_CHANGED = "trailer_changed";
    public static final int COL_TRAILER_ID = 0;
    public static final int COL_TRAILER_KEY = 1;
    private static final String[] TRAILER_LIST_COLUMNS = {
            MovieContract.TrailersEntry.TABLE_NAME + "." +
                    MovieContract.TrailersEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailersEntry.COLUMN_TRAILER_PATH};

    public ReviewListChangedBroadcastReceiver mReviewListChangedBroadcastReceiver;
    public static final String REVIEW_CHANGED = "review_changed";
    public static final int COL_REVIEW_ID = 0;
    public static final int COL_REVIEW_AUTHOR = 1;
    public static final int COL_REVIEW_CONTENT = 2;
    private static final String[] REVIEW_LIST_COLUMNS = {
            MovieContract.ReviewsEntry.TABLE_NAME + "." +
                    MovieContract.ReviewsEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_CONTENT};

    public Cursor mCursor;
    public Movie mMovie;
    public RecyclerView mTrailerRecyclerView;
    public TrailerListCursorAdapter mTrailerRecyclerAdapter;
    public RecyclerView mReviewRecyclerView;
    public ReviewListCursorAdapter mReviewRecyclerAdapter;

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
    public TextView reviewLabel;


    public DetailFragment() {
        mTrailerListChangedBroadcastReceiver = new TrailerListChangedBroadcastReceiver(this);
        mReviewListChangedBroadcastReceiver = new ReviewListChangedBroadcastReceiver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        this.favoriteCheckBox = (CheckBox) rootView.findViewById(R.id.favorite_check_box);
        this.title = (TextView) rootView.findViewById(R.id.detail_title);
        this.poster = ((ImageView) rootView.findViewById(R.id.detail_poster));
        this.overviewLabel = (TextView)rootView.findViewById(R.id.detail_overview_label);
        this.overview = (TextView) rootView.findViewById(R.id.detail_overview);
        this.rateLabel = (TextView) rootView.findViewById(R.id.detail_rate_label);
        this.rate = (TextView) rootView.findViewById(R.id.detail_rate);
        this.releaseLabel = (TextView) rootView.findViewById(R.id.detail_release_label);
        this.release = (TextView) rootView.findViewById(R.id.detail_release);
        this.trailerLabel = (TextView) rootView.findViewById(R.id.detail_trailer_label);

        mTrailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_trailer_list);
        mTrailerRecyclerAdapter = new TrailerListCursorAdapter(getActivity());
        mTrailerRecyclerView.setAdapter(mTrailerRecyclerAdapter);

        this.reviewLabel = (TextView) rootView.findViewById(R.id.detail_review_label);

        mReviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_review_list);
        mReviewRecyclerAdapter = new ReviewListCursorAdapter(getActivity());
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

        if (getActivity().getIntent().hasExtra(ARG_MOVIE)) {
            mMovie = getActivity().getIntent().getParcelableExtra(ARG_MOVIE);
        }
        if (getActivity().getIntent().getData()!=null){
            Uri uri = getActivity().getIntent().getData();
            String[] parts = uri.getPath().split("[/]");
            String[] idAndTitle = parts[parts.length-1].split("[-]");
            String id = idAndTitle[0];
            Utility ut = new Utility(getContext());
            Log.d(LOG_TAG, "THE ID IS HERE "+id);
            mMovie = ut.getMovieById(id);
        }
        Bundle args = getArguments();
        if (args != null){
            mMovie = args.getParcelable(ARG_MOVIE);
        }
        if(null!=mMovie) {
            showMovie(mMovie);
            startUpdaters();
            updateTrailerList();
            updateReviewList();
        }
        else{
            this.favoriteCheckBox.setVisibility(View.INVISIBLE);
        }

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

    public void showMovie(Movie movie) {
        this.favoriteCheckBox.setVisibility(View.VISIBLE);
        this.favoriteCheckBox.setText(R.string.favorite_label);
        this.title.setText(movie.original_title);
        Utility ut = new Utility(getActivity());
        if(ut.isConnectionAvailable()) {
            Picasso.with(getActivity().getBaseContext()).load("http://image.tmdb.org/t/p/w185" + movie.poster_path).into(this.poster);
        }
        else{
            this.poster.setImageResource(R.drawable.blank_poster);
        }
        this.overviewLabel.setText(R.string.overview_label);
        this.overview.setText(movie.overview);
        this.rateLabel.setText(R.string.rate_label);
        this.rate.setText(""+movie.vote_average);
        this.releaseLabel.setText(R.string.release_label);
        this.release.setText(movie.release_date);
        this.trailerLabel.setText(R.string.trailer_label);
        this.reviewLabel.setText(R.string.detail_review_label);
        if (movie.favorite==1){
            this.favoriteCheckBox.setChecked(true);
        }
        else if (movie.favorite==0){
            this.favoriteCheckBox.setChecked(false);
        }
        else{
            System.out.println("THE VALUE OF FAVORITE OBJECT IS "+movie.favorite);
        }
    }

    public void startUpdaters(){
        /**
         * Here we will replace the AsyncTask with a IntentService
         */
//        TrailersAsyncTask trailersAsyncTask = new TrailersAsyncTask(this, getContext(), mMovie);
//        trailersAsyncTask.execute();
//        ReviewAsyncTask reviewAsyncTask = new ReviewAsyncTask(this, getContext(), mMovie);
//        reviewAsyncTask.execute();
        if(null!=mMovie) {
            Intent intent = new Intent(getContext(), ReviewAndTrailerService.class);
            intent.putExtra(ARG_MOVIE, mMovie);
            getActivity().startService(intent);
        }
    }

    @Override
    public void updateTrailerList() {
        Cursor cursor = getContext().getContentResolver().query(MovieContract.TrailersEntry.CONTENT_URI,
                TRAILER_LIST_COLUMNS,
                MovieContract.TrailersEntry.COLUMN_MOVIE_ID+"=? ",
                new String[]{mMovie.id},
                null);

        mTrailerRecyclerAdapter.setMovieCursor(cursor);
    }

    @Override
    public void updateReviewList(){
        Cursor cursor = getContext().getContentResolver().query(MovieContract.ReviewsEntry.CONTENT_URI,
                REVIEW_LIST_COLUMNS,
                MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+"=? ",
                new String[]{mMovie.id},
                null);

        mReviewRecyclerAdapter.setMovieCursor(cursor);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mTrailerListChangedBroadcastReceiver.register(getContext());
        mReviewListChangedBroadcastReceiver.register(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTrailerListChangedBroadcastReceiver.unregister(getContext());
        mReviewListChangedBroadcastReceiver.unregister(getContext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }
}
