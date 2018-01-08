package com.sfotakos.themovielist.movie_details.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.general.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spyridion on 05/01/18.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviewList = new ArrayList<>();

    public void setReviewList(List<Review> reviewList){
        this.reviewList = reviewList;
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.mReviewAuthor.setText(review.getAuthor());
        holder.mReviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView mReviewAuthor;
        TextView mReviewContent;

        ReviewViewHolder(View itemView) {
            super(itemView);

            mReviewAuthor = itemView.findViewById(R.id.tv_review_author);
            mReviewContent = itemView.findViewById(R.id.tv_review_content);
        }
    }
}
