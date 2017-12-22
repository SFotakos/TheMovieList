package com.sfotakos.themovielist;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.sfotakos.themovielist.MovieList.Adapter.MarginItemDecoration;
import com.sfotakos.themovielist.MovieList.Adapter.MovieListAdapter;
import com.sfotakos.themovielist.MovieList.Model.Movie;
import com.sfotakos.themovielist.MovieList.Model.MovieResponse;
import com.sfotakos.themovielist.databinding.ActivityFavoritesBinding;

public class FavoritesActivity extends AppCompatActivity implements MovieListAdapter.MovieItemClickListener {

    private static final int GRID_COLUMNS = 2;

    private ActivityFavoritesBinding mBinding;

    private MovieListAdapter mAdapter;

    //TODO populate movie list with favorites
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
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
    }


    @Override
    public void onClick(Movie movie) {
        Toast.makeText(this, movie.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
