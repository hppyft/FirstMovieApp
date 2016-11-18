package com.example.bridge.firstmovieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bridge.firstmovieapp.data.MovieContract.MoviesEntry;
import com.example.bridge.firstmovieapp.data.MovieContract.TrailersEntry;
import com.example.bridge.firstmovieapp.data.MovieContract.ReviewsEntry;




/**
 * Created by bridge on 03/11/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry.COLUMN_MOVIE_ID + " TEXT PRIMARY KEY," +
                MoviesEntry.COLUMN_TITLE + " TEXT, " +
                MoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesEntry.COLUMN_VOTE_AVERAGE + " NUMERIC, " +
                MoviesEntry.COLUMN_POPULARITY + " NUMERIC, " +
                MoviesEntry.COLUMN_FAVORITE + " INTEGER" +
                " );";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                TrailersEntry.TABLE_NAME + " (" +
                TrailersEntry.COLUMN_TRAILER_ID + " TEXT PRIMARY KEY," +
                TrailersEntry.COLUMN_MOVIE_ID + " TEXT, " +
                TrailersEntry.COLUMN_TRAILER_PATH + " TEXT, " +
                "FOREIGN KEY(" + TrailersEntry.COLUMN_MOVIE_ID + ") " +
                "REFERENCES " + MoviesEntry.TABLE_NAME + "(" +
                MoviesEntry.COLUMN_MOVIE_ID + ")" +
                " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry.COLUMN_REVIEW_ID + " TEXT PRIMARY KEY," +
                ReviewsEntry.COLUMN_MOVIE_ID + " TEXT, " +
                ReviewsEntry.COLUMN_AUTHOR + " TEXT, " +
                ReviewsEntry.COLUMN_CONTENT + " TEXT, " +
                "FOREIGN KEY(" + ReviewsEntry.COLUMN_MOVIE_ID + ") " +
                "REFERENCES " + MoviesEntry.TABLE_NAME + "(" +
                MoviesEntry.COLUMN_MOVIE_ID + ")" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /**
         * I don't want to wipe all the stored data
         * because I don't want the users to lose their favorite's list
         * so I'm commenting the next line
         */
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
