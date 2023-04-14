package com.example.recyclerviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyItemDecoration extends RecyclerView.ItemDecoration
{
    private final MyAdapter adapter;
    private final Context context;
    private HeaderInfo headerInfo;

    public MyItemDecoration(Context context, MyAdapter adapter)
    {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        if (state.isPreLayout())  // 如果recyclerview正在进行布局前的准备工作
        {
            return; // 直接返回，不做任何操作
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int firstVisiblePosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();

        int firstVisibleViewType = adapter.getItemViewType(firstVisiblePosition); // 获取第一个可见的item的类型
        if (firstVisibleViewType == MyAdapter.VIEW_TYPE_HEADER)  // 如果是组的头部
        {
            if (headerInfo == null) headerInfo = new HeaderInfo(parent.getChildAt(firstVisiblePosition));
        }

        List<Integer> offsetList = adapter.getGroupIndexList();  // 获取所有 view 对应的 group 下标
        Group group = adapter.getDataList().get(offsetList.get(firstVisiblePosition)); // 获取当前组对象

        drawHeader(c, parent, group);  // 在画布上绘制上一个组头部视图
    }

    private void drawHeader(Canvas c, RecyclerView parent, Group group)
    {
        // 创建一个新的 View 对象，并将头部内容绘制到该 View 上
        View headerView = LayoutInflater.from(context).inflate(R.layout.header_recycler_view, parent, false);
        TextView dateTextView = headerView.findViewById(R.id.header_date);
        TextView incomeTextView = headerView.findViewById(R.id.header_income);
        TextView expenseTextView = headerView.findViewById(R.id.header_expense);
        ImageView arrowImageView = headerView.findViewById(R.id.header_button);
        dateTextView.setText(group.getHeaderData().getDate());
        incomeTextView.setText(String.format(Locale.getDefault(), "+%.2f", group.getHeaderData().getIncome()));
        expenseTextView.setText(String.format(Locale.getDefault(), "-%.2f", group.getHeaderData().getExpense()));
        arrowImageView.setImageResource(group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more);

        // 将头部 View 绘制到 RecyclerView 的画布上
        int widthSpec = View.MeasureSpec.makeMeasureSpec(headerInfo.getHeaderRight() - headerInfo.getHeaderLeft(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(headerInfo.getHeaderBottom() - headerInfo.getHeaderTop(), View.MeasureSpec.EXACTLY);
        headerView.measure(widthSpec, heightSpec);
        headerView.layout(headerInfo.getHeaderLeft(), headerInfo.getHeaderTop(), headerInfo.getHeaderRight(), headerInfo.getHeaderBottom());
        headerView.draw(c);
    }
}