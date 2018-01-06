package com.sfotakos.themovielist.movie_details.model;

import android.net.Uri;

import com.sfotakos.themovielist.general.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by spyridion on 05/01/18.
 */

public class MovieReviewRequest {

    private static final String TYPE_PARAM = "movie";
    private static final String REVIEW_FUNCTIONALITY_PARAM = "reviews";

    private static final String LANGUAGE_PARAM = "language";
    private static final String PAGE_PARAM = "page";

    private static final String LANGUAGE = "en-US";

    private final int movieId;
    private final int page;

    public MovieReviewRequest(int movieId, int page) {
        this.movieId = movieId;
        this.page = page;
    }

    public URL buildMovieReviewsRequest() {
        Uri builtUri = Uri.parse(NetworkUtils.BASE_TMDB_URL).buildUpon()
                .appendPath(NetworkUtils.TMDB_API_VERSION)
                .appendPath(TYPE_PARAM)
                .appendPath(Integer.toString(movieId))
                .appendPath(REVIEW_FUNCTIONALITY_PARAM)
                .appendQueryParameter(NetworkUtils.TMDB_API_KEY_PARAM, NetworkUtils.TMDB_API_KEY)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
