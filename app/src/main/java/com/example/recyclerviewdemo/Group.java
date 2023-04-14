package com.example.recyclerviewdemo;

import java.util.List;

/**
 * 分组类，用于存储分组数据
 */
public class Group
{
    private final HeaderData headerData;
    private final List<ItemData> itemDataList;
    private boolean isExpanded = false;  // 是否展开

    public Group(HeaderData headerData, List<ItemData> itemDataList)
    {
        this.headerData = headerData;
        this.itemDataList = itemDataList;
    }

    public int getItemCount()
    {
        return itemDataList.size() + 1; // 分组标题占据一项
    }

    public HeaderData getHeaderData()
    {
        return headerData;
    }

    public ItemData getItemData(int position)
    {
        return itemDataList.get(position - 1); // 减去分组标题项
    }

    public boolean isExpanded()
    {
        return isExpanded;
    }

    public void setExpanded(boolean expanded)
    {
        isExpanded = expanded;
    }
}