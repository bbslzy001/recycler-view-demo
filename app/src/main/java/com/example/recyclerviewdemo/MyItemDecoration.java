package com.example.recyclerviewdemo;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyItemDecoration extends RecyclerView.ItemDecoration {
    private int headerHeight;
    private List<Integer> headerPositionList;
    private MyAdapter adapter;

    public MyItemDecoration(int headerHeight, List<Integer> headerPositionList, MyAdapter adapter) {
        this.headerHeight = headerHeight;
        this.headerPositionList = headerPositionList;
        this.adapter = adapter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (headerPositionList.contains(position)) {
            outRect.top = headerHeight;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (headerPositionList.contains(position)) {
                View headerView = adapter.onCreateViewHolder(parent, adapter.getItemViewType(position)).itemView;
                adapter.onBindViewHolder((MyAdapter.HeaderViewHolder) headerView.getTag(), position);
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                int top = child.getTop() - headerHeight;
                int bottom = child.getTop();
                c.save();
                c.translate(0, top);
                headerView.layout(left, 0, right, headerHeight);
                headerView.draw(c);
                c.restore();
            }
        }
    }
}