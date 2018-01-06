package com.sfotakos.themovielist.movie_list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sfotakos.themovielist.movie_details.DetailsActivity;
import com.sfotakos.themovielist.FavoritesActivity;
import com.sfotakos.themovielist.general.model.Movie;
import com.sfotakos.themovielist.movie_list.adapter.MarginItemDecoration;
import com.sfotakos.themovielist.movie_list.adapter.MovieListAdapter;
import com.sfotakos.themovielist.movie_list.model.MovieListRequest;
import com.sfotakos.themovielist.movie_list.model.MovieListResponse;
import com.sfotakos.themovielist.general.NetworkUtils;
import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.MovieItemClickListener {

    public static final String SCROLL_STATE_KEY = "scroll-state";

    private static final int DEFAULT_GRID_COLUMNS = 2;
    private static final int DEFAULT_PAGE = 1;

    private boolean isSortingByPopularity = true;

    private ActivityMainBinding mBinding;

    private MovieListAdapter mAdapter;

    private Parcelable scrollState;
    private ConnectivityReceiver connectivityReceiver;

    private int gridColumns = DEFAULT_GRID_COLUMNS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_movie_clapboard, null));
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mAdapter = new MovieListAdapter(this);

        // Margins for item decorator
        int marginInPixels = getResources().getDimensionPixelSize(R.dimen.movie_item_margin);
        setGridColumns();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, gridColumns);
        mBinding.rvMovies.setLayoutManager(layoutManager);

        mBinding.rvMovies.addItemDecoration(new MarginItemDecoration(marginInPixels, gridColumns));
        mBinding.rvMovies.setAdapter(mAdapter);

        fetchMovies();
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

    private void showMovieList() {
        mBinding.tvErrorMessage.setVisibility(View.GONE);
        mBinding.rvMovies.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String errorMessage) {
        mBinding.tvErrorMessage.setText(errorMessage);
        mBinding.tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void fetchMovies() {
        if (NetworkUtils.hasConnection(this)) {
            MovieListRequest movieListRequest = new MovieListRequest(DEFAULT_PAGE);
            new FetchMovies().execute(movieListRequest);
        } else {
            showErrorMessage(getResources().getString(R.string.error_no_connectivity));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort:
                //Switch between popularity and rating sorting type and update text accordingly.
                if (isSortingByPopularity) {
                    item.setTitle(getResources().getString(R.string.action_sortByRating));
                    isSortingByPopularity = false;
                } else {
                    item.setTitle(getResources().getString(R.string.action_sortByPopularity));
                    isSortingByPopularity = true;
                }

                mAdapter.setMovieList(null);
                fetchMovies();
                return true;

            case R.id.action_favorites:
                Intent favoritesActivityIntent =
                        new Intent(this, FavoritesActivity.class);
                startActivity(favoritesActivityIntent);

                return true;

            case R.id.action_about:

                Resources resources = getResources();

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
                builder.setTitle(resources.getString(R.string.dialog_about_title));
                builder.setMessage(resources.getString(R.string.dialog_about_content));
                builder.setPositiveButton(resources.getString(android.R.string.ok), null);
                builder.show();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailsActivityIntent = new Intent(this, DetailsActivity.class);
        detailsActivityIntent.putExtra(DetailsActivity.MOVIE_DATA_EXTRA, movie);
        detailsActivityIntent.setAction(DetailsActivity.MAIN_ACTIVITY_PARENT);
        startActivity(detailsActivityIntent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            scrollState = savedInstanceState.getParcelable(SCROLL_STATE_KEY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        persistScrollState();
        outState.putParcelable(SCROLL_STATE_KEY, scrollState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, filter);
    }

    private void persistScrollState() {
        RecyclerView.LayoutManager layoutManager = mBinding.rvMovies.getLayoutManager();
        scrollState = layoutManager.onSaveInstanceState();
    }

    private void restoreScrollState() {
        if (scrollState != null) {
            RecyclerView.LayoutManager layoutManager = mBinding.rvMovies.getLayoutManager();
            layoutManager.onRestoreInstanceState(scrollState);
        }
    }

    private class FetchMovies extends AsyncTask<MovieListRequest, Void, MovieListResponse> {

        @Override
        protected void onPreExecute() {
            mBinding.tvErrorMessage.setVisibility(View.GONE);
            mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected MovieListResponse doInBackground(MovieListRequest... movieListRequests) {

            MovieListRequest movieListRequest = movieListRequests[0];
            URL requestURL;
            if (isSortingByPopularity) {
                requestURL = movieListRequest.buildPopularMovieRequestURL();
            } else {
                requestURL = movieListRequest.buildTopRatedMovieRequestURL();
            }

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestURL);
                return new MovieListResponse(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieListResponse movieListResponse) {
            mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieListResponse != null) {
                showMovieList();
                mAdapter.setMovieList(movieListResponse.getMovieList());
                restoreScrollState();
            } else {
                showErrorMessage(getResources().getString(R.string.error_default));
            }
        }
    }

    // Deal with connectivity changes
    private class ConnectivityReceiver extends BroadcastReceiver implements Serializable {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (NetworkUtils.hasConnection(context)) {
                fetchMovies();
            } else {
                showErrorMessage(getResources().getString(R.string.error_no_connectivity));
            }
        }
    }
}
