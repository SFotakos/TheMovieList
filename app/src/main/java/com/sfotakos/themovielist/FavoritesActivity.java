package com.sfotakos.themovielist;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.sfotakos.themovielist.general.model.Movie;
import com.sfotakos.themovielist.movie_details.DetailsActivity;
import com.sfotakos.themovielist.movie_list.adapter.MarginItemDecoration;
import com.sfotakos.themovielist.movie_list.adapter.MovieListAdapter;
import com.sfotakos.themovielist.databinding.ActivityFavoritesBinding;
import com.sfotakos.themovielist.general.data.MovieListContract.FavoriteMovieEntry;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements MovieListAdapter.MovieItemClickListener {

    private static final int DEFAULT_GRID_COLUMNS = 2;

    private ActivityFavoritesBinding mBinding;

    private MovieListAdapter mAdapter;

    private int gridColumns = DEFAULT_GRID_COLUMNS;

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
        mBinding.rvFavorites.setAdapter(mAdapter);

        // Margins for item decorator
        int marginInPixels = getResources().getDimensionPixelSize(R.dimen.movie_item_margin);
        setGridColumns();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, gridColumns);
        mBinding.rvFavorites.setLayoutManager(layoutManager);
        mBinding.rvFavorites.addItemDecoration(new MarginItemDecoration(marginInPixels, gridColumns));
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailActivityIntent = new Intent(this, DetailsActivity.class);
        detailActivityIntent.putExtra(DetailsActivity.MOVIE_DATA_EXTRA, movie);
        detailActivityIntent.setAction(DetailsActivity.FAVORITES_ACTIVITY_PARENT);
        startActivity(detailActivityIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Movie> movieList = queryFavorites();
        mBinding.tvNoFavorites.setVisibility(
                (movieList == null || movieList.size() == 0) ? View.VISIBLE : View.INVISIBLE);
        mAdapter.setMovieList(movieList);
    }

    private List<Movie> queryFavorites() {
        List<Movie> movieList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                FavoriteMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null) {
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
            cursor.close();
        }
        return movieList;
    }

    private void setGridColumns() {
        int orientation = getResources().getConfiguration().orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                gridColumns = 2;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                gridColumns = 3;
                break;
            default:
                gridColumns = DEFAULT_GRID_COLUMNS;
                break;
        }
    }
}
