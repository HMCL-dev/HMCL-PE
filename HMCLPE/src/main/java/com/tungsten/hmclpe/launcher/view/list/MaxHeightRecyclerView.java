package com.tungsten.hmclpe.launcher.view.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

public class MaxHeightRecyclerView extends RecyclerView {
	/**
	* 默认最大高度
	**/
    private int maxHeight = 300;

    public MaxHeightRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable") TypedArray a = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecycler);
        if (a != null) {
            try {
                maxHeight = a.getInteger(R.styleable.MaxHeightRecycler_maxRecyclerHeight, 300);
            }
            finally {
                a.recycle();
            }
        }
		// 设置的高度dp转成px
        maxHeight = ConvertUtils.dip2px(getContext(),maxHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 0) {
            int height;
            View child = getChildAt(0);
            RecyclerView.LayoutParams params = (LayoutParams) child.getLayoutParams();
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            // item个数
            int itemCount = getAdapter().getItemCount();
            // item高度
            int item = child.getMeasuredHeight() + getPaddingTop() + getPaddingBottom() + params.topMargin + params.bottomMargin;
            // 把item的高度转成px
            int max = itemCount * ConvertUtils.dip2px(getContext(),item);
            height = Math.min(max, maxHeight);
            setMeasuredDimension(widthMeasureSpec, height);
        }
        else {
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
    }
}

