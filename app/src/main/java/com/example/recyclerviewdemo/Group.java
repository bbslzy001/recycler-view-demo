package com.example.recyclerviewdemo;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 分组类，用于存储分组数据
 */
public class Group
{
    private final HeaderData mHeaderData;
    private final List<ItemData> mItemDataList;
    private final AtomicBoolean isExpanded = new AtomicBoolean(false);  // 添加变量

    public Group(HeaderData headerData, List<ItemData> itemDataList)
    {
        mHeaderData = headerData;
        mItemDataList = itemDataList;
    }

    public int getItemCount()
    {
        return mItemDataList.size() + 1; // 分组标题占据一项
    }

    public boolean isHeader(int position)
    {
        return position == 0;
    }

    public HeaderData getHeaderData()
    {
        return mHeaderData;
    }

    public ItemData getItemData(int position)
    {
        return mItemDataList.get(position - 1); // 减去分组标题项
    }

    public List<ItemData> getItemDataList()
    {
        return mItemDataList;
    }

    public AtomicBoolean isExpanded()
    {
        return isExpanded;
    }

    public void setExpanded(boolean expanded)
    {
        isExpanded.set(expanded);
    }
}