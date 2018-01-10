package com.sfotakos.themovielist.movie_details;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sfotakos.themovielist.ConnectivityReceiver;
import com.sfotakos.themovielist.FavoritesActivity;
import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.general.IErrorMessages;
import com.sfotakos.themovielist.general.NetworkUtils;
import com.sfotakos.themovielist.general.data.MovieListContract.FavoriteMovieEntry;
import com.sfotakos.themovielist.general.model.Movie;
import com.sfotakos.themovielist.databinding.ActivityDetailBinding;
import com.sfotakos.themovielist.movie_details.adapter.DetailsPageAdapter;
import com.sfotakos.themovielist.movie_list.MainActivity;

import java.security.InvalidParameterException;

@SuppressWarnings("FieldCanBeLocal")
public class DetailsActivity extends AppCompatActivity
        implements ConnectivityReceiver.IConnectivityChange,
        IErrorMessages {

    public static final String MOVIE_DATA_EXTRA = "movie-data";
    public static final String MAIN_ACTIVITY_PARENT = "main-activity";
    public static final String FAVORITES_ACTIVITY_PARENT = "favorites-activity";

    public static final String CURRENT_FRAGMENT_TAG = "current-fragment";

    private ActivityDetailBinding mBinding;

    private boolean mFavorited = false;

    private Movie mMovie = null;
    private ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        setSupportActionBar(mBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(MOVIE_DATA_EXTRA)) {
                mMovie = intent.getParcelableExtra(MOVIE_DATA_EXTRA);
                if (mMovie == null) {
                    throw new RuntimeException("Movie data was not recovered properly");
                }

                //TODO Date object and proper DateFormatter parsing
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
            }
        }

        if (mMovie == null) {
            throw new RuntimeException("Movie data was not recovered properly");
        }

        setupViewPager(mBinding.vpDetailsTabs);
        mBinding.tlDetailsTabs.setupWithViewPager(mBinding.vpDetailsTabs);
        mBinding.tlDetailsTabs.setTabGravity(TabLayout.GRAVITY_FILL);
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
        connectivityReceiver = new ConnectivityReceiver(this);
        registerReceiver(connectivityReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_toolbar, menu);

        isFavorite();
        updateFavoritedIcon(menu.findItem(R.id.action_add_favorite));

        return true;
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
                        mFavorited = true;
                    }
                } else {
                    Uri uriToDelete = FavoriteMovieEntry.CONTENT_URI.buildUpon()
                            .appendPath(String.valueOf(mMovie.getId())).build();
                    int deleted = getContentResolver().delete(uriToDelete, null, null);
                    if (deleted > 0) {
                        mFavorited = false;
                    }
                }
                updateFavoritedIcon(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return this.getNavigationUpIntent();
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return this.getNavigationUpIntent();
    }

    @Override
    public void showErrorMessage(@Nullable String errorMessage) {
        mBinding.tvErrorMessage.setText(errorMessage);
        mBinding.tvErrorMessage.setVisibility(errorMessage == null ? View.GONE : View.VISIBLE);
    }

    private Intent getNavigationUpIntent() {
        Intent navigationIntent = null;

        String action = getIntent().getAction();
        if (action != null) {
            switch (action) {
                case MAIN_ACTIVITY_PARENT:
                    navigationIntent = new Intent(this, MainActivity.class);
                    navigationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    break;
                case FAVORITES_ACTIVITY_PARENT:
                    navigationIntent = new Intent(this, FavoritesActivity.class);
                    navigationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    break;

                default:
                    throw new InvalidParameterException("Unknown parent activity");
            }
        }
        return navigationIntent;
    }

    @Override
    public void connectivityChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
        if (NetworkUtils.hasConnection(this)) {
            if (currentFragment != null && currentFragment.isVisible()) {
                if (currentFragment instanceof ReviewsFragment) {
                    ((ReviewsFragment) currentFragment).fetchReviews();
                } else if (currentFragment instanceof TrailersFragment) {
                    ((TrailersFragment) currentFragment).fetchTrailers();
                }
            }
        }
    }

    private void setupViewPager(ViewPager vpDetailsTabs) {
        DetailsPageAdapter detailsPageAdapter = new DetailsPageAdapter(getSupportFragmentManager());
        detailsPageAdapter.addFrag(DetailsFragment.newInstance(mMovie),
                getResources().getString(R.string.nav_view_details));
        detailsPageAdapter.addFrag(ReviewsFragment.newInstance(mMovie.getId()),
                getResources().getString(R.string.nav_view_reviews));
        detailsPageAdapter.addFrag(TrailersFragment.newInstance(mMovie.getId()),
                getResources().getString(R.string.nav_view_trailers));
        vpDetailsTabs.setAdapter(detailsPageAdapter);
    }

    private void updateFavoritedIcon(MenuItem item) {
        if (mFavorited) {
            item.setIcon(R.drawable.ic_favorite_white);
        } else {
            item.setIcon(R.drawable.ic_favorite_border_white);
        }
    }

    private void isFavorite() {
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
}

