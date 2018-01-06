package com.sfotakos.themovielist.movie_details;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.databinding.FragmentDetailsBinding;
import com.sfotakos.themovielist.general.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailsFragment extends Fragment {
    private static final String MOVIE_PARAM = "movie-param";

    private Movie mMovie;
    private FragmentDetailsBinding mBinding;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(Movie movie) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_PARAM, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(MOVIE_PARAM);
            if (mMovie == null){
                throw new RuntimeException("Movie data was not recovered properly");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView =
                inflater.inflate(R.layout.fragment_details, container, false);
        mBinding = DataBindingUtil.bind(fragmentView);

        Picasso.with(getContext())
                .load(mMovie.getPosterPath())
                .placeholder(R.drawable.ic_movie_clapboard)
                .into(mBinding.ivMoviePoster);

        Double voteAvg = mMovie.getVoteAverage() * 10;
        mBinding.pbMovieAverageScore.setProgress(voteAvg.intValue());

        String avgScore = String.valueOf(voteAvg.intValue()) + " / 100";
        mBinding.tvMovieAverageScore.setText(avgScore);

        mBinding.tvMovieSynopsis.setText(String.valueOf(mMovie.getOverview()));
        mBinding.tvMovieSynopsis.setMovementMethod(new ScrollingMovementMethod());

        return fragmentView;
    }
}
