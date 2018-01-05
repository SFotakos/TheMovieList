package com.sfotakos.themovielist.general.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by spyridion on 22/12/17.
 */

public class MovieListContract {


    public static final String CONTENT_AUTHORITY = "com.sfotakos.themovielist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "favorite_movies";

        public static final String MOVIE_ID = "movie_id";
        public static final String RELEASE_DATE = "release_date";
        public static final String TITLE = "title";
        public static final String POSTER = "poster";
        public static final String AVERAGE_SCORE = "average_score";
        public static final String SYNOPSIS = "synopsis";
    }
}
