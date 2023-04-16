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
    public static final int VIEW_TYPE_HEADER = 0;  // 列表头
    public static final int VIEW_TYPE_ITEM = 1;  // 列表项
    private final List<Group> dataList;  // 所有组所构成的List
    private final Context context;

    public MyAdapter(@NonNull Context context, List<Group> dataList)
    {
        this.context = context;
        this.dataList = dataList;
    }

    public List<Group> getDataList()
    {
        return dataList;
    }

    @Override
    public int getItemCount()
    {
        int count = dataList.size();  // 统计列表头的数目
        for (Group group : dataList)
        {
            if (group.isExpanded())  // 如果该组处于展开装填
            {
                count += group.getItemCount() - 1;  // 统计对应的列表项的数目
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position)
    {
        int count = 0;  // 定义一个计数器
        for (Group group : dataList)  // 遍历所有组
        {
            if (position == count)  // 如果position等于计数器的值，则说明该位置是一个列表头
            {
                return VIEW_TYPE_HEADER;  // 返回列表头的视图类型
            }
            ++count;
            if (group.isExpanded())  // 如果该组处于展开状态
            {
                int size = group.getItemCount() - 1;  // 获取该组中列表项的数目
                if (position < count + size)  // 如果position在该组的范围内
                {
                    return VIEW_TYPE_ITEM;  // 返回列表项的视图类型
                }
                count += size;  // 计数器加上该组中列表项的数目
            }
        }
        throw new IllegalArgumentException("Invalid position");  // 如果没有找到对应的视图类型，则抛出异常
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_HEADER)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.header_recycler_view, parent, false);
            return new HeaderViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_view, parent, false);
            return new ItemViewHolder(view);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        int count = 0;
        for (Group group : dataList)
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
                int size = group.getItemCount() - 1;
                if (position < count + size)
                {
                    ItemViewHolder itemHolder = (ItemViewHolder) holder;
                    SubItem subItem = group.getItemData(position - count + 1);
                    itemHolder.bind(subItem);
                    break;
                }
                count += size;
            }
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
            HeaderItem headerItem = group.getHeaderData();
            date.setText(headerItem.getDate());
            income.setText(String.format(Locale.getDefault(), "+%.2f", headerItem.getIncome()));
            expense.setText(String.format(Locale.getDefault(), "-%.2f", headerItem.getExpense()));
            toggle.setImageResource(group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more);
        }

        @Override
        public void onClick(View view)
        {
            int position = getAdapterPosition();  // 获取ViewHolder的位置
            int count = 0;  // 计算当前分组在列表中的起始位置
            for (Group group : dataList)
            {
                if (position == count)
                {
                    onHeaderClick(group);
                    return;
                }
                count++;
                if (group.isExpanded())
                {
                    int size = group.getItemCount() - 1;
                    if (position < count + size) return;
                    count += size;
                }
            }
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

    /**
     * 列表头点击事件
     */
    public void onHeaderClick(Group group)
    {
        group.setExpanded(!group.isExpanded());  // 将分组的isExpanded状态取反
        notifyDataSetChanged();  // 更新视图
    }

    /**
     * 计算所有 view 对应的 group 下标
     */
    public List<Integer> getGroupIndexList()
    {
        int allCount = getItemCount();
        List<Integer> offsetList = new ArrayList<>();
        int headerIndex = -1;
        for (int position = 0; position < allCount; ++position)
        {
            int count = 0;
            for (Group group : dataList)
            {
                if (position == count)
                {
                    ++headerIndex;
                    break;
                }
                count++;
                if (group.isExpanded())
                {
                    int size = group.getItemCount() - 1;
                    if (position < count + size) break;
                    count += size;
                }
            }
            offsetList.add(headerIndex);
        }
        return offsetList;
    }
}