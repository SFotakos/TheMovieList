package com.sfotakos.themovielist.movie_details.model;

import com.sfotakos.themovielist.general.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spyridion on 05/01/18.
 */

public class MovieReviewResponse {
    private Integer id;
    private Integer page;
    private Integer totalResults;
    private Integer totalPages;

    private List<Review> reviewList = new ArrayList<>();

    public MovieReviewResponse(String movieReviewJsonResponse) {
        try {
            getMovieReviewsFromJson(movieReviewJsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    private void getMovieReviewsFromJson(String reviewString) throws JSONException {

        final String MOVIE_ID = "id";
        final String PAGE = "page";
        final String TOTAL_PAGES = "total_pages";

        final String TOTAL_RESULTS = "total_results";
        final String RESULTS = "results";

        final String REVIEW_ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";

        JSONObject reviewsJson = new JSONObject(reviewString);

        if (reviewsJson.has(TOTAL_RESULTS)) {
            int resultsAmount = reviewsJson.getInt(TOTAL_RESULTS);

            if (resultsAmount == 0) return;
        }

        id = reviewsJson.getInt(MOVIE_ID);
        page = reviewsJson.getInt(PAGE);
        totalPages = reviewsJson.getInt(TOTAL_PAGES);
        totalResults = reviewsJson.getInt(TOTAL_RESULTS);

        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);

        for (int i = 0; i < reviewsArray.length(); i++) {
            Review review = new Review();
            JSONObject movieJsonObject = reviewsArray.getJSONObject(i);

            review.setId(movieJsonObject.getString(REVIEW_ID));
            review.setAuthor(movieJsonObject.getString(AUTHOR));
            review.setContent(movieJsonObject.getString(CONTENT));
            review.setUrl(movieJsonObject.getString(URL));

            reviewList.add(review);
        }
    }
}
