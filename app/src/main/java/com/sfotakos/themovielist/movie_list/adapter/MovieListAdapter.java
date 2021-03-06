package com.sfotakos.themovielist.movie_list.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.general.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spyridion on 11/10/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private List<Movie> movieList = new ArrayList<>();

    public interface MovieItemClickListener {
        void onClick(Movie movie);
    }

    private final MovieItemClickListener mClickHandler;

    public MovieListAdapter(MovieItemClickListener mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    public void setMovieList(List<Movie> movieList){
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Movie movie = movieList.get(position);

        holder.loadImage(movie.getPosterPath());
    }

    @Override
    public int getItemCount() {
        return movieList == null ? 0 : movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mMoviePoster;

        MovieViewHolder(View itemView) {
            super(itemView);

            mMoviePoster = itemView.findViewById(R.id.iv_movie_poster);

            itemView.setOnClickListener(this);
        }

        void loadImage(String url) {
            Picasso.with(mMoviePoster.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_movie_clapboard)
                    .into(mMoviePoster);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(movieList.get(getAdapterPosition()));
        }
    }
}
