package com.example.recyclerviewdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

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

        // 将假数据绑定到Adapter上
        adapter = new MyAdapter(this, getFakeData());

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new MyItemDecoration(this, adapter));
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
}