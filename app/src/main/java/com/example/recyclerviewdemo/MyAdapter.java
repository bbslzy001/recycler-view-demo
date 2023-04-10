package com.example.recyclerviewdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private final List<Group> mDataList;
    private final Context mContext;

    public MyAdapter(Context context, List<Group> dataList)
    {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public int getItemCount()
    {
        int count = 0;
        for (Group group : mDataList)
        {
            count++;  // 统计每个组的头部
            if (group.isExpanded())
            {
                count += group.getItemCount() - 1;  // 统计每个组展开后的项数
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position)
    {
        int count = 0;  // 定义一个计数器
        for (Group group : mDataList)  // 遍历数据列表中的每一个组
        {
            if (position == count)  // 如果当前位置等于计数器的值，则说明这是一个组的头部
            {
                return VIEW_TYPE_HEADER;  // 返回头部视图类型
            }
            ++count;
            if (group.isExpanded())  // 如果这个组是展开状态
            {
                int size = group.getItemCount();  // 获取该组中列表项的数量
                if (position < count + size - 1)  // 如果当前位置在该组的范围内
                {
                    return VIEW_TYPE_ITEM;  // 返回列表项视图类型
                }
                count += size - 1;  // 计数器加上该组中列表项的数量，再减去1
            }
        }
        throw new IllegalArgumentException("Invalid position " + position);  // 如果没有找到对应的视图类型，则抛出异常
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_HEADER)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.header_recycler_view, parent, false);
            return new HeaderViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_view, parent, false);
            return new ItemViewHolder(view);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        int count = 0;
        for (Group group : mDataList)
        {
            if (position == count)
            {
                HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                headerHolder.bind(group);
                break;
            }
            count++;
            if (group.isExpanded())
            {
                int size = group.getItemCount();
                if (position < count + size - 1)
                {
                    ItemViewHolder itemHolder = (ItemViewHolder) holder;
                    ItemData itemData = group.getItemData(position - count + 1);
                    itemHolder.bind(itemData);
                    break;
                }
                count += size - 1;
            }
        }
    }

    private void updateVisibility()
    {
        notifyDataSetChanged();
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
            HeaderData headerData = group.getHeaderData();
            date.setText(headerData.getDate());
            income.setText(String.format(Locale.getDefault(), "+%.2f", headerData.getIncome()));
            expense.setText(String.format(Locale.getDefault(), "-%.2f", headerData.getExpense()));
            toggle.setImageResource(group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more);
        }

        @Override
        public void onClick(View view)
        {
            int position = getAdapterPosition();  // 获取ViewHolder的位置
            int count = 0;  // 计算当前分组在列表中的起始位置
            for (Group group : mDataList)
            {
                if (position == count)
                {
                    group.setExpanded(!group.isExpanded());  // 将分组的isExpanded状态取反
                    updateVisibility();  // 更新视图
                    return;
                }
                count++;
                if (group.isExpanded())
                {
                    int size = group.getItemCount();
                    if (position < count + size - 1) return;
                    count += size - 1;
                }
            }
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder
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
        }

        public void bind(ItemData itemData)
        {
            time.setText(itemData.getTime());
            name.setText(itemData.getName());
            amount.setText(String.format(Locale.getDefault(), "%.2f", itemData.getAmount()));
        }
    }
}