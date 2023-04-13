package com.example.recyclerviewdemo;

import java.util.List;

/**
 * 分组类，用于存储分组数据
 */
public class Group
{
    private final HeaderData mHeaderData;
    private final List<ItemData> mItemDataList;
    private boolean isExpanded = false;  // 是否展开

    public Group(HeaderData headerData, List<ItemData> itemDataList)
    {
        mHeaderData = headerData;
        mItemDataList = itemDataList;
    }

    public int getItemCount()
    {
        return mItemDataList.size() + 1; // 分组标题占据一项
    }

    public HeaderData getHeaderData()
    {
        return mHeaderData;
    }

    public ItemData getItemData(int position)
    {
        return mItemDataList.get(position - 1); // 减去分组标题项
    }

    public boolean isExpanded()
    {
        return isExpanded;
    }

    public void setExpanded(boolean expanded)
    {
        isExpanded = expanded;
    }

    @Override
    public String toString()
    {
        return "Group{" +
                "mHeaderData=" + mHeaderData.toString() +
                ", mItemDataList=" + mItemDataList.toString() +
                ", isExpanded=" + isExpanded +
                '}';
    }
}