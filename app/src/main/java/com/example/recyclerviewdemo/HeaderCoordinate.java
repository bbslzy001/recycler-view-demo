package com.example.recyclerviewdemo;

import android.view.View;

public class HeaderCoordinate
{
    private final int headerTop;
    private final int headerBottom;
    private final int headerLeft;
    private final int headerRight;

    public HeaderCoordinate(View headerView)
    {
        this.headerTop = headerView.getTop();
        this.headerBottom = headerView.getBottom();
        this.headerLeft = headerView.getLeft();
        this.headerRight = headerView.getRight();
    }

    public int getHeaderTop()
    {
        return headerTop;
    }

    public int getHeaderBottom()
    {
        return headerBottom;
    }

    public int getHeaderLeft()
    {
        return headerLeft;
    }

    public int getHeaderRight()
    {
        return headerRight;
    }
}