package com.sfotakos.themovielist.general.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sfotakos.themovielist.general.data.MovieListContract.FavoriteMovieEntry;

/**
 * Created by spyridion on 22/12/17.
 */

public class MovieListDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movielist.db";

    private static final int DATABASE_VERSION = 8;

    public MovieListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_LIST_TABLE =

                "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +

                        FavoriteMovieEntry.MOVIE_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE," +

                        FavoriteMovieEntry.RELEASE_DATE + " TEXT," +
                        FavoriteMovieEntry.TITLE + " TEXT, " +
                        FavoriteMovieEntry.POSTER + " TEXT, " +
                        FavoriteMovieEntry.AVERAGE_SCORE + " TEXT, " +
                        FavoriteMovieEntry.SYNOPSIS + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_LIST_TABLE);
    }

    //TODO do not drop table on update.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
