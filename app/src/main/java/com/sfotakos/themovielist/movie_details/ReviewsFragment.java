package com.sfotakos.themovielist.movie_details;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.databinding.FragmentReviewsBinding;
import com.sfotakos.themovielist.general.IErrorMessages;
import com.sfotakos.themovielist.general.NetworkUtils;
import com.sfotakos.themovielist.general.model.Review;
import com.sfotakos.themovielist.movie_details.adapter.ReviewsAdapter;
import com.sfotakos.themovielist.movie_details.model.MovieReviewRequest;
import com.sfotakos.themovielist.movie_details.model.MovieReviewResponse;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ReviewsFragment extends Fragment {

    private static final String MOVIE_ID = "movie-id";

    private static final int DEFAULT_PAGE = 1;

    private FragmentReviewsBinding mBinding;

    private int mMovieId;
    private Review mReview;

    private ReviewsAdapter reviewsAdapter = new ReviewsAdapter();

    private IErrorMessages mListener;


    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance(int movieId) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putInt(MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieId = getArguments().getInt(MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView =
                inflater.inflate(R.layout.fragment_reviews, container, false);
        mBinding = DataBindingUtil.bind(fragmentView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mBinding.rvReviews.setLayoutManager(layoutManager);
        mBinding.rvReviews.setAdapter(reviewsAdapter);

        fetchReviews();

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IErrorMessages) {
            mListener = (IErrorMessages) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IErrorMessages");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void fetchReviews() {
        if (NetworkUtils.hasConnection(getContext())) {
            MovieReviewRequest movieReviewRequest =
                    new MovieReviewRequest(mMovieId, DEFAULT_PAGE);

            new FetchReviews().execute(movieReviewRequest);
        } else {
            mListener.showErrorMessage(getResources().getString(R.string.error_no_connectivity));
        }
    }

    private class FetchReviews extends AsyncTask<MovieReviewRequest, Void, MovieReviewResponse> {

        @Override
        protected void onPreExecute() {
            mListener.showErrorMessage(null);
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
            if (movieReviewResponse != null) {

                if (mListener != null) {
                    mListener.showErrorMessage(null);
                }

                List<Review> reviews = movieReviewResponse.getReviewList();
                mBinding.tvNoReviews.setVisibility(
                        (reviews == null || reviews.size() == 0) ? View.VISIBLE : View.INVISIBLE);
                reviewsAdapter.setReviewList(reviews);
            } else {
                if (mListener != null) {
                    mListener.showErrorMessage(getResources().getString(R.string.error_default));
                }
            }
        }
    }
}
