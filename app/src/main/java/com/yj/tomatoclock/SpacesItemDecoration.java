package com.yj.tomatoclock;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int top;
    private int left;
    private int bottom;
    private int right;

    public SpacesItemDecoration(int space) {
        this.top=space;
        this.left=space;
        this.bottom=space;
        this.right=space;
    }

    public SpacesItemDecoration(int left, int top, int right, int bottom) {
        this.top=top;
        this.left=left;
        this.bottom=bottom;
        this.right=right;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right=right;
        outRect.bottom=bottom;
        outRect.left=left;
        outRect.top=top;

    }

}
