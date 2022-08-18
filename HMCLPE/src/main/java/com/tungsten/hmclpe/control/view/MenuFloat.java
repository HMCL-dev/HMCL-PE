package com.tungsten.hmclpe.control.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import java.io.File;

public class MenuFloat extends View {

    private MenuHelper menuHelper;
    private int screenWidth;
    private int screenHeight;
    private float xPosition;
    private float yPosition;

    private MenuFloatCallback callback;

    private final int DEFAULT_WIDTH = ConvertUtils.dip2px(getContext(),40);
    private final int DEFAULT_HEIGHT = ConvertUtils.dip2px(getContext(),40);

    private Paint paint;
    private Paint areaPaint;

    private final Paint outlinePaint;

    private Bitmap bitmap;

    private boolean pressed = false;

    private float initialX;
    private float initialY;

    private long downTime;

    public MenuFloat(Context context, MenuHelper menuHelper, int screenWidth, int screenHeight, float xPosition, float yPosition) {
        super(context);
        this.menuHelper = menuHelper;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getContext().getColor(R.color.colorDarkGray));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ConvertUtils.dip2px(getContext(),2));

        areaPaint = new Paint();
        areaPaint.setAntiAlias(true);

        outlinePaint = new Paint();
        outlinePaint.setAntiAlias(true);
        outlinePaint.setColor(getContext().getColor(R.color.colorRed));
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(3);

        File picPath=new File(context.getExternalFilesDir("Theme"),"floatIcon.png");
        if (picPath.exists()){
            bitmap=BitmapFactory.decodeFile(picPath.getAbsolutePath());
        } else {
            bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.ic_craft_table);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setX((screenWidth - DEFAULT_WIDTH) * xPosition);
        setY((screenHeight - DEFAULT_HEIGHT) * yPosition);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pressed){
            areaPaint.setColor(getContext().getColor(R.color.launcher_ui_background_light));
        }
        else {
            areaPaint.setColor(getContext().getColor(R.color.colorTransparent));
        }
        canvas.drawCircle(getMeasuredWidth() >> 1,getMeasuredHeight() >> 1,(getMeasuredWidth() >> 1) - ConvertUtils.dip2px(getContext(),1),paint);
        canvas.drawCircle(getMeasuredWidth() >> 1,getMeasuredHeight() >> 1,(getMeasuredWidth() >> 1) - ConvertUtils.dip2px(getContext(),2),areaPaint);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dst = new Rect(ConvertUtils.dip2px(getContext(),6), ConvertUtils.dip2px(getContext(),6), ConvertUtils.dip2px(getContext(),34), ConvertUtils.dip2px(getContext(),34));
        canvas.drawBitmap(bitmap, src, dst, new Paint(Paint.ANTI_ALIAS_FLAG));
        if (menuHelper.showOutline) {
            Path outlinePath = new Path();
            outlinePath.moveTo(0,0);
            outlinePath.lineTo(getWidth(),0);
            outlinePath.lineTo(getWidth(),getHeight());
            outlinePath.lineTo(0,getHeight());
            outlinePath.lineTo(0,0);
            canvas.drawPath(outlinePath,outlinePaint);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                downTime = System.currentTimeMillis();
                pressed = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float targetX;
                float targetY;
                if (getX() + event.getX() - initialX >= 0 && getX() + event.getX() - initialX <= screenWidth - getMeasuredWidth()){
                    targetX = getX() + event.getX() - initialX;
                }
                else if (getX() + event.getX() - initialX < 0){
                    targetX = 0;
                }
                else {
                    targetX = screenWidth - getMeasuredWidth();
                }
                if (getY() + event.getY() - initialY >= 0 && getY() + event.getY() - initialY <= screenHeight - getMeasuredHeight()){
                    targetY = getY() + event.getY() - initialY;
                }
                else if (getY() + event.getY() - initialY < 0){
                    targetY = 0;
                }
                else {
                    targetY = screenHeight - getMeasuredHeight();
                }
                if (menuHelper.gameMenuSetting.menuFloatSetting.movable) {
                    setX(targetX);
                    setY(targetY);
                    if (callback != null){
                        callback.onMove(getX() / (screenWidth - getMeasuredWidth()),getY() / (screenHeight - getMeasuredHeight()));
                    }
                }
                pressed = true;
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - initialX) <= 10 && Math.abs(event.getY() - initialY) <= 10 && System.currentTimeMillis() - downTime <= 400 && callback != null){
                    callback.onClick();
                }
                pressed = false;
                break;
        }
        return true;
    }

    public void addCallback(MenuFloatCallback callback){
        this.callback = callback;
    }

    public interface MenuFloatCallback{
        void onClick();
        void onMove(float xPosition,float yPosition);
    }
}
