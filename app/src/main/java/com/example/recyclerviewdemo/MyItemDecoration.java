package com.example.recyclerviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyItemDecoration extends RecyclerView.ItemDecoration implements RecyclerView.OnItemTouchListener
{
    private final MyAdapter adapter;
    private final Context context;
    private HeaderInfo headerInfo;
    private Group currentGroup;
    private View headerView;

    public MyItemDecoration(Context context, MyAdapter adapter)
    {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        if (state.isMeasuring() || adapter.getItemCount() == 0)  // 如果recyclerview正在进行布局前的准备工作或数据为空
        {
            return; // 直接返回，不做任何操作
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int firstVisiblePosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();

        int firstVisibleViewType = adapter.getItemViewType(firstVisiblePosition); // 获取第一个可见的item的类型
        if (firstVisibleViewType == MyAdapter.VIEW_TYPE_HEADER)  // 如果是列表头
        {
            if (headerInfo == null) headerInfo = new HeaderInfo(parent.getChildAt(firstVisiblePosition));
        }

        List<Integer> offsetList = adapter.getGroupIndexList();  // 获取所有 view 对应的 group 下标
        Group group = adapter.getDataList().get(offsetList.get(firstVisiblePosition)); // 获取当前组对象

        if (currentGroup == null || currentGroup != group)  // 更新列表头
        {
            currentGroup = group;
            headerView = getHeaderView(parent,currentGroup);
        }
        drawFloatingHeader(c, headerView);
    }

    /**
     * 创建一个新的 HeaderView对象，并将列表头内容绘制到该视图上
     */
    private View getHeaderView(RecyclerView parent, Group group)
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
        return headerView;
    }

    /**
     * 将 HeaderView 绘制到 RecyclerView 的画布上
     */
    private void drawFloatingHeader(Canvas c, View headerView)
    {
        // 将头部 View 绘制到 RecyclerView 的画布上
        int widthSpec = View.MeasureSpec.makeMeasureSpec(headerInfo.getHeaderRight() - headerInfo.getHeaderLeft(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(headerInfo.getHeaderBottom() - headerInfo.getHeaderTop(), View.MeasureSpec.EXACTLY);
        headerView.measure(widthSpec, heightSpec);
        headerView.layout(headerInfo.getHeaderLeft(), headerInfo.getHeaderTop(), headerInfo.getHeaderRight(), headerInfo.getHeaderBottom());
        headerView.draw(c);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
    {
        if(adapter.getItemCount() == 0) return false;  // 如果recyclerView数据为空

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
                //Objects.requireNonNull(layoutManager).scrollToPositionWithOffset(firstVisiblePosition, 0);  // 点击悬浮列表头，自动将recyclerview滚动到该列表头所在位置
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