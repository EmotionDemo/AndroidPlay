package com.example.test.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 添加设置高度最大值
 */
public class MyRecyclerView extends RecyclerView {
    private int mMaxHeight;

    public MyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int limitHeight = getMeasuredHeight();
        if (limitHeight > mMaxHeight) {
            setMeasuredDimension(widthSpec, mMaxHeight);
        } else {
            setMeasuredDimension(widthSpec, getMeasuredHeight());
        }
    }

    public void setMaxHeight(int maxHeight) {
        this.mMaxHeight = maxHeight;
    }
}
