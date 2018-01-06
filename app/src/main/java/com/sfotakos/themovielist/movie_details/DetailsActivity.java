package com.sfotakos.themovielist.movie_details;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.sfotakos.themovielist.FavoritesActivity;
import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.general.NetworkUtils;
import com.sfotakos.themovielist.general.data.MovieListContract.FavoriteMovieEntry;
import com.sfotakos.themovielist.general.model.Movie;
import com.sfotakos.themovielist.databinding.ActivityDetailBinding;
import com.sfotakos.themovielist.movie_details.adapter.ReviewsAdapter;
import com.sfotakos.themovielist.movie_details.model.MovieReviewRequest;
import com.sfotakos.themovielist.movie_details.model.MovieReviewResponse;
import com.sfotakos.themovielist.movie_list.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.security.InvalidParameterException;

@SuppressWarnings("FieldCanBeLocal")
public class DetailsActivity extends AppCompatActivity {

    public static final String MOVIE_DATA_EXTRA = "movie-data";
    public static final String MAIN_ACTIVITY_PARENT = "main-activity";
    public static final String FAVORITES_ACTIVITY_PARENT = "favorites-activity";

    private Movie mMovie = null;

    private ActivityDetailBinding mBinding;



    private boolean mFavorited = false;

    //TODO Add customized layout for landscape orientation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

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



//                fetchReviews();
            }
        }

        if (mMovie == null) {
            throw new RuntimeException("Movie data was not recovered properly");
        }

        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.nav_details:
                                fragment = DetailsFragment.newInstance(mMovie);
                                break;

                            case R.id.nav_reviews:
                                fragment = ReviewsFragment.newInstance(mMovie.getId());
                                break;

                            case R.id.nav_trailers:
                                fragment = DetailsFragment.newInstance(mMovie);
                                break;

                            default:
                                throw new InvalidParameterException("Unknown navigation action");
                        }

                        try {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container_fl, fragment).commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                });
        mBinding.bottomNavigation.setSelectedItemId(R.id.nav_details);
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

    private void showErrorMessage(String errorMessage) {
        //mBinding.tvErrorMessage.setText(errorMessage);
        //mBinding.tvErrorMessage.setVisibility(View.VISIBLE);
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

            default:
                break;
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


    // Deal with connectivity changes
    private class ConnectivityReceiver extends BroadcastReceiver implements Serializable {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (NetworkUtils.hasConnection(context)) {

            } else {
                showErrorMessage(getResources().getString(R.string.error_no_connectivity));
            }
        }
    }


}
