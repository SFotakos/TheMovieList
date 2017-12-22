package com.sfotakos.themovielist;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sfotakos.themovielist.MovieList.MainActivity;
import com.sfotakos.themovielist.MovieList.Model.Movie;
import com.sfotakos.themovielist.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

@SuppressWarnings("FieldCanBeLocal")
public class DetailActivity extends AppCompatActivity {

    private Movie mMovie;

    private ActivityDetailBinding mBinding;

    private boolean mFavorited = false;

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
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setTitle(mMovie.getTitle());
                    actionBar.setSubtitle(formattedReleaseDate);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_favorite:
                //TODO implement logic based on movieID being on the local favorite list.
                mFavorited = !mFavorited;
                if (mFavorited){
                    item.setIcon(R.drawable.ic_favorite_white);
                } else {
                    item.setIcon(R.drawable.ic_favorite_border_white);
                }

                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
