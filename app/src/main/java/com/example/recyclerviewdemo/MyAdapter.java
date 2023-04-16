package com.example.recyclerviewdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public static final int HEADER_ITEM = 0;  // 列表头
    public static final int SUB_ITEM = 1;  // 列表项
    private final List<Group> groupList;  // 所有 Group 所构成的 List
    private List<ItemInfo> itemInfoList;  // 所有 Item 的信息 所构成的 List
    private final Context context;

    public MyAdapter(@NonNull Context context, List<Group> groupList)
    {
        this.context = context;
        this.groupList = groupList;
        calculateItemInfoList();
    }

    public List<Group> getGroupList()
    {
        return groupList;
    }

    public int getGroupIndex(int position)
    {
        return itemInfoList.get(position).groupIndex;
    }

    public int getSubItemIndex(int position)
    {
        return itemInfoList.get(position).subItemIndex;
    }

    /**
     * 列表头点击事件
     */
    public void onHeaderClick(Group group)
    {
        group.setExpanded(!group.isExpanded());  // 将分组的isExpanded状态取反
        notifyDataSetChanged();  // 更新视图（待修改）
        calculateItemInfoList();  // 重新计算所有 Item 的信息
    }

    /**
     * 计算所有 Item 的信息，并更新到私有属性 itemInfoList 上
     */
    private void calculateItemInfoList()
    {
        itemInfoList = new ArrayList<>();
        for (int groupIndex = 0; groupIndex < groupList.size(); ++groupIndex)
        {
            // 记录列表头
            itemInfoList.add(new ItemInfo(groupIndex, HEADER_ITEM, -1));

            // 记录对应的列表项
            Group group = groupList.get(groupIndex);
            if (group.isExpanded())
            {
                for (int subItemIndex = 0; subItemIndex < group.getItemCount() - 1; ++subItemIndex)
                    itemInfoList.add(new ItemInfo(groupIndex, SUB_ITEM, subItemIndex));
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return itemInfoList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return itemInfoList.get(position).itemType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (viewType == HEADER_ITEM)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.header_recycler_view, parent, false);
            return new HeaderViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_view, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ItemInfo itemInfo = itemInfoList.get(position);  // 获取当前 position 对应的 Item 的信息
        Group group = groupList.get(itemInfo.groupIndex);  // 获取当前 Item 对应的 Group
        if (itemInfo.itemType == HEADER_ITEM)
        {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.bind(group);
        }
        else
        {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.bind(group.getSubItem(itemInfo.subItemIndex));
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView date;
        private final TextView income;
        private final TextView expense;
        private final ImageView toggle;

        public HeaderViewHolder(View itemView)
        {
            super(itemView);
            date = itemView.findViewById(R.id.header_date);
            income = itemView.findViewById(R.id.header_income);
            expense = itemView.findViewById(R.id.header_expense);
            toggle = itemView.findViewById(R.id.header_button);
            itemView.setOnClickListener(this);   // 整个HeaderViewHolder的点击事件绑定
        }

        public void bind(Group group)
        {
            HeaderItem headerItem = group.getHeaderItem();
            date.setText(headerItem.getDate());
            income.setText(String.format(Locale.getDefault(), "+%.2f", headerItem.getIncome()));
            expense.setText(String.format(Locale.getDefault(), "-%.2f", headerItem.getExpense()));
            toggle.setImageResource(group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more);
        }

        @Override
        public void onClick(View view)
        {
            int position = getAdapterPosition();  // 获取ViewHolder的位置
            Group group = groupList.get(itemInfoList.get(position).groupIndex);  // 获取对应的 Group
            onHeaderClick(group);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView time;
        private final TextView name;
        private final TextView amount;

        public ItemViewHolder(View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.item_time);
            name = itemView.findViewById(R.id.item_name);
            amount = itemView.findViewById(R.id.item_amount);
            itemView.setOnClickListener(this);   // 整个ItemViewHolder的点击事件绑定
        }

        public void bind(SubItem subItem)
        {
            time.setText(subItem.getTime());
            name.setText(subItem.getName());
            amount.setText(String.format(Locale.getDefault(), "%.2f", subItem.getAmount()));
        }

        @Override
        public void onClick(View view)
        {
            Log.d("test", "onClick: " + amount.getText());
        }
    }

    private static class ItemInfo
    {
        private final int groupIndex;
        private final int itemType;
        private final int subItemIndex;

        /**
         * ItemInfo 的构造函数，用于保存一个 Item 的信息
         *
         * @param groupIndex   当前 Item 属于第几个 Group，从 0 开始计数
         * @param itemType     当前 Item 属于 列表头类型 还是 列表项类型
         * @param subItemIndex 当前 Item 属于 当前 Group 的第几个列表项  // 列表头值为 -1，列表项从 0 开始计数
         */
        public ItemInfo(int groupIndex, int itemType, int subItemIndex)
        {
            this.groupIndex = groupIndex;
            this.itemType = itemType;
            this.subItemIndex = subItemIndex;
        }
    }
}