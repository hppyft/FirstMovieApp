package com.example.bridge.firstmovieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bridge.firstmovieapp.data.MovieContract.MoviesEntry;

/**
 * Created by bridge on 03/11/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry.COLUMN_MOVIE_ID + " TEXT PRIMARY KEY," +
                MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_VOTE_AVERAGE + " NUMERIC NOT NULL, " +
                MoviesEntry.COLUMN_POPULARITY + " NUMERIC NOT NULL, " +
                MoviesEntry.COLUMN_FAVORITE + " INTEGER" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /**
         * I don't want to wipe all the stored data
         * because I don't want the users to lose their favorite's list
         * so I'm commenting the next two lines
         */
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
