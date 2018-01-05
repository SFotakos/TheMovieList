package com.sfotakos.themovielist.movie_list.model;

import com.sfotakos.themovielist.general.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spyridion on 12/10/17.
 */

public class MovieListResponse {
    private Integer page;
    private Integer totalResults;
    private Integer totalPages;

    private List<Movie> movieList = new ArrayList<>();

    public MovieListResponse(String discoverMovieJsonResponse) {
        try {
            getMovieResponseFromJson(discoverMovieJsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

    private void getMovieResponseFromJson(String movieString) throws JSONException {

        final String PAGE = "page";
        final String TOTAL_PAGES = "total_pages";

        final String TOTAL_RESULTS = "total_results";
        final String RESULTS = "results";

        final String VOTE_COUNT = "vote_count";
        final String ID = "id";
        final String VIDEO = "video";
        final String VOTE_AVERAGE = "vote_average";
        final String TITLE = "title";
        final String POPULARITY = "popularity";
        final String POSTER_PATH = "poster_path";
        final String ORIGINAL_LANGUAGE = "original_language";
        final String ORIGINAL_TITLE = "original_title";
        final String GENRE_IDS = "genre_ids";
        final String BACKDROP_PATH = "backdrop_path";
        final String ADULT = "adult";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";

        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";


        JSONObject movieJson = new JSONObject(movieString);

        if (movieJson.has(TOTAL_RESULTS)) {
            int resultsAmount = movieJson.getInt(TOTAL_RESULTS);

            if (resultsAmount == 0) return;
        }

        page = movieJson.getInt(PAGE);
        totalPages = movieJson.getInt(TOTAL_PAGES);
        totalResults = movieJson.getInt(TOTAL_RESULTS);

        JSONArray moviesArray = movieJson.getJSONArray(RESULTS);

        for (int i = 0; i < moviesArray.length(); i++) {
            Movie movie = new Movie();
            JSONObject movieJsonObject = moviesArray.getJSONObject(i);


            movie.setVoteCount(movieJsonObject.getInt(VOTE_COUNT));
            movie.setId(movieJsonObject.getInt(ID));
            movie.setVideo(movieJsonObject.getBoolean(VIDEO));
            movie.setVoteAverage(movieJsonObject.getDouble(VOTE_AVERAGE));
            movie.setTitle(movieJsonObject.getString(TITLE));
            movie.setPopularity(movieJsonObject.getDouble(POPULARITY));
            movie.setPosterPath(movieJsonObject.getString(POSTER_PATH));
            movie.setOriginalLanguage(movieJsonObject.getString(ORIGINAL_LANGUAGE));
            movie.setOriginalTitle(movieJsonObject.getString(ORIGINAL_TITLE));

            JSONArray genreArray = movieJsonObject.getJSONArray(GENRE_IDS);

            List<Integer> genreList = new ArrayList<>();
            for (int j = 0; j < genreArray.length(); j++) {
                genreList.add(genreArray.getInt(j));
            }

            movie.setGenreIds(genreList);

            movie.setBackdropPath(movieJsonObject.getString(BACKDROP_PATH));
            movie.setAdult(movieJsonObject.getBoolean(ADULT));
            movie.setOverview(movieJsonObject.getString(OVERVIEW));
            movie.setReleaseDate(movieJsonObject.getString(RELEASE_DATE));

            movieList.add(movie);
        }
    }
}
