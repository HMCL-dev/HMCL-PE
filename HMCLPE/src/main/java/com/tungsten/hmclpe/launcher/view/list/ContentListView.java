package com.tungsten.hmclpe.launcher.view.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ListView;

import com.tungsten.hmclpe.R;

public class ContentListView extends ListView {

    private float maxHeight = 10000;

    public ContentListView(Context context) {
        super(context);
    }

    public ContentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ContentListView, 0, defStyleAttr);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int type = array.getIndex(i);
            if (type == R.styleable.ContentListView_maxHeight) {
                //获得布局中限制的最大高度
                maxHeight = array.getDimension(type, -1);
            }
        }
        array.recycle();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取lv本身高度
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        //限制高度小于lv高度,设置为限制高度
        if (maxHeight <= specSize && maxHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Float.valueOf(maxHeight).intValue(),
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeight(float maxHeight){
        this.maxHeight = maxHeight;
    }

    public float getMaxHeight(){
        return maxHeight;
    }

}
