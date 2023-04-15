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
    private View currentHeaderView;

    public MyItemDecoration(Context context, MyAdapter adapter)
    {
        this.context = context;
        this.adapter = adapter;
    }

    /**
     * 初始化 HeaderInfo
     */
    private void initHeaderInfo(RecyclerView recyclerView, int firstVisiblePosition)
    {
        int firstVisibleViewType = adapter.getItemViewType(firstVisiblePosition); // 获取第一个可见的 item 的类型
        if (firstVisibleViewType == MyAdapter.VIEW_TYPE_HEADER)  // 如果是列表头
        {
            headerInfo = new HeaderInfo(recyclerView.getChildAt(firstVisiblePosition));
        }
    }

    /**
     * 获取第一个可见item的位置
     */
    private int getFirstVisiblePosition(RecyclerView recyclerView)
    {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        return Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();
    }

    /**
     * 当 recyclerview 存在数据的时候，程序启动一定先执行 onDrawOver() 再执行 onInterceptTouchEvent()
     */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        if (state.isMeasuring() || adapter.getItemCount() == 0)  // 如果 recyclerview 正在进行布局前的准备工作或数据为空
        {
            return; // 直接返回，不做任何操作
        }

        int firstVisiblePosition = getFirstVisiblePosition(parent);  // 获取第一个可见的 item 的位置

        if (headerInfo == null) initHeaderInfo(parent, firstVisiblePosition);  // 初始化 headerInfo

        List<Integer> offsetList = adapter.getGroupIndexList();  // 获取所有 view 对应的 group 下标
        Group group = adapter.getDataList().get(offsetList.get(firstVisiblePosition)); // 获取当前组对象

        if (currentGroup == null || currentGroup != group)
        {
            currentGroup = group;
            currentHeaderView = createHeaderView(parent, group);  // 初始化一个新的列表头
        }

        drawFloatingHeader(c);
    }

    /**
     * 创建一个新的悬浮列表头
     */
    private View createHeaderView(RecyclerView recyclerView, Group group)
    {
        View headerView = LayoutInflater.from(context).inflate(R.layout.header_recycler_view, recyclerView, false);
        TextView dateTextView = headerView.findViewById(R.id.header_date);
        TextView incomeTextView = headerView.findViewById(R.id.header_income);
        TextView expenseTextView = headerView.findViewById(R.id.header_expense);
        ImageView imageView = headerView.findViewById(R.id.header_button);
        dateTextView.setText(group.getHeaderData().getDate());
        incomeTextView.setText(String.format(Locale.getDefault(), "+%.2f", group.getHeaderData().getIncome()));
        expenseTextView.setText(String.format(Locale.getDefault(), "-%.2f", group.getHeaderData().getExpense()));
        imageView.setImageResource(group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more);
        return headerView;
    }

    /**
     * 更新悬浮列表头
     */
    private void updateHeaderView()
    {
        ImageView imageView = currentHeaderView.findViewById(R.id.header_button);
        imageView.setImageResource(currentGroup.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more);
    }

    /**
     * 将 HeaderView 绘制到 RecyclerView 的画布上
     */
    private void drawFloatingHeader(Canvas canvas)
    {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(headerInfo.getHeaderRight() - headerInfo.getHeaderLeft(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(headerInfo.getHeaderBottom() - headerInfo.getHeaderTop(), View.MeasureSpec.EXACTLY);
        currentHeaderView.measure(widthSpec, heightSpec);
        currentHeaderView.layout(headerInfo.getHeaderLeft(), headerInfo.getHeaderTop(), headerInfo.getHeaderRight(), headerInfo.getHeaderBottom());
        currentHeaderView.draw(canvas);
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

    /**
     * 将 recyclerview 滚动至列表头原位置
     */
    private void autoScroll(RecyclerView recyclerView)
    {
        List<Integer> offsetList = adapter.getGroupIndexList();  // 获取所有 view 对应的 group 下标
        int firstVisiblePosition = getFirstVisiblePosition(recyclerView);
        int groupIndex = offsetList.get(firstVisiblePosition);  // 获取当前组的下标
        for (int i = 0; i < offsetList.size(); ++i)
        {
            if (offsetList.get(i) == groupIndex)
            {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) layoutManager.scrollToPositionWithOffset(i, 0);
                return;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
    {
        if (adapter.getItemCount() == 0) return false;  // 如果 recyclerView 数据为空

        // 参数 0 表示获取第一个触摸点的坐标，以避免数组越界异常
        // 注意：下面的代码仅适用于判断单点触摸事件
        if (!isFloatingHeaderClicked(e.getX(0), e.getY(0), e.getAction())) return false;

        adapter.onHeaderClick(currentGroup);  // 折叠或展开列表头，更新 recyclerview
        updateHeaderView();  // 更新悬浮列表头
        autoScroll(rv);  // 将 recyclerview 滚动至列表头原位置

        return true;  // 拦截触摸事件，不再传递给子 view
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