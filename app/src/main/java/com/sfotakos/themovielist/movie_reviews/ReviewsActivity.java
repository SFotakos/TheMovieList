package com.sfotakos.themovielist.movie_reviews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;

import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.databinding.ActivityReviewsBinding;
import com.sfotakos.themovielist.general.NetworkUtils;
import com.sfotakos.themovielist.movie_reviews.adapter.ReviewsAdapter;
import com.sfotakos.themovielist.movie_reviews.model.MovieReviewRequest;
import com.sfotakos.themovielist.movie_reviews.model.MovieReviewResponse;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.security.InvalidParameterException;

public class ReviewsActivity extends AppCompatActivity {

    public static final String MOVIE_ID = "movie-id";

    private static final int DEFAULT_PAGE = 1;

    private ActivityReviewsBinding mBinding;

    private ReviewsAdapter reviewsAdapter = new ReviewsAdapter();

    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reviews);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Reviews");
        }

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(MOVIE_ID)) {
                movieId = intent.getIntExtra(MOVIE_ID, 0);
                if (movieId == 0){
                    throw new InvalidParameterException("A valid movieId is needed to fetch reviews");
                }
            }
        } else {
            throw new InvalidParameterException("A valid movieId is needed to fetch reviews");
        }

        LinearLayoutManager reviewsLayoutManager =
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvReviews.setLayoutManager(reviewsLayoutManager);
        mBinding.rvReviews.setAdapter(reviewsAdapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mBinding.rvReviews);

        fetchReviews();

    }

    private void showErrorMessage(String errorMessage) {
        //mBinding.tvErrorMessage.setText(errorMessage);
        //mBinding.tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void fetchReviews() {
        if (NetworkUtils.hasConnection(this)) {
            MovieReviewRequest movieReviewRequest =
                    new MovieReviewRequest(movieId, DEFAULT_PAGE);

            new FetchReviews().execute(movieReviewRequest);
        } else {
            showErrorMessage(getResources().getString(R.string.error_no_connectivity));
        }
    }

    private class FetchReviews extends AsyncTask<MovieReviewRequest, Void, MovieReviewResponse> {

        @Override
        protected void onPreExecute() {
            //mBinding.tvErrorMessage.setVisibility(View.GONE);
            //mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected MovieReviewResponse doInBackground(MovieReviewRequest... movieListRequests) {

            MovieReviewRequest movieListRequest = movieListRequests[0];
            URL requestURL = movieListRequest.buildMovieReviewsRequest();

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestURL);
                return new MovieReviewResponse(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieReviewResponse movieReviewResponse) {
            //mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieReviewResponse != null) {
                //showMovieList();
                reviewsAdapter.setReviewList(movieReviewResponse.getReviewList());
            } else {
                showErrorMessage(getResources().getString(R.string.error_default));
            }
        }
    }

    // Deal with connectivity changes
    private class ConnectivityReceiver extends BroadcastReceiver implements Serializable {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (NetworkUtils.hasConnection(context)) {
                fetchReviews();
            } else {
                showErrorMessage(getResources().getString(R.string.error_no_connectivity));
            }
        }
    }

}
