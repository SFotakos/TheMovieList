package com.sfotakos.themovielist;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.sfotakos.themovielist.general.model.Movie;
import com.sfotakos.themovielist.movie_details.DetailActivity;
import com.sfotakos.themovielist.movie_list.adapter.MarginItemDecoration;
import com.sfotakos.themovielist.movie_list.adapter.MovieListAdapter;
import com.sfotakos.themovielist.databinding.ActivityFavoritesBinding;
import com.sfotakos.themovielist.general.data.MovieListContract.FavoriteMovieEntry;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements MovieListAdapter.MovieItemClickListener {

    private static final int GRID_COLUMNS = 2;

    private ActivityFavoritesBinding mBinding;

    private MovieListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Favorites");
        }

        mAdapter = new MovieListAdapter(this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, GRID_COLUMNS);

        mBinding.rvFavorites.setLayoutManager(layoutManager);

        // Margins for item decorator
        int marginInPixels = getResources().getDimensionPixelSize(R.dimen.movie_item_margin);
        mBinding.rvFavorites.addItemDecoration(new MarginItemDecoration(marginInPixels, GRID_COLUMNS));

        mBinding.rvFavorites.setAdapter(mAdapter);

        Cursor cursor = getContentResolver().query(
                FavoriteMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            List<Movie> movieList = new ArrayList<>();
            while (cursor.moveToNext()) {
                Movie movie = new Movie();
                int movieIdIndex = cursor.getColumnIndex(FavoriteMovieEntry.MOVIE_ID);
                int movieReleaseDateIndex = cursor.getColumnIndex(FavoriteMovieEntry.RELEASE_DATE);
                int movieTitleIndex = cursor.getColumnIndex(FavoriteMovieEntry.TITLE);
                int moviePosterIndex = cursor.getColumnIndex(FavoriteMovieEntry.POSTER);
                int movieAverageScoreIndex = cursor.getColumnIndex(FavoriteMovieEntry.AVERAGE_SCORE);
                int movieSynopsisIndex = cursor.getColumnIndex(FavoriteMovieEntry.SYNOPSIS);

                movie.setId(cursor.getInt(movieIdIndex));
                movie.setReleaseDate(cursor.getString(movieReleaseDateIndex));
                movie.setTitle(cursor.getString(movieTitleIndex));
                movie.setFullPosterPath(cursor.getString(moviePosterIndex));
                movie.setVoteAverage(Double.valueOf(cursor.getString(movieAverageScoreIndex)));
                movie.setOverview(cursor.getString(movieSynopsisIndex));

                movieList.add(movie);
            }

            mAdapter.setMovieList(movieList);
            cursor.close();
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailActivityIntent = new Intent(this, DetailActivity.class);
        detailActivityIntent.putExtra(DetailActivity.MOVIE_DATA_EXTRA, movie);
        detailActivityIntent.setAction(DetailActivity.FAVORITES_ACTIVITY_PARENT);
        startActivity(detailActivityIntent);
    }
}
