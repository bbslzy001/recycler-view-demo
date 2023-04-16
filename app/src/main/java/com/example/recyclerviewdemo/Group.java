package com.example.recyclerviewdemo;

import java.util.List;

/**
 * 分组类，用于存储分组数据
 */
public class Group
{
    private final HeaderItem headerItem;
    private final List<SubItem> subItemList;
    private boolean isExpanded = false;  // 是否展开

    public Group(HeaderItem headerItem, List<SubItem> subItemList)
    {
        this.headerItem = headerItem;
        this.subItemList = subItemList;
    }

    public int getItemCount()
    {
        return subItemList.size() + 1; // 分组标题占据一项
    }

    public HeaderItem getHeaderItem()
    {
        return headerItem;
    }

    public SubItem getSubItem(int position)
    {
        return subItemList.get(position);
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