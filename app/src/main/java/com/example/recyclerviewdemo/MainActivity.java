package com.example.recyclerviewdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

        initRecyclerView();
    }

    /**
     * 初始化 RecyclerView
     */
    private void initRecyclerView()
    {
        recyclerView = findViewById(R.id.recycler_view);

        adapter = new MyAdapter(this, getFakeData());  // 将假数据绑定到Adapter上
        recyclerView.setAdapter(adapter);  // 将Adapter绑定到RecyclerView上
        MyItemDecoration itemDecoration = new MyItemDecoration(this, adapter);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addOnItemTouchListener(itemDecoration);
    }

    /**
     * 生成假数据
     */
    private List<Group> getFakeData()
    {
        List<Group> dataList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Random random = new Random();
        double totalIncome, totalExpense;
        for (int i = 0; i < 10; i++)
        {
            String dateStr = sdf.format(new Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000L));
            List<SubItem> subItemList = new ArrayList<>();
            int transactionCount = random.nextInt(5) + 1;
            totalIncome = 0;
            totalExpense = 0;
            for (int j = 0; j < transactionCount; j++)
            {
                double amount = random.nextDouble() * 2000 - 1000; // 金额有正有负
                totalIncome += amount >= 0 ? amount : 0;
                totalExpense += amount < 0 ? -amount : 0;
                SubItem subItem = new SubItem("16:00:00", "Item " + (j + 1), amount);
                subItemList.add(subItem);
            }
            HeaderItem headerItem = new HeaderItem(dateStr, totalIncome, totalExpense);
            dataList.add(new Group(headerItem, subItemList));
        }
        return dataList;
    }
}