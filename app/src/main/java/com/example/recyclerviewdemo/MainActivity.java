package com.example.recyclerviewdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnGroupHeaderClickListener
{
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Group> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建假数据
        dataList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Random random = new Random();
        double totalIncome, totalExpense;
        for (int i = 0; i < 10; i++)
        {
            String dateStr = sdf.format(new Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000L));
            List<ItemData> itemDataList = new ArrayList<>();
            int transactionCount = random.nextInt(5) + 1;
            totalIncome = 0;
            totalExpense = 0;
            for (int j = 0; j < transactionCount; j++)
            {
                double amount = random.nextDouble() * 2000 - 1000; // 金额有正有负
                totalIncome += amount >= 0 ? amount : 0;
                totalExpense += amount < 0 ? -amount : 0;
                ItemData itemData = new ItemData("16:00:00", "Item " + (j + 1), amount);
                itemDataList.add(itemData);
            }
            HeaderData headerData = new HeaderData(dateStr, totalIncome, totalExpense);
            dataList.add(new Group(headerData, itemDataList));
        }

        // 将假数据绑定到Adapter上
        adapter = new MyAdapter(this, dataList);
        adapter.setOnGroupHeaderClickListener(this);  // 设置OnGroupHeaderClickListener的实例

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        adapter.setRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                int count = 0;
                for (Group group : adapter.getDataList())
                {
                    if (group.isExpanded())
                    {
                        int headerPosition = count;
                        int firstItemPosition = headerPosition + 1;
                        int lastItemPosition = firstItemPosition + group.getItemCount() - 2;
                        if (firstVisiblePosition >= firstItemPosition && lastVisiblePosition <= lastItemPosition)
                        { // 当前组的所有项都在可见区域内
                            MyAdapter.HeaderViewHolder headerViewHolder = adapter.getHeaderViewHolder(headerPosition);
                            if (headerViewHolder != null)
                            {
                                // 如果当前组的HeaderViewHolder存在，则设置HeaderViewHolder的悬浮状态为true
                                headerViewHolder.itemView.setTranslationY(Math.max(0, recyclerView.getPaddingTop() - headerViewHolder.itemView.getTop()));
                            }
                            return;
                        }
                        else if (firstVisiblePosition <= firstItemPosition && lastVisiblePosition >= firstItemPosition)
                        { // 当前组的第一项在可见区域内
                            MyAdapter.HeaderViewHolder headerViewHolder = adapter.getHeaderViewHolder(headerPosition);
                            if (headerViewHolder != null)
                            {
                                // 如果当前组的HeaderViewHolder存在，则设置HeaderViewHolder的悬浮状态为true
                                headerViewHolder.itemView.setTranslationY(Math.max(0, recyclerView.getPaddingTop() - headerViewHolder.itemView.getTop()));
                            }
                        }
                        else if (firstVisiblePosition <= lastItemPosition && lastVisiblePosition >= lastItemPosition)
                        { // 当前组的最后一项在可见区域内
                            MyAdapter.HeaderViewHolder headerViewHolder = adapter.getHeaderViewHolder(headerPosition);
                            if (headerViewHolder != null)
                            {
                                // 如果当前组的HeaderViewHolder存在，则设置HeaderViewHolder的悬浮状态为false
                                headerViewHolder.itemView.setTranslationY(0);
                            }
                        }
                    }
                    count++;
                    if (group.isExpanded())
                    {
                        int size = group.getItemCount();
                        count += size - 1;
                    }
                }
            }
        });
    }

    @Override
    public void onGroupHeaderClick(int groupPosition)
    {
        // 当组标题被点击时，滚动到该组的起始位置
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(groupPosition, 0);
    }
}