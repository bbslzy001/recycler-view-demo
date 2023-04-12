package com.example.recyclerviewdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.recycler_view);

        // 将假数据绑定到Adapter上
        adapter = new MyAdapter(this, getFakeData());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisiblePosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();
                List<Integer> offsetList = initOffsetList(adapter.getDataList(), adapter.getItemCount());
                List<Integer> headerPositionList = initHeaderPosition(adapter.getDataList(), adapter.getItemCount());
                int headerPosition = offsetList.get(firstVisiblePosition);
                for (int position : headerPositionList)
                {
                    MyAdapter.HeaderViewHolder headerViewHolder = (MyAdapter.HeaderViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                    if (headerViewHolder != null)
                    {
                        Log.d("test", "not null");
                        if (position == headerPosition)
                        {
                            headerViewHolder.itemView.setTranslationY(Math.max(0, recyclerView.getPaddingTop() - headerViewHolder.itemView.getTop()));
                        }
                        else headerViewHolder.itemView.setTranslationY(0);
                    }
                    else {
                        Log.d("test", "null");
                    }
                }
            }
        });

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 31);
    }

    private List<Group> getFakeData()
    {
        List<Group> dataList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
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
        return dataList;
    }

    private List<Integer> initOffsetList(List<Group> list, int allCount)
    {
        List<Integer> offsetList = new ArrayList<>();
        int headerIndex = -1;
        int itemCount = 0;
        for (int position = 0; position < allCount; ++position)
        {
            int count = 0;
            for (Group group : list)
            {
                if (position == count)
                {
                    ++headerIndex;
                    headerIndex += itemCount;
                    itemCount = 0;
                    break;
                }
                count++;
                if (group.isExpanded())
                {
                    int size = group.getItemCount();
                    if (position < count + size - 1)
                    {
                        ++itemCount;
                        break;
                    }
                    count += size - 1;
                }
            }
            offsetList.add(headerIndex);
        }
        return offsetList;
    }

    private List<Integer> initHeaderPosition(List<Group> list, int allCount)
    {
        List<Integer> headerPositionList = new ArrayList<>();
        int headerIndex = -1;
        int itemCount = 0;
        for (int position = 0; position < allCount; ++position)
        {
            int count = 0;
            for (Group group : list)
            {
                if (position == count)
                {
                    ++headerIndex;
                    headerIndex += itemCount;
                    headerPositionList.add(headerIndex);
                    itemCount = 0;
                    break;
                }
                count++;
                if (group.isExpanded())
                {
                    int size = group.getItemCount();
                    if (position < count + size - 1)
                    {
                        ++itemCount;
                        break;
                    }
                    count += size - 1;
                }
            }
        }
        return headerPositionList;
    }
}