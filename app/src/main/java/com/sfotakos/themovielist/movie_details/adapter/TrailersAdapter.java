package com.sfotakos.themovielist.movie_details.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sfotakos.themovielist.R;
import com.sfotakos.themovielist.general.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spyridion on 05/01/18.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private List<Trailer> trailerList = new ArrayList<>();
    private TrailerItemClickListener mClickHandler;

    public interface TrailerItemClickListener {
        void onClick(Trailer trailer);
    }

    public TrailersAdapter(TrailerItemClickListener mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    public void setTrailerList(List<Trailer> trailerList){
        this.trailerList = trailerList;
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);
        holder.mTrailerName.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return trailerList == null ? 0 : trailerList.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mTrailerName;

        TrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerName = itemView.findViewById(R.id.tv_trailer_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(trailerList.get(getAdapterPosition()));
        }
    }
}
