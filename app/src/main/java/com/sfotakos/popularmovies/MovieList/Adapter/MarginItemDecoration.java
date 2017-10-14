package com.sfotakos.popularmovies.MovieList.Adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by spyridion on 13/10/17.
 */

//Extended class from https://stackoverflow.com/a/28533234 to support columns

public class MarginItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpace;
    private int mColumns = 1;

    public MarginItemDecoration(int space, int columns) {
        this.mSpace = space;
        this.mColumns = columns;
    }

    public MarginItemDecoration(int space) {
        this.mSpace = space;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.right = mSpace;
        outRect.bottom = mSpace;

        // Add top margin only for the first item of each column to avoid double space between items
        if (parent.getChildLayoutPosition(view) < mColumns) {
            outRect.top = mSpace;
        } else {
            outRect.top = 0;
        }

        // Add left margin only for items in the first column to avoid double space between items
        if (parent.getChildLayoutPosition(view) % mColumns == 0){
            outRect.left = mSpace;
        } else {
            outRect.left = 0;
        }
    }
}