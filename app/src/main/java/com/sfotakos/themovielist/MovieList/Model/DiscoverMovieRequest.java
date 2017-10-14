package com.sfotakos.themovielist.MovieList.Model;

import android.net.Uri;

import com.sfotakos.themovielist.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by spyridion on 12/10/17.
 */


public class DiscoverMovieRequest {

    private static final String FUNCTIONALITY_PARAM = "discover";
    private static final String TYPE_PARAM = "movie";
    private static final String SORT_BY_PARAM = "sort_by";
    private static final String INCLUDE_ADULT_PARAM = "include_adult";
    private static final String INCLUDE_VIDEO_PARAM = "include_video";
    private static final String VOTE_COUNT_GTE_PARAM = "vote_count.gte";
    private static final String PAGE_PARAM = "page";

    //There were some really bad results showing without vote count,
    //pornography labeled as not adult, movies with 10 average score, this mitigates that.
    private static final String VOTE_COUNT_GTE = "300";

    //TODO [X] Add more parameters
    public enum SortTypes {
        POPULARITY("popularity"),
        RELEASE_DATE("release_date"),
        REVENUE("revenue"),
        PRIMARY_RELEASE_DATE("primary_release_date"),
        ORIGINAL_TITLE("original_title"),
        VOTE_AVERAGE("vote_average"),
        VOTE_COUNT("vote_count");

        private final String apiSortType;

        SortTypes(String apiSortType) {
            this.apiSortType = apiSortType;
        }

        public String getApiSortType(){
            return apiSortType;
        }
    }

    public enum Order {
        ASCENDING("asc"),
        DESCENDING("desc");

        private final String apiSortOrder;

        Order(String apiSortOrder) {
            this.apiSortOrder = apiSortOrder;
        }

        public String getApiSortOrder(){
            return apiSortOrder;
        }
    }

    private String sortBy;
    private final int page;

    public DiscoverMovieRequest(String sortBy, int page) {
        this.sortBy = sortBy;
        this.page = page;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public static String getSortBy(SortTypes sortTypes, Order order) {
        return sortTypes.getApiSortType() + "." + order.getApiSortOrder();
    }

    public URL buildMovieDiscoveryRequestURL() {
        Uri builtUri = Uri.parse(NetworkUtils.BASE_TMDB_URL).buildUpon()
                .appendPath(NetworkUtils.TMDB_API_VERSION)
                .appendPath(FUNCTIONALITY_PARAM)
                .appendPath(TYPE_PARAM)
                .appendQueryParameter(NetworkUtils.TMDB_API_KEY_PARAM, NetworkUtils.TMDB_API_KEY)
                .appendQueryParameter(SORT_BY_PARAM, sortBy)
                .appendQueryParameter(INCLUDE_ADULT_PARAM, "false")
                .appendQueryParameter(INCLUDE_VIDEO_PARAM, "false")
                .appendQueryParameter(VOTE_COUNT_GTE_PARAM, VOTE_COUNT_GTE)
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
