package com.tungsten.hmclpe.control.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

public class LayoutPanel extends RelativeLayout {

    private static final int POSITION_MODE_PERCENT = 0;
    private static final int POSITION_MODE_ABSOLUTE = 1;

    private int positionMode = POSITION_MODE_PERCENT;

    private float[] xReference;
    private float[] yReference;

    private boolean showBackground = false;
    private boolean showReference = false;

    private Paint linePaint;
    private Path path;
    private Paint textPaint;
    private String xText;
    private String yText;

    private Bitmap background;

    public LayoutPanel(Context context) {
        super(context);
    }

    public LayoutPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(getContext().getColor(R.color.colorGreen));
        linePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getContext().getColor(R.color.colorGreen));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(50);

        xReference = new float[2];
        yReference = new float[2];

        background = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_background);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        if (showBackground){
            Rect src = new Rect(0, 0, background.getWidth(), background.getHeight());
            Rect dst = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawBitmap(background, src, dst, new Paint(Paint.ANTI_ALIAS_FLAG));
        }
        if (showReference){
            path = new Path();
            path.moveTo(xReference[0],0);
            path.lineTo(xReference[0],getHeight());
            path.moveTo(xReference[1],0);
            path.lineTo(xReference[1],getHeight());
            path.moveTo(0,yReference[0]);
            path.lineTo(getWidth(),yReference[0]);
            path.moveTo(0,yReference[1]);
            path.lineTo(getWidth(),yReference[1]);
            canvas.drawPath(path,linePaint);
            canvas.drawText(xText,100,100,textPaint);
            canvas.drawText(yText,100,200,textPaint);
        }
        invalidate();
    }

    public void showReference(int positionMode, float x, float y, int width, int height){
        this.positionMode = positionMode;
        if (positionMode == POSITION_MODE_PERCENT){
            xText = "X:" + ((int) ((x / (getWidth() - width)) * 1000)) / 10f + "%";
            yText = "Y:" + ((int) ((y / (getHeight() - height)) * 1000)) / 10f + "%";
        }
        if (positionMode == POSITION_MODE_ABSOLUTE){
            xText = "X:" + ConvertUtils.px2dip(getContext(),x) + "dp";
            yText = "Y:" + ConvertUtils.px2dip(getContext(),y) + "dp";
        }
        this.xReference[0] = x;
        this.yReference[0] = y;
        this.xReference[1] = x + width;
        this.yReference[1] = y + height;
        showReference = true;
    }

    public void hideReference(){
        showReference = false;
    }

    public void showBackground(){
        showBackground = true;
    }

    public void hideBackground() {
        showBackground = false;
    }
}
