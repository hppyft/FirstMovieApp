package com.example.bridge.firstmovieapp.entities;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;


public class Utility {
    public Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public boolean isConnectionAvailable(){
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

//    public void getCursorByMovieId(String id) {
//
//        Cursor cursor = context.getContentResolver().query(MovieContract.MoviesEntry.CONTENT_URI,
//                MovieListFragment.MOVIE_LIST_COLUMNS,
//                MovieContract.MoviesEntry.COLUMN_MOVIE_ID + "=?",
//                new String[] {id},
//                null);
//
//        Movie movie = new Movie();
//
//        if(cursor.moveToFirst()) {
////            movie.id = cursor.getString(MovieListFragment.COL_MOVIE_ID);
////            movie.original_title = cursor.getString(MovieListFragment.COL_TITLE);
////            movie.poster_path = cursor.getString(MovieListFragment.COL_POSTER_PATH);
////            movie.vote_average = cursor.getFloat(MovieListFragment.COL_VOTE_AVERAGE);
////            movie.overview = cursor.getString(MovieListFragment.COL_OVERVIEW);
////            movie.release_date = cursor.getString(MovieListFragment.COL_RELEASE);
////            movie.popularity = cursor.getFloat(MovieListFragment.COL_POPULARITY);
////            movie.favorite = cursor.getInt(MovieListFragment.COL_FAVORITE);
//            return cursor;
//        }
//        else{
//            Intent intent = new Intent(context, LooseMovieService.class);
//            intent.putExtra(ARG_MOVIE_ID, id);
//            context.startService(intent);
//            return null;
//        }
//    }
}
