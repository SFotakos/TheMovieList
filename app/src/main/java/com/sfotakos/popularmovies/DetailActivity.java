package com.sfotakos.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sfotakos.popularmovies.MovieList.MainActivity;
import com.sfotakos.popularmovies.MovieList.Model.Movie;
import com.squareup.picasso.Picasso;


//TODO [1] Long titles should go to a new line, implement Toolbar.
public class DetailActivity extends AppCompatActivity {

    private Movie mMovie;

    private ProgressBar mMovieAvgScoreProgress;
    private TextView mMovieAvgScoreValue;
    private TextView mMovieSynopsis;
    private ImageView mMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieAvgScoreProgress = (ProgressBar) findViewById(R.id.pb_movie_average_score);
        mMovieAvgScoreValue = (TextView) findViewById(R.id.tv_movie_average_score);
        mMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);

        mMovieSynopsis.setMovementMethod(new ScrollingMovementMethod());

        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(MainActivity.MOVIE_DATA)) {
                mMovie = intent.getParcelableExtra(MainActivity.MOVIE_DATA);

                //TODO [2] Date object and proper DateFormatter parsing
                String releaseDate = mMovie.getReleaseDate();
                String formattedReleaseDate = null;
                if (releaseDate != null && !releaseDate.isEmpty()) {
                    String releaseYear = releaseDate.substring(0, 4);
                    formattedReleaseDate = "(" + releaseYear + ")";
                }

                if (actionBar != null){
                    actionBar.setTitle(mMovie.getTitle());
                    actionBar.setSubtitle(formattedReleaseDate);
                }

                Picasso.with(this)
                        .load(mMovie.getPosterPath())
                        .placeholder(R.drawable.ic_movie_clapboard)
                        .into(mMoviePoster);

                Double voteAvg = mMovie.getVoteAverage()*10;
                mMovieAvgScoreProgress.setProgress(voteAvg.intValue());

                String avgScore = getResources().getString(R.string.rating) +
                        ": " + String.valueOf(voteAvg.intValue()) + "%";
                mMovieAvgScoreValue.setText(avgScore);

                mMovieSynopsis.setText(String.valueOf(mMovie.getOverview()));
            }
        }
    }
}
