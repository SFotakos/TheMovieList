package com.sfotakos.popularmovies.MovieList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sfotakos.popularmovies.DetailActivity;
import com.sfotakos.popularmovies.MovieList.Model.DiscoverMovieRequest;
import com.sfotakos.popularmovies.MovieList.Model.DiscoverMovieResponse;
import com.sfotakos.popularmovies.MovieList.Model.Movie;
import com.sfotakos.popularmovies.NetworkUtils;
import com.sfotakos.popularmovies.R;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.MovieItemClickListener {

    public static final String MOVIE_DATA = "MovieData";

    private static final int GRID_COLUMNS = 2;
    private static final int DEFAULT_PAGE = 1;

    private RecyclerView mMoviesList;
    private MovieListAdapter mAdapter;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;

    private static final String mSortDescPopularity = DiscoverMovieRequest.getSortBy(
            DiscoverMovieRequest.SortTypes.POPULARITY,
            DiscoverMovieRequest.Order.DESCENDING);

    private static final String mSortDescRating = DiscoverMovieRequest.getSortBy(
            DiscoverMovieRequest.SortTypes.VOTE_AVERAGE,
            DiscoverMovieRequest.Order.DESCENDING);

    private String mSortType = mSortDescPopularity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_movie_clapboard, null));
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        mAdapter = new MovieListAdapter(this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, GRID_COLUMNS);

        mMoviesList.setLayoutManager(layoutManager);

        // Fixing double margins
        int marginInPixels = getResources().getDimensionPixelSize(R.dimen.movie_item_margin);
        mMoviesList.addItemDecoration(new MarginItemDecoration(marginInPixels, GRID_COLUMNS));

        mMoviesList.setAdapter(mAdapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectivityReceiver(), filter);

        fetchMovies();
    }

    private void showMovieList() {
        mErrorMessage.setVisibility(View.GONE);
        mMoviesList.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String errorMessage) {
        mErrorMessage.setText(errorMessage);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void fetchMovies() {
        if (NetworkUtils.hasConnection(this)) {
            DiscoverMovieRequest discoverMovieRequest = new DiscoverMovieRequest(mSortType, DEFAULT_PAGE);
            new FetchMovies().execute(discoverMovieRequest);
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
                if (mSortType.equals(mSortDescPopularity)) {
                    item.setTitle(getResources().getString(R.string.action_sortByPopularity));
                    mSortType = mSortDescRating;
                } else if (mSortType.equals(mSortDescRating)) {
                    item.setTitle(getResources().getString(R.string.action_sortByRating));
                    mSortType = mSortDescPopularity;
                }

                mAdapter.setMovieList(null);
                fetchMovies();
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
        Intent detailActivityIntent = new Intent(this, DetailActivity.class);
        detailActivityIntent.putExtra(MOVIE_DATA, movie);
        startActivity(detailActivityIntent);
    }

    private class FetchMovies extends AsyncTask<DiscoverMovieRequest, Void, DiscoverMovieResponse> {

        @Override
        protected void onPreExecute() {
            mErrorMessage.setVisibility(View.GONE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected DiscoverMovieResponse doInBackground(DiscoverMovieRequest... discoverMovieRequests) {

            DiscoverMovieRequest discoverMovieRequest = discoverMovieRequests[0];
            URL discoveryRequestURL = discoverMovieRequest.buildMovieDiscoveryRequestURL();

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(discoveryRequestURL);

                return new DiscoverMovieResponse(jsonResponse);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DiscoverMovieResponse discoverMovieResponse) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (discoverMovieResponse != null) {
                showMovieList();
                mAdapter.setMovieList(discoverMovieResponse.getMovieList());
            } else {
                showErrorMessage(getResources().getString(R.string.error_default));
            }
        }
    }

    private class ConnectivityReceiver extends BroadcastReceiver {
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
