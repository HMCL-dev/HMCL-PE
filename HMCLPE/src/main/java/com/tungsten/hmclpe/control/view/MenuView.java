package com.tungsten.hmclpe.control.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MenuView extends View {

    private MenuHelper menuHelper;
    private MenuCallback menuCallback;

    private int screenWidth;
    private int screenHeight;

    private float yPercent;

    public static final int MENU_MODE_LEFT = 0;
    public static final int MENU_MODE_RIGHT = 1;

    private final int DEFAULT_WIDTH = ConvertUtils.dip2px(getContext(),60);
    private final int DEFAULT_HEIGHT = ConvertUtils.dip2px(getContext(),60);

    private int radius = ConvertUtils.dip2px(getContext(),10);

    private boolean moveMode = false;
    private int menuMode;

    private Timer moveTimer;
    private Timer positionTimer;
    private Timer modeTimer;

    private float initialX;
    private float initialY;

    private Paint painter;

    private final Paint outlinePaint;

    public MenuView(Context context, MenuHelper menuHelper, int screenWidth, int screenHeight, int mode, float yPercent) {
        super(context);
        this.menuHelper = menuHelper;
        painter = new Paint();
        painter.setAntiAlias(true);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.menuMode = mode;
        this.yPercent = yPercent;

        outlinePaint = new Paint();
        outlinePaint.setAntiAlias(true);
        outlinePaint.setColor(getContext().getColor(R.color.colorRed));
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        if (menuMode == MENU_MODE_LEFT){
            setX(-(DEFAULT_WIDTH >> 1));
        }
        else {
            setX(screenWidth - (DEFAULT_WIDTH >> 1));
        }
        setY((screenHeight - DEFAULT_HEIGHT) * yPercent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int r = Math.min(radius, (getMeasuredHeight() / 2));
        painter.setColor((r == (getMeasuredHeight() / 2) && !moveMode) ? getContext().getColor(R.color.launcher_ui_background) : getContext().getColor(R.color.launcher_ui_background_light));
        @SuppressLint("DrawAllocation") Rect rect = new Rect((getMeasuredWidth() / 2) - r,r,(getMeasuredWidth() / 2) + r,getMeasuredHeight() - r);
        @SuppressLint("DrawAllocation") RectF upRectF = new RectF((getMeasuredWidth() >> 1) - r,0,(getMeasuredWidth() >> 1) + r,2 * r);
        @SuppressLint("DrawAllocation") RectF downRectF = new RectF((getMeasuredWidth() >> 1) - r,getMeasuredHeight() - (2 * r),(getMeasuredWidth() >> 1) + r,getMeasuredHeight());
        canvas.drawArc(upRectF,0,-180,true,painter);
        canvas.drawArc(downRectF,0,180,true,painter);
        if (radius < getMeasuredHeight() / 2){
            canvas.drawRect(rect,painter);
        }
        if (menuHelper.showOutline) {
            @SuppressLint("DrawAllocation") Path outlinePath = new Path();
            outlinePath.moveTo(0,0);
            outlinePath.lineTo(getWidth(),0);
            outlinePath.lineTo(getWidth(),getHeight());
            outlinePath.lineTo(0,getHeight());
            outlinePath.lineTo(0,0);
            canvas.drawPath(outlinePath,outlinePaint);
        }
        invalidate();
    }

    public void addCallback(MenuCallback menuCallback){
        this.menuCallback = menuCallback;
    }

    public void setMenuMode(int menuMode){
        this.menuMode = menuMode;
    }

    public int getMenuMode(){
        return menuMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (menuMode == MENU_MODE_LEFT && event.getX() >= initialX && radius < getMeasuredHeight() / 2 && initialX <= ConvertUtils.dip2px(getContext(),40) && !moveMode){
                    radius = (int) ((event.getX() - initialX) / 4) + ConvertUtils.dip2px(getContext(),10);
                }
                if (menuMode == MENU_MODE_RIGHT && event.getX() <= initialX && radius < getMeasuredHeight() / 2 && initialX >= ConvertUtils.dip2px(getContext(),20) && !moveMode){
                    radius = (int) (initialX - (event.getX()) / 4) + ConvertUtils.dip2px(getContext(),10);
                }
                if (radius >= getMeasuredHeight() / 2 && moveTimer == null){
                    moveTimer = new Timer();
                    TimerTask task = new TimerTask() {
                        public void run() {
                            moveMode = true;
                            initialY = event.getY();
                            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            if (menuCallback != null){
                                menuCallback.onMoveModeStart();
                            }
                            Timer timer = new Timer();
                            TimerTask recoverTask = new TimerTask() {
                                public void run() {
                                    if (radius > ConvertUtils.dip2px(getContext(),10)){
                                        radius--;
                                    }
                                    else {
                                        timer.cancel();
                                    }
                                }
                            };
                            timer.schedule(recoverTask, 0,1);
                            positionTimer = new Timer();
                            TimerTask positionTask;
                            if (menuMode == MENU_MODE_LEFT){
                                positionTask = new TimerTask() {
                                    public void run() {
                                        if (getX() < -ConvertUtils.dip2px(getContext(),20)){
                                            setX(getX() + 1);
                                        }
                                        else {
                                            positionTimer.cancel();
                                            positionTimer = null;
                                        }
                                    }
                                };
                            }
                            else {
                                positionTask = new TimerTask() {
                                    public void run() {
                                        if (getX() > screenWidth - ConvertUtils.dip2px(getContext(),40)){
                                            setX(getX() - 1);
                                        }
                                        else {
                                            positionTimer.cancel();
                                            positionTimer = null;
                                        }
                                    }
                                };
                            }
                            positionTimer.schedule(positionTask,0,1);
                            moveTimer.cancel();
                            moveTimer = null;
                        }
                    };
                    moveTimer.schedule(task, 800);
                }
                else if (radius < getMeasuredHeight() / 2 && moveTimer != null){
                    moveTimer.cancel();
                    moveTimer = null;
                }
                if (moveMode){
                    if (getY() + event.getY() - initialY >= 0 && getY() + event.getY() - initialY <= screenHeight - getMeasuredHeight()){
                        setY(getY() + event.getY() - initialY);
                        yPercent = (getY() + event.getY() - initialY) / (screenHeight - getMeasuredHeight());
                    }
                    else if (getY() + event.getY() - initialY < 0){
                        setY(0);
                        yPercent = 0;
                    }
                    else if (getY() + event.getY() - initialY > screenHeight - getMeasuredHeight()){
                        setY(screenHeight - getMeasuredHeight());
                        yPercent = 1;
                    }
                    if (event.getRawX() > screenWidth >> 1){
                        menuMode = MENU_MODE_RIGHT;
                        setX(screenWidth - ConvertUtils.dip2px(getContext(),40));
                    }
                    else {
                        menuMode = MENU_MODE_LEFT;
                        setX(-ConvertUtils.dip2px(getContext(),20));
                    }
                    if (menuCallback != null){
                        menuCallback.onMove(menuMode,yPercent);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!moveMode && radius > ConvertUtils.dip2px(getContext(),10)){
                    if (radius >= getMeasuredHeight() / 2 && menuCallback != null){
                        menuCallback.onRelease();
                    }
                    if (moveTimer != null){
                        moveTimer.cancel();
                        moveTimer = null;
                    }
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        public void run() {
                            if (radius > ConvertUtils.dip2px(getContext(),10)){
                                radius--;
                            }
                            else {
                                timer.cancel();
                            }
                        }
                    };
                    timer.schedule(task, 0,1);
                }
                if (moveMode){
                    if (menuCallback != null){
                        menuCallback.onMoveModeStop();
                    }
                    if (positionTimer != null){
                        positionTimer.cancel();
                        positionTimer = null;
                    }
                    positionTimer = new Timer();
                    TimerTask positionTask;
                    if (menuMode == MENU_MODE_LEFT){
                        positionTask = new TimerTask() {
                            public void run() {
                                if (getX() > -ConvertUtils.dip2px(getContext(),30)){
                                    setX(getX() - 1);
                                }
                                else {
                                    positionTimer.cancel();
                                    positionTimer = null;
                                }
                            }
                        };
                    }
                    else {
                        positionTask = new TimerTask() {
                            public void run() {
                                if (getX() < screenWidth - ConvertUtils.dip2px(getContext(),30)){
                                    setX(getX() + 1);
                                }
                                else {
                                    positionTimer.cancel();
                                    positionTimer = null;
                                }
                            }
                        };
                    }
                    positionTimer.schedule(positionTask,0,1);
                    moveMode = false;
                }
                break;
        }
        return true;
    }

    public interface MenuCallback{
        void onRelease();
        void onMoveModeStart();
        void onMove(int mode,float yPercent);
        void onMoveModeStop();
    }
}