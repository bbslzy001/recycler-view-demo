package com.example.recyclerviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyItemDecoration extends RecyclerView.ItemDecoration
{
    private MyAdapter adapter;
    private Context context;

    public MyItemDecoration(Context context, MyAdapter adapter)
    {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
//        int position = parent.getChildAdapterPosition(view); // 获取当前视图在适配器中的位置
//        int viewType = adapter.getItemViewType(position); // 获取当前视图的类型
//        if (viewType == 0)
//        { // 如果是组的头部
//            outRect.set(0, 50, 0, 0); // 设置上方偏移量为50dp
//        }
//        else
//        {
//            outRect.set(0, 0, 0, 0); // 否则不设置偏移量
//        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        View firstVisibleView = parent.getChildAt(0); // 获取第一个可见的item的视图
        int firstVisiblePosition = parent.getChildAdapterPosition(firstVisibleView); // 获取第一个可见的item在适配器中的位置
        int firstVisibleViewType = adapter.getItemViewType(firstVisiblePosition); // 获取第一个可见的item的类型
        if (firstVisibleViewType == 0)
        { // 如果是组的头部
            Group group = adapter.getDataList().get(firstVisiblePosition); // 获取当前组对象
            //HeaderData headerData = group.getHeaderData(); // 获取当前组头部数据对象
            Log.d("test", "is header: "+group.toString());
            drawHeader(c, parent, group); // 在画布上绘制当前组头部视图
        }
        else
        { // 如果不是组的头部
            int previousHeaderPosition = getPreviousHeaderPosition(firstVisiblePosition); // 获取上一个组头部在适配器中的位置
            Group group = adapter.getDataList().get(previousHeaderPosition); // 获取上一个组对象
            Log.d("test", "not header: "+group.toString());
            //HeaderData headerData = group.getHeaderData(); // 获取上一个组头部数据对象
            drawHeader(c, parent, group); // 在画布上绘制上一个组头部视图
        }

        if (state.isPreLayout())
        { // 如果recyclerview正在进行布局前的准备工作
            return; // 直接返回，不做任何操作
        }

        int nextHeaderPosition = getNextHeaderPosition(firstVisiblePosition); // 获取下一个组头部在适配器中的位置
        if (nextHeaderPosition != -1)
        { // 如果存在下一个组头部
            View nextHeaderView = Objects.requireNonNull(parent.findViewHolderForAdapterPosition(nextHeaderPosition)).itemView; // 获取下一个组头部的视图
            if (nextHeaderView.getTop() <= 50)
            { // 如果下一个组头部的顶部坐标小于等于50dp（也就是悬浮视图的高度）
                Group group = adapter.getDataList().get(nextHeaderPosition);  // 获取下一个组对象
                c.save(); // 保存画布状态
                c.translate(0, nextHeaderView.getTop() - 50); // 将画布向上平移，平移量为下一个组头部的顶部坐标减去50dp
                drawHeader(c, parent, group); // 在画布上绘制悬浮视图
                c.restore(); // 恢复画布状态
                return; // 结束方法，不再执行后面的代码
            }
        }
    }

    private int getPreviousHeaderPosition(int position)
    {
        for (int i = position - 1; i >= 0; i--)
        { // 从当前位置向前遍历
            if (adapter.getItemViewType(i) == 0)
            { // 如果找到一个组头部
                return i; // 返回它的位置
            }
        }
        return -1; // 如果没有找到，返回-1
    }

    private int getNextHeaderPosition(int position)
    {
        for (int i = position + 1; i < adapter.getItemCount(); i++)
        { // 从当前位置向后遍历
            if (adapter.getItemViewType(i) == 0)
            { // 如果找到一个组头部
                return i; // 返回它的位置
            }
        }
        return -1; // 如果没有找到，返回-1
    }

    private void drawHeader(Canvas c, RecyclerView parent, Group group)
    {
        // 创建一个Paint对象，用来设置绘制的颜色和样式
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY); // 设置背景色为浅灰色

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(45);
        textPaint.setColor(Color.GRAY);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);


        // 获取recyclerview的左右边界
        int left = parent.getLeft();
        int right = parent.getRight();
        int top = parent.getTop();

        Log.d("test", "drawHeader: "+left+" "+right+" "+top);

        // 绘制一个矩形作为背景
        c.drawRect(left, top, right, 100, paint);

        // 绘制日期文本
        Log.d("test", "drawHeader: "+group.getHeaderData().getDate());
        c.drawText(group.getHeaderData().getDate(), left + 16, top+60, textPaint);

        // 绘制收入文本
        Log.d("test", "drawHeader: "+group.getHeaderData().getIncome());
        c.drawText(String.format(Locale.getDefault(), "+%.2f", group.getHeaderData().getIncome()), left+500, top+60, textPaint);

        // 绘制支出文本
        Log.d("test", "drawHeader: "+group.getHeaderData().getExpense());
        c.drawText(String.format(Locale.getDefault(), "-%.2f", group.getHeaderData().getExpense()), left + 800, top+60, textPaint);

        // 绘制展开或收起的图标
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), group.isExpanded() ? R.drawable.expand_less : R.drawable.expand_more); // 根据数据对象的isExpanded状态来选择不同的图标资源
       // c.drawBitmap(bitmap, right - 50, 10, null); // 在右下角绘制图标
    }
}