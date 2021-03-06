package com.example.bridge.firstmovieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.bridge.firstmovieapp.data.MovieContract.MoviesEntry;
import com.example.bridge.firstmovieapp.data.MovieContract.ReviewsEntry;
import com.example.bridge.firstmovieapp.data.MovieContract.TrailersEntry;


public class Provider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int TRAILER = 200;
    static final int REVIEW = 300;
    public static String ARG_MOVIE = "movie";
    public static String ARG_MOVIE_URI = "movie_uri";
    public static String ARG_MOVIE_ID = "movie_id";
    public static String ARG_KEY_WORD = "key_word";

    public static final String sMovieIdSelection =
            MoviesEntry.TABLE_NAME +
                    "." +
                    MoviesEntry.COLUMN_MOVIE_ID +
                    "=?";

//    private static final SQLiteQueryBuilder SQ_LITE_QUERY_BUILDER;
//    static {
//        SQ_LITE_QUERY_BUILDER = new SQLiteQueryBuilder();
//        SQ_LITE_QUERY_BUILDER.setTables(MoviesEntry.TABLE_NAME);
//    }
//
//    private static final String sFavoriteSelection =
//            MoviesEntry.TABLE_NAME + "." + MoviesEntry.COLUMN_FAVORITE + " = ? ";
//
//    private Cursor getFavoriteMovies(Uri uri, String[] projection, String sortOrder){
//
//        String selection = sFavoriteSelection;
//        String[] selectionArgs = new String[] {"true"};
//        return SQ_LITE_QUERY_BUILDER.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                sortOrder);
//    }
//
//    private Cursor getAllMovies(Uri uri, String[] projection, String sortOrder){
//
//        return SQ_LITE_QUERY_BUILDER.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                null,
//                null,
//                null,
//                null,
//                sortOrder);
//    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEW);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType (Uri uri){
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_WITH_ID:
                return MoviesEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MoviesEntry.CONTENT_TYPE;
            case TRAILER:
                return TrailersEntry.CONTENT_TYPE;
            case REVIEW:
                return ReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder){
        Cursor retCursor;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesEntry.TABLE_NAME,
                        null,
                        sMovieIdSelection,
                        new String[] {uri.getLastPathSegment()},
                        null,
                        null,
                        null);
                break;
            }
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrailersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIE_WITH_ID: {
                throw new android.database.SQLException("Failed to insert row into " + uri);
            }
            case MOVIE: {
                long _id = db.insert(MoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = MoviesEntry.buildMovieUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRAILER: {
                long _id = db.insert(TrailersEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = TrailersEntry.buildTrailerUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long _id = db.insert(ReviewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = ReviewsEntry.buildReviewUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null==selection){
            selection = "1";
        }

        switch (match){
            case MOVIE_WITH_ID: {
                rowsDeleted = db.delete(MoviesEntry.TABLE_NAME, sMovieIdSelection, new String[] {uri.getLastPathSegment()});
                break;
            }
                case MOVIE: {
                rowsDeleted = db.delete(MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRAILER: {
                rowsDeleted = db.delete(TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE_WITH_ID: {
                rowsUpdated = db.update(MoviesEntry.TABLE_NAME, values, sMovieIdSelection, new String[] {uri.getLastPathSegment()});
                break;
            }
            case MOVIE: {
                rowsUpdated = db.update(MoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case TRAILER: {
                rowsUpdated = db.update(TrailersEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(ReviewsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
