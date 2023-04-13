package com.example.recyclerviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyItemDecoration extends RecyclerView.ItemDecoration
{
    private final MyAdapter adapter;

    private HeaderInfo headerInfo;

    public MyItemDecoration(MyAdapter adapter)
    {
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
        drawHeader(c, group);  // 在画布上绘制上一个组头部视图
    }

    private void drawHeader(Canvas c, Group group)
    {
        Paint paint = new Paint();
        paint.setColor(headerInfo.headerColor); // 设置背景色

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(45);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // 绘制一个矩形作为背景
        c.drawRect(headerInfo.headerLeft, headerInfo.headerTop, headerInfo.headerRight, headerInfo.headerBottom, paint);

        // 绘制日期文本
        c.drawText(group.getHeaderData().getDate(), headerInfo.headerLeft + 16, headerInfo.headerTop + 60, textPaint);

        // 绘制收入文本
        c.drawText(String.format(Locale.getDefault(), "+%.2f", group.getHeaderData().getIncome()), headerInfo.headerLeft + 500, headerInfo.headerTop + 60, textPaint);

        // 绘制支出文本
        c.drawText(String.format(Locale.getDefault(), "-%.2f", group.getHeaderData().getExpense()), headerInfo.headerLeft + 800, headerInfo.headerTop + 60, textPaint);

        // 绘制展开或收起的图标
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more); // 根据数据对象的isExpanded状态来选择不同的图标资源
        // c.drawBitmap(bitmap, right - 50, 10, null); // 在右下角绘制图标
    }

    private static class HeaderInfo
    {
        private final int headerTop;
        private final int headerBottom;
        private final int headerLeft;
        private final int headerRight;
        private final int headerColor;

        public HeaderInfo(View headerView)
        {
            this.headerTop = headerView.getTop();
            this.headerBottom = headerView.getBottom();
            this.headerLeft = headerView.getLeft();
            this.headerRight = headerView.getRight();
            Drawable background = headerView.getBackground();
            this.headerColor = background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : Color.WHITE;
        }
    }
}