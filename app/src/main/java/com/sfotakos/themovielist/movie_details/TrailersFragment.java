package com.sfotakos.themovielist.movie_details;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.databinding.FragmentTrailersBinding;
import com.sfotakos.themovielist.general.IErrorMessages;
import com.sfotakos.themovielist.general.NetworkUtils;
import com.sfotakos.themovielist.general.model.Trailer;
import com.sfotakos.themovielist.movie_details.adapter.TrailersAdapter;
import com.sfotakos.themovielist.movie_details.model.MovieTrailerRequest;
import com.sfotakos.themovielist.movie_details.model.MovieTrailerResponse;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class TrailersFragment extends Fragment implements TrailersAdapter.TrailerItemClickListener {
    private static final String MOVIE_ID = "movie-id";

    private static final int DEFAULT_PAGE = 1;

    private FragmentTrailersBinding mBinding;

    private int mMovieId;
    private Trailer mTrailer;

    private TrailersAdapter trailersAdapter = new TrailersAdapter(this);

    private IErrorMessages mListener;


    public TrailersFragment() {
        // Required empty public constructor
    }

    public static TrailersFragment newInstance(int movieId) {
        TrailersFragment fragment = new TrailersFragment();
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
                inflater.inflate(R.layout.fragment_trailers, container, false);
        mBinding = DataBindingUtil.bind(fragmentView);

        LinearLayoutManager trailersLayoutManager =
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL, false);
        mBinding.rvTrailers.setLayoutManager(trailersLayoutManager);
        mBinding.rvTrailers.setAdapter(trailersAdapter);

        fetchTrailers();

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

    @Override
    public void onClick(Trailer trailer) {
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW);
        trailerIntent.setData(NetworkUtils.buildYoutubeUri(trailer.getKey()));
        startActivity(trailerIntent);
    }

    public void fetchTrailers() {
        if (NetworkUtils.hasConnection(getContext())) {
            MovieTrailerRequest movieReviewRequest =
                    new MovieTrailerRequest(mMovieId, DEFAULT_PAGE);

            new FetchTrailers().execute(movieReviewRequest);
        } else {
            mListener.showErrorMessage(getResources().getString(R.string.error_no_connectivity));
        }
    }

    private class FetchTrailers extends AsyncTask<MovieTrailerRequest, Void, MovieTrailerResponse> {

        @Override
        protected void onPreExecute() {
            mListener.showErrorMessage(null);
            super.onPreExecute();
        }

        @Override
        protected MovieTrailerResponse doInBackground(MovieTrailerRequest... movieListRequests) {

            MovieTrailerRequest movieListRequest = movieListRequests[0];
            URL requestURL = movieListRequest.buildMovieReviewsRequest();

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestURL);
                return new MovieTrailerResponse(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieTrailerResponse movieTrailerResponse) {
            if (movieTrailerResponse != null) {

                if (mListener != null) {
                    mListener.showErrorMessage(null);
                }

                List<Trailer> trailers = movieTrailerResponse.getTrailerList();
                mBinding.tvNoTrailers.setVisibility(
                        (trailers == null || trailers.size() == 0) ? View.VISIBLE : View.INVISIBLE);
                trailersAdapter.setTrailerList(trailers);
            } else {
                if (mListener != null) {
                    mListener.showErrorMessage(getResources().getString(R.string.error_default));
                }
            }
        }
    }
}
