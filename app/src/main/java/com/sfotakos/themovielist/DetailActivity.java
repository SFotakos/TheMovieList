package com.sfotakos.themovielist;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.sfotakos.themovielist.MovieList.MainActivity;
import com.sfotakos.themovielist.MovieList.Model.Movie;
import com.sfotakos.themovielist.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

//TODO [1] Long titles should go to a new line, implement custom Toolbar.
@SuppressWarnings("FieldCanBeLocal")
public class DetailActivity extends AppCompatActivity {

    private Movie mMovie;

    private ActivityDetailBinding mBinding;

    //TODO Add customized layout for landscape orientation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mBinding.tvMovieSynopsis.setMovementMethod(new ScrollingMovementMethod());

        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(MainActivity.MOVIE_DATA_EXTRA)) {
                mMovie = intent.getParcelableExtra(MainActivity.MOVIE_DATA_EXTRA);

                //TODO [2] Date object and proper DateFormatter parsing
                String releaseDate = mMovie.getReleaseDate();
                String formattedReleaseDate = null;
                if (releaseDate != null && !releaseDate.isEmpty()) {
                    String releaseYear = releaseDate.substring(0, 4);
                    formattedReleaseDate = "(" + releaseYear + ")";
                }

                if (actionBar != null){

                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                    actionBar.setDisplayShowCustomEnabled(true);
                    actionBar.setCustomView(R.layout.actionbar_activity_detail);
                    actionBar.setDisplayHomeAsUpEnabled(true);

                    TextView title = actionBar.getCustomView().findViewById(R.id.tv_title);
                    TextView subtitle = actionBar.getCustomView().findViewById(R.id.tv_subtitle);
                    AppCompatCheckBox favorite =
                            actionBar.getCustomView().findViewById(R.id.cb_favorite);

                    title.setText(mMovie.getTitle());
                    subtitle.setText(formattedReleaseDate);

                }

                Picasso.with(this)
                        .load(mMovie.getPosterPath())
                        .placeholder(R.drawable.ic_movie_clapboard)
                        .into(mBinding.ivMoviePoster);

                Double voteAvg = mMovie.getVoteAverage()*10;
                mBinding.pbMovieAverageScore.setProgress(voteAvg.intValue());

                String avgScore = String.valueOf(voteAvg.intValue()) + " / 100";
                mBinding.tvMovieAverageScore.setText(avgScore);

                mBinding.tvMovieSynopsis.setText(String.valueOf(mMovie.getOverview()));
            }
        }
    }

}
