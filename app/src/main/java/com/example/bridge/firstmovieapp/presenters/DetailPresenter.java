package com.example.bridge.firstmovieapp.presenters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.broadcastreceivers.ReviewListChangedBroadcastReceiver;
import com.example.bridge.firstmovieapp.broadcastreceivers.TrailerListChangedBroadcastReceiver;
import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.fragments.MovieListFragment;
import com.example.bridge.firstmovieapp.interfaces.IDetailPres;
import com.example.bridge.firstmovieapp.interfaces.IDetailView;
import com.example.bridge.firstmovieapp.interfaces.OnReviewListChanged;
import com.example.bridge.firstmovieapp.interfaces.OnTrailerListChanged;
import com.example.bridge.firstmovieapp.syncservice.LooseMovieService;
import com.example.bridge.firstmovieapp.syncservice.ReviewAndTrailerService;

import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE;
import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE_ID;
import static com.example.bridge.firstmovieapp.presenters.MovieListPresenter.MOVIE_LIST_COLUMNS;

public class DetailPresenter implements IDetailPres, LoaderManager.LoaderCallbacks<Cursor>, OnTrailerListChanged, OnReviewListChanged {

    private IDetailView mView;
    private Context mContext;
    private LoaderManager mLoaderManager;
    private Uri mUri;
    private Cursor mCursor;
    private Movie mMovie;
    private TrailerListChangedBroadcastReceiver mTrailerListChangedBroadcastReceiver;
    private ReviewListChangedBroadcastReceiver mReviewListChangedBroadcastReceiver;
    private static final int DETAIL_FRAGMENT_LOADER = 0;
    public static final String[] TRAILER_LIST_COLUMNS = {
            MovieContract.TrailersEntry.TABLE_NAME + "." +
                    MovieContract.TrailersEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailersEntry.COLUMN_TRAILER_PATH};
    public static final String[] REVIEW_LIST_COLUMNS = {
            MovieContract.ReviewsEntry.TABLE_NAME + "." +
                    MovieContract.ReviewsEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_CONTENT};

    public DetailPresenter (IDetailView view, Context context, LoaderManager loaderManager, Uri uri){
        this.mView = view;
        this.mContext = context;
        this.mLoaderManager = loaderManager;
        this.mUri = uri;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri.toString().contains(mContext.getString(R.string.the_movie_db_host))){
            String path = mUri.getLastPathSegment();
            int index = path.indexOf('-');
            if(index>-1){
                path = path.substring(0, index);
            }
            mUri = MovieContract.MoviesEntry.buildMovieUri(Long.parseLong(path));
        }
        return new CursorLoader(mContext,
                mUri,
                MOVIE_LIST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        if(mCursor!=null && mCursor.moveToFirst()) {
            mMovie = new Movie();
            mMovie.id = mCursor.getString(MovieListFragment.COL_MOVIE_ID);
            mMovie.title = mCursor.getString(MovieListFragment.COL_TITLE);
            mMovie.poster_path = mCursor.getString(MovieListFragment.COL_POSTER_PATH);
            mMovie.vote_average = mCursor.getFloat(MovieListFragment.COL_VOTE_AVERAGE);
            mMovie.overview = mCursor.getString(MovieListFragment.COL_OVERVIEW);
            mMovie.release_date = mCursor.getString(MovieListFragment.COL_RELEASE_DATE);
            mMovie.popularity = mCursor.getFloat(MovieListFragment.COL_POPULARITY);
            mMovie.favorite = mCursor.getInt(MovieListFragment.COL_FAVORITE);
            mView.showMovie(mMovie);
            startUpdaters();
        }
        else{
            String[] parts = mUri.toString().split("[/]");
            String idFromUri = parts[parts.length-1];
            Intent intent = new Intent(mContext, LooseMovieService.class);
            intent.putExtra(ARG_MOVIE_ID, idFromUri);
            mContext.startService(intent);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }

    public void startUpdaters(){
        if(null!=mMovie) {
            mView.updatersStarted();
            Intent intent = new Intent(mContext, ReviewAndTrailerService.class);
            intent.putExtra(ARG_MOVIE, mMovie);
            mContext.startService(intent);
        }
    }

    @Override
    public void onCreate(){
        if(mUri!=null) {
            mLoaderManager.initLoader(DETAIL_FRAGMENT_LOADER, null, this);
        }
        mTrailerListChangedBroadcastReceiver = new TrailerListChangedBroadcastReceiver(this);
        mReviewListChangedBroadcastReceiver = new ReviewListChangedBroadcastReceiver(this);
    }

    @Override
    public void onDestroy(){
        mView = null;
    }

    @Override
    public void onPause(){
        mTrailerListChangedBroadcastReceiver.unregister(mContext);
        mReviewListChangedBroadcastReceiver.unregister(mContext);
    }

    @Override
    public void onResume(){
        mTrailerListChangedBroadcastReceiver.register(mContext);
        mReviewListChangedBroadcastReceiver.register(mContext);
    }

    @Override
    public void updateTrailerList() {
        if(mMovie!=null) {
            Cursor cursor = mContext.getContentResolver().query(MovieContract.TrailersEntry.CONTENT_URI,
                    TRAILER_LIST_COLUMNS,
                    MovieContract.TrailersEntry.COLUMN_MOVIE_ID + "=? ",
                    new String[]{mMovie.id},
                    null);
            mView.setUpdatedTrailerList(cursor);
        }
    }

    @Override
    public void updateReviewList(){
        if(mMovie!=null) {
            Cursor cursor = mContext.getContentResolver().query(MovieContract.ReviewsEntry.CONTENT_URI,
                    REVIEW_LIST_COLUMNS,
                    MovieContract.ReviewsEntry.COLUMN_MOVIE_ID + "=? ",
                    new String[]{mMovie.id},
                    null);
            mView.setUpdatedReviewList(cursor);
        }
    }

    @Override
    public void setFavorite(Movie movie, boolean favorite){
        if(movie!=null) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MoviesEntry.COLUMN_FAVORITE, favorite);
            mContext.getContentResolver().update(MovieContract.MoviesEntry.CONTENT_URI,
                    movieValues,
                    MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{movie.id});
            mContext.getContentResolver().notifyChange(MovieContract.MoviesEntry.CONTENT_URI, null);
        }
    }

    @Override
    public Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_message) +
                " " + "http://" + mContext.getString(R.string.the_movie_db_host) +
                "/movie/" + mMovie.id);
        return shareIntent;
    }
}
