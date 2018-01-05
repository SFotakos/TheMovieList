package com.sfotakos.themovielist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.sfotakos.themovielist.general.data.MovieListContract.FavoriteMovieEntry;
import com.sfotakos.themovielist.general.model.Movie;
import com.sfotakos.themovielist.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

@SuppressWarnings("FieldCanBeLocal")
public class DetailActivity extends AppCompatActivity {

    public static final String MOVIE_DATA_EXTRA = "movie-data";

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
            if (intent.hasExtra(MOVIE_DATA_EXTRA)) {
                mMovie = intent.getParcelableExtra(MOVIE_DATA_EXTRA);

                //TODO [2] Date object and proper DateFormatter parsing
                String releaseDate = mMovie.getReleaseDate();
                String formattedReleaseDate = null;
                if (releaseDate != null && !releaseDate.isEmpty()) {
                    String releaseYear = releaseDate.substring(0, 4);
                    formattedReleaseDate = "(" + releaseYear + ")";
                }

                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setTitle(mMovie.getTitle());
                    actionBar.setSubtitle(formattedReleaseDate);
                }

                Picasso.with(this)
                        .load(mMovie.getPosterPath())
                        .placeholder(R.drawable.ic_movie_clapboard)
                        .into(mBinding.ivMoviePoster);

                Double voteAvg = mMovie.getVoteAverage() * 10;
                mBinding.pbMovieAverageScore.setProgress(voteAvg.intValue());

                String avgScore = String.valueOf(voteAvg.intValue()) + " / 100";
                mBinding.tvMovieAverageScore.setText(avgScore);

                mBinding.tvMovieSynopsis.setText(String.valueOf(mMovie.getOverview()));
            }
        }
    }

    private void updateFavoritedIcon(MenuItem item) {
        if (mFavorited) {
            item.setIcon(R.drawable.ic_favorite_white);
        } else {
            item.setIcon(R.drawable.ic_favorite_border_white);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail, menu);

        isFavorite();

        updateFavoritedIcon(menu.findItem(R.id.action_add_favorite));

        return true;
    }

    private void isFavorite(){

        Uri uriToQuery = FavoriteMovieEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(mMovie.getId())).build();
        Cursor cursor = getContentResolver().query(
                uriToQuery,
                null,
                null,
                null,
                null);

        mFavorited = (cursor != null && cursor.getCount() != 0);

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_favorite:

                if (!mFavorited) {
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(FavoriteMovieEntry.MOVIE_ID, mMovie.getId());
                    contentValues.put(FavoriteMovieEntry.RELEASE_DATE, mMovie.getReleaseDate());
                    contentValues.put(FavoriteMovieEntry.TITLE, mMovie.getTitle());
                    contentValues.put(FavoriteMovieEntry.POSTER, mMovie.getPosterPath());
                    contentValues.put(FavoriteMovieEntry.AVERAGE_SCORE, mMovie.getVoteAverage());
                    contentValues.put(FavoriteMovieEntry.SYNOPSIS, mMovie.getOverview());

                    Uri uri = getContentResolver()
                            .insert(FavoriteMovieEntry.CONTENT_URI, contentValues);

                    if (uri != null) {
                        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
                        mFavorited = true;
                    }
                } else {
                    Uri uriToDelete = FavoriteMovieEntry.CONTENT_URI.buildUpon()
                            .appendPath(String.valueOf(mMovie.getId())).build();
                    int deleted = getContentResolver().delete(uriToDelete, null, null);
                    if (deleted > 0){
                        Toast.makeText(getBaseContext(), String.valueOf(deleted), Toast.LENGTH_LONG).show();
                        mFavorited = false;
                    }
                }
                updateFavoritedIcon(item);

                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
