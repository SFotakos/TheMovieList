package com.sfotakos.themovielist.general.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.sfotakos.themovielist.general.data.MovieListContract.FavoriteMovieEntry;

/**
 * Created by spyridion on 22/12/17.
 */

public class MovieListProvider extends ContentProvider {

    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieListDbHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieListContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieListContract.PATH_FAVORITES, CODE_FAVORITES);
        matcher.addURI(authority, MovieListContract.PATH_FAVORITES + "/#", CODE_FAVORITES_WITH_ID);

        return matcher;

    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieListDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                cursor = mDbHelper.getReadableDatabase().query(
                        FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITES_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                cursor = mDbHelper.getReadableDatabase().query(
                        FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        FavoriteMovieEntry.MOVIE_ID + "=?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                long _id = db.insert(FavoriteMovieEntry.TABLE_NAME,
                        null, contentValues);

                if (_id > 0) {
                    returnUri = ContentUris
                            .withAppendedId(FavoriteMovieEntry.CONTENT_URI, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert favorite into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deletedMovie;
        switch (sUriMatcher.match(uri)){
            case CODE_FAVORITES_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                deletedMovie = db.delete(FavoriteMovieEntry.TABLE_NAME,
                        FavoriteMovieEntry.MOVIE_ID + "=?", new String[]{movieId});
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if (deletedMovie != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedMovie;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("We do not need to update entries");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
