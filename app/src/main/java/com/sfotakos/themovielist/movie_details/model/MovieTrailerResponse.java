package com.sfotakos.themovielist.movie_details.model;

import com.sfotakos.themovielist.general.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spyridion on 05/01/18.
 */

public class MovieTrailerResponse {
    private Integer id;

    private List<Trailer> trailerList = new ArrayList<>();

    public MovieTrailerResponse(String movieTrailerJsonResponse) {
        try {
            getMovieTrailersFromJson(movieTrailerJsonResponse);
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

    public List<Trailer> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<Trailer> trailerList) {
        this.trailerList = trailerList;
    }

    private void getMovieTrailersFromJson(String trailerString) throws JSONException {

        final String MOVIE_ID = "id";

        final String RESULTS = "results";

        final String TRAILER_ID = "id";
        final String ISO_639_1 = "iso_639_1";
        final String ISO_3166_1 = "iso_3166_1";
        final String KEY = "key";
        final String NAME = "name";
        final String SITE = "site";
        final String SIZE = "size";
        final String TYPE = "type";

        JSONObject reviewsJson = new JSONObject(trailerString);
        id = reviewsJson.getInt(MOVIE_ID);

        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);
        if (reviewsArray.length() == 0) return;

        for (int i = 0; i < reviewsArray.length(); i++) {

            Trailer trailer = new Trailer();
            JSONObject movieJsonObject = reviewsArray.getJSONObject(i);

            trailer.setId(movieJsonObject.getString(TRAILER_ID));
            trailer.setIso_639_1(movieJsonObject.getString(ISO_639_1));
            trailer.setIso_3166_1(movieJsonObject.getString(ISO_3166_1));
            trailer.setKey(movieJsonObject.getString(KEY));
            trailer.setName(movieJsonObject.getString(NAME));
            trailer.setSite(movieJsonObject.getString(SITE));
            trailer.setSize(movieJsonObject.getString(SIZE));
            trailer.setType(movieJsonObject.getString(TYPE));

            trailerList.add(trailer);
        }
    }
}
