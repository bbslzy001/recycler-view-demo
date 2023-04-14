package com.example.recyclerviewdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MotionEvent;

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
    private HeaderInfo headerInfo;

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
        recyclerView.addItemDecoration(new MyItemDecoration(this, adapter));
        recyclerView.addOnItemTouchListener(new MyItemTouchListener());
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

    private class MyItemTouchListener implements RecyclerView.OnItemTouchListener
    {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
        {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
            int firstVisiblePosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();

            int firstVisibleViewType = adapter.getItemViewType(firstVisiblePosition); // 获取第一个可见的item的类型
            if (firstVisibleViewType == MyAdapter.VIEW_TYPE_HEADER)  // 如果是组的头部
            {
                if (headerInfo == null) headerInfo = new HeaderInfo(rv.getChildAt(firstVisiblePosition));
            }

            // 获取触摸事件的坐标
            // 参数 0 表示获取第一个触摸点的坐标，以避免数组越界异常
            // 注意：下面的代码仅适用于单点触摸事件
            float x = e.getX(0);
            float y = e.getY(0);
            // 获取触摸事件的动作
            int action = e.getAction();
            // 如果是按下动作
            if (action == MotionEvent.ACTION_DOWN)
            {
                // 判断是否点击了HeaderView区域
                if (x >= headerInfo.getHeaderLeft() && x <= headerInfo.getHeaderRight() && y >= headerInfo.getHeaderTop() && y <= headerInfo.getHeaderBottom())
                {
                    // 处理点击事件
                    List<Integer> offsetList = adapter.getGroupIndexList();  // 获取所有 view 对应的 group 下标
                    Group group = adapter.getDataList().get(offsetList.get(firstVisiblePosition)); // 获取当前组对象
                    adapter.onHeaderClick(group);
                    // 拦截触摸事件，不再传递给子View
                    return true;
                }
            }
            // 否则，按照默认的逻辑处理触摸事件
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
        {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {
        }
    }
}