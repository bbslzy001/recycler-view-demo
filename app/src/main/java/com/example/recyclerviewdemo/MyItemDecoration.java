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
    private ImageView arrowImageView;

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
            headerView = getHeaderView(parent, currentGroup);
        }
        drawFloatingHeader(c, headerView);
    }

    /**
     * 创建一个新的 HeaderView 对象，并将列表头内容绘制到该视图上
     */
    private View getHeaderView(RecyclerView parent, Group group)
    {
        View headerView = LayoutInflater.from(context).inflate(R.layout.header_recycler_view, parent, false);
        TextView dateTextView = headerView.findViewById(R.id.header_date);
        TextView incomeTextView = headerView.findViewById(R.id.header_income);
        TextView expenseTextView = headerView.findViewById(R.id.header_expense);
        arrowImageView = headerView.findViewById(R.id.header_button);
        dateTextView.setText(group.getHeaderData().getDate());
        incomeTextView.setText(String.format(Locale.getDefault(), "+%.2f", group.getHeaderData().getIncome()));
        expenseTextView.setText(String.format(Locale.getDefault(), "-%.2f", group.getHeaderData().getExpense()));
        arrowImageView.setImageResource(group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more);
        return headerView;
    }

    /**
     * 将 HeaderView 绘制到 RecyclerView 的画布上
     */
    private void drawFloatingHeader(Canvas canvas, View headerView)
    {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(headerInfo.getHeaderRight() - headerInfo.getHeaderLeft(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(headerInfo.getHeaderBottom() - headerInfo.getHeaderTop(), View.MeasureSpec.EXACTLY);
        headerView.measure(widthSpec, heightSpec);
        headerView.layout(headerInfo.getHeaderLeft(), headerInfo.getHeaderTop(), headerInfo.getHeaderRight(), headerInfo.getHeaderBottom());
        headerView.draw(canvas);
    }

    /**
     * 判断是否点击了悬浮列表头
     *
     * @param x      获取触摸事件的坐标
     * @param y      获取触摸事件的坐标
     * @param action 获取触摸事件的动作
     */
    private boolean isFloatingHeaderClicked(float x, float y, int action)
    {
        if (action != MotionEvent.ACTION_DOWN) return false;
        return x >= headerInfo.getHeaderLeft() && x <= headerInfo.getHeaderRight() && y >= headerInfo.getHeaderTop() && y <= headerInfo.getHeaderBottom();
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
    {
        if (adapter.getItemCount() == 0) return false;  // 如果recyclerView数据为空

        LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        int firstVisiblePosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();
        int firstVisibleViewType = adapter.getItemViewType(firstVisiblePosition); // 获取第一个可见的item的类型
        if (firstVisibleViewType == MyAdapter.VIEW_TYPE_HEADER)  // 如果是组的头部
        {
            if (headerInfo == null) headerInfo = new HeaderInfo(rv.getChildAt(firstVisiblePosition));
        }

        // 参数 0 表示获取第一个触摸点的坐标，以避免数组越界异常
        // 注意：下面的代码仅适用于单点触摸事件
        if (isFloatingHeaderClicked(e.getX(0), e.getY(0), e.getAction()))
        {
            List<Integer> offsetList = adapter.getGroupIndexList();  // 获取所有 view 对应的 group 下标
            int groupIndex = offsetList.get(firstVisiblePosition);  // 获取当前组的下标
            Group group = adapter.getDataList().get(groupIndex); // 获取当前组对象
            adapter.onHeaderClick(group);  // 折叠/展开列表头，更新视图
            arrowImageView.setImageResource(group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more);
            for (int i = 0; i < offsetList.size(); ++i)
            {
                if (offsetList.get(i) == groupIndex)  // 获取悬浮列表头在recyclerview中的位置
                {
                    // 点击悬浮列表头，自动将recyclerview滚动到该列表头所在位置
                    Objects.requireNonNull(layoutManager).scrollToPositionWithOffset(i, 0);
                    break;
                }
            }
            return true;  // 拦截触摸事件，不再传递给子View
        }
        return false;  // 否则，按照默认的逻辑处理触摸事件
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