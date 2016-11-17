package com.example.bridge.firstmovieapp.syncservice;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.data.MovieContract;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.entities.MovieList;
import com.example.bridge.firstmovieapp.interfaces.IFetchDataFromMovieDB;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncAdapter extends AbstractThreadedSyncAdapter{
    public final String LOG_TAG = SyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60*180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public SyncAdapter(Context context, boolean autoInitialize){
        super(context,autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IFetchDataFromMovieDB fetchDataFromMovieDB = retrofit.create(IFetchDataFromMovieDB.class);
        //        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        //        String sort = prefs.getString(mContext.getString(R.string.pref_sort_key),
        //                mContext.getString(R.string.pref_sort_popular));
        String sortPopular = getContext().getString(R.string.pref_sort_popular);
        Call<MovieList> movieListCallPopular = fetchDataFromMovieDB.getMovieList(sortPopular);
        MovieList listPopular = null;
        try {
            listPopular = movieListCallPopular.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sortTopRated = getContext().getString(R.string.pref_sort_top_rated);
        Call<MovieList> movieListCallTopRated = fetchDataFromMovieDB.getMovieList(sortTopRated);
        MovieList listTopRated = null;
        try {
            listTopRated = movieListCallTopRated.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MovieList movieList = new MovieList();
        movieList.results = new ArrayList<>();
        movieList.results.addAll(listPopular.results);
        movieList.results.addAll(listTopRated.results);
        for (Movie iterator : movieList.results) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MoviesEntry.COLUMN_MOVIE_ID, iterator.id);
            movieValues.put(MovieContract.MoviesEntry.COLUMN_TITLE, iterator.original_title);
            movieValues.put(MovieContract.MoviesEntry.COLUMN_POSTER_PATH, iterator.poster_path);
            movieValues.put(MovieContract.MoviesEntry.COLUMN_OVERVIEW, iterator.overview);
            movieValues.put(MovieContract.MoviesEntry.COLUMN_RELEASE_DATE, iterator.release_date);
            movieValues.put(MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE, iterator.vote_average);
            movieValues.put(MovieContract.MoviesEntry.COLUMN_POPULARITY, iterator.popularity);
            //            movieValues.put(MovieContract.MoviesEntry.COLUMN_FAVORITE, iterator.favorite);
            Cursor cursor = getContext().getContentResolver().query(MovieContract.MoviesEntry.CONTENT_URI,
                    null,
                    MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{iterator.id},
                    null);
            if (cursor.getCount() != 0) {
                getContext().getContentResolver().update(MovieContract.MoviesEntry.CONTENT_URI,
                        movieValues,
                        MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{iterator.id});
            } else {
                getContext().getContentResolver().insert(MovieContract.MoviesEntry.CONTENT_URI, movieValues);
            }
            cursor.close();
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
