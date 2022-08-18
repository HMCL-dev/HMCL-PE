package com.tungsten.hmclpe.control.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class RockerView extends View {

    private String pointerColor = "#f6f6f6";
    private String pointerColorPress = "#40ffffff";

    private int followType = 0;
    private boolean doubleClick = true;

    private OnShakeListener onShakeListener;

    private State center = State.NORMAL;
    private State up = State.NORMAL;
    private State down = State.NORMAL;
    private State left = State.NORMAL;
    private State right = State.NORMAL;
    private State upLeft = State.HIDE;
    private State downLeft = State.HIDE;
    private State upRight = State.HIDE;
    private State downRight = State.HIDE;

    private static final double ANGLE_0 = 0;
    private static final double ANGLE_360 = 360;

    private static final double ANGLE_8D_OF_0P = 22.5;
    private static final double ANGLE_8D_OF_1P = 67.5;
    private static final double ANGLE_8D_OF_2P = 112.5;
    private static final double ANGLE_8D_OF_3P = 157.5;
    private static final double ANGLE_8D_OF_4P = 202.5;
    private static final double ANGLE_8D_OF_5P = 247.5;
    private static final double ANGLE_8D_OF_6P = 292.5;
    private static final double ANGLE_8D_OF_7P = 337.5;

    private Direction tempDirection = Direction.DIRECTION_CENTER;

    private boolean touching = false;

    private int clickCount = 0;
    private long firstClickTime;

    private float initialPositionX;
    private float initialPositionY;

    public RockerView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path centerPointerPath = new Path();
        Path upPointerPath = new Path();
        Path downPointerPath = new Path();
        Path leftPointerPath = new Path();
        Path rightPointerPath = new Path();
        Path upLeftPointerPath = new Path();
        Path upRightPointerPath = new Path();
        Path downLeftPointerPath = new Path();
        Path downRightPointerPath = new Path();

        //中
        centerPointerPath.moveTo((4 * getWidth()) / 10,getHeight() / 2);
        centerPointerPath.lineTo(getWidth() / 2,(4 * getHeight()) / 10);
        centerPointerPath.lineTo((6 * getWidth()) / 10,getHeight() / 2);
        centerPointerPath.lineTo(getWidth() / 2,(6 * getHeight()) / 10);
        centerPointerPath.lineTo((4 * getWidth()) / 10,getHeight() / 2);
        //上
        upPointerPath.moveTo(getWidth() / 2,getHeight() / 10);
        upPointerPath.lineTo((getWidth() / 2) - (getWidth() / 10),(2 * getHeight()) / 10);
        upPointerPath.lineTo((getWidth() / 2) + (getWidth() / 10),(2 * getHeight()) / 10);
        //下
        downPointerPath.moveTo(getWidth() / 2,(9 * getHeight()) / 10);
        downPointerPath.lineTo((getWidth() / 2) - (getWidth() / 10),(8 * getHeight()) / 10);
        downPointerPath.lineTo((getWidth() / 2) + (getWidth() / 10),(8 * getHeight()) / 10);
        //左
        leftPointerPath.moveTo(getWidth() / 10,getHeight() / 2);
        leftPointerPath.lineTo(2 * (getWidth() / 10),(getHeight() / 2) - (getHeight() / 10));
        leftPointerPath.lineTo(2 * (getWidth() / 10),(getHeight() / 2) + (getHeight() / 10));
        //右
        rightPointerPath.moveTo(9 * (getWidth() / 10),getHeight() / 2);
        rightPointerPath.lineTo(8 * (getWidth() / 10),(getHeight() / 2) - (getHeight() / 10));
        rightPointerPath.lineTo(8 * (getWidth() / 10),(getHeight() / 2) + (getHeight() / 10));
        //左上
        upLeftPointerPath.moveTo(2 * (getWidth() / 10),2 * (getHeight() / 10));
        upLeftPointerPath.lineTo(3 * (getWidth() / 10),2 * (getHeight() / 10));
        upLeftPointerPath.lineTo(2 * (getWidth() / 10),3 * (getHeight() / 10));
        //左下
        downLeftPointerPath.moveTo(2 * (getWidth() / 10),8 * (getHeight() / 10));
        downLeftPointerPath.lineTo(3 * (getWidth() / 10),8 * (getHeight() / 10));
        downLeftPointerPath.lineTo(2 * (getWidth() / 10),7 * (getHeight() / 10));
        //右上
        upRightPointerPath.moveTo(8 * (getWidth() / 10),2 * (getHeight() / 10));
        upRightPointerPath.lineTo(7 * (getWidth() / 10),2 * (getHeight() / 10));
        upRightPointerPath.lineTo(8 * (getWidth() / 10),3 * (getHeight() / 10));
        //右下
        downRightPointerPath.moveTo(8 * (getWidth() / 10),8 * (getHeight() / 10));
        downRightPointerPath.lineTo(7 * (getWidth() / 10),8 * (getHeight() / 10));
        downRightPointerPath.lineTo(8 * (getWidth() / 10),7 * (getHeight() / 10));

        Paint pointerPaint = new Paint();
        pointerPaint.setAntiAlias(true);
        pointerPaint.setColor(Color.parseColor(pointerColor));
        pointerPaint.setStyle(Paint.Style.FILL);

        Paint pointerPaintPress = new Paint();
        pointerPaintPress.setAntiAlias(true);
        pointerPaintPress.setColor(Color.parseColor(pointerColorPress));
        pointerPaintPress.setStyle(Paint.Style.FILL);

        if (center == State.NORMAL) {
            canvas.drawPath(centerPointerPath,pointerPaint);
        }
        if (up == State.NORMAL) {
            canvas.drawPath(upPointerPath,pointerPaint);
        }
        if (down == State.NORMAL) {
            canvas.drawPath(downPointerPath,pointerPaint);
        }
        if (left == State.NORMAL) {
            canvas.drawPath(leftPointerPath,pointerPaint);
        }
        if (right == State.NORMAL) {
            canvas.drawPath(rightPointerPath,pointerPaint);
        }
        if (upLeft == State.NORMAL) {
            canvas.drawPath(upLeftPointerPath,pointerPaint);
        }
        if (upRight == State.NORMAL) {
            canvas.drawPath(upRightPointerPath,pointerPaint);
        }
        if (downLeft == State.NORMAL) {
            canvas.drawPath(downLeftPointerPath,pointerPaint);
        }
        if (downRight == State.NORMAL) {
            canvas.drawPath(downRightPointerPath,pointerPaint);
        }
        if (center == State.PRESS) {
            canvas.drawPath(centerPointerPath,pointerPaintPress);
        }
        if (up == State.PRESS) {
            canvas.drawPath(upPointerPath,pointerPaintPress);
        }
        if (down == State.PRESS) {
            canvas.drawPath(downPointerPath,pointerPaintPress);
        }
        if (left == State.PRESS) {
            canvas.drawPath(leftPointerPath,pointerPaintPress);
        }
        if (right == State.PRESS) {
            canvas.drawPath(rightPointerPath,pointerPaintPress);
        }
        if (upLeft == State.PRESS) {
            canvas.drawPath(upLeftPointerPath,pointerPaintPress);
        }
        if (upRight == State.PRESS) {
            canvas.drawPath(upRightPointerPath,pointerPaintPress);
        }
        if (downLeft == State.PRESS) {
            canvas.drawPath(downLeftPointerPath,pointerPaintPress);
        }
        if (downRight == State.PRESS) {
            canvas.drawPath(downRightPointerPath,pointerPaintPress);
        }

        invalidate();
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float centerX = getWidth() / 2;
                float centerY = getHeight() / 2;
                if (calculateDistance(event.getX(),event.getY(),centerX,centerY) <= getWidth() / 2) {
                    touching = true;
                    if (onShakeListener != null) {
                        onShakeListener.onTouch(this);
                    }
                    initialPositionX = getX();
                    initialPositionY = getY();
                    if ((followType == 1 && calculateDistance(event.getX(),event.getY(),centerX,centerY) <= getWidth() / 6) || followType == 2) {
                        setX(initialPositionX + event.getX() - centerX);
                        setY(initialPositionY + event.getY() - centerY);
                    }
                    if (calculateDistance(event.getX(),event.getY(),centerX,centerY) <= getWidth() / 6) {
                        tempDirection = Direction.DIRECTION_CENTER;
                        center = State.PRESS;
                        up = State.NORMAL;
                        down = State.NORMAL;
                        left = State.NORMAL;
                        right = State.NORMAL;
                        upLeft = State.HIDE;
                        downLeft = State.HIDE;
                        upRight = State.HIDE;
                        downRight = State.HIDE;
                        if (onShakeListener != null) {
                            onShakeListener.onShake(this,tempDirection);
                        }
                    }
                    refreshView(event);
                    if ((calculateDistance(event.getX(),event.getY(),centerX,centerY) <= getWidth() / 6 || followType == 2) && doubleClick) {
                        clickCount++;
                        if (clickCount == 1) {
                            firstClickTime = System.currentTimeMillis();
                        }
                        if (clickCount == 2) {
                            if (System.currentTimeMillis() - firstClickTime <= 500) {
                                if (onShakeListener != null) {
                                    onShakeListener.onCenterDoubleClick(this);
                                }
                                clickCount = 0;
                            }
                            else {
                                firstClickTime = System.currentTimeMillis();
                                clickCount = 1;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (touching) {
                    refreshView(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (touching) {
                    center = State.NORMAL;
                    up = State.NORMAL;
                    down = State.NORMAL;
                    left = State.NORMAL;
                    right = State.NORMAL;
                    upLeft = State.HIDE;
                    downLeft = State.HIDE;
                    upRight = State.HIDE;
                    downRight = State.HIDE;
                    if (tempDirection != Direction.DIRECTION_CENTER) {
                        tempDirection = Direction.DIRECTION_CENTER;
                        if (onShakeListener != null) {
                            onShakeListener.onShake(this,tempDirection);
                        }
                    }
                    if (followType != 0) {
                        setX(initialPositionX);
                        setY(initialPositionY);
                    }
                    if (onShakeListener != null) {
                        onShakeListener.onFinish(this);
                    }
                    touching = false;
                }
                break;
        }
        return true;
    }

    public void setSize(int size){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = size;
        params.height = size;
        setLayoutParams(params);
    }

    public void setPointerColor(String pointerColor) {
        this.pointerColor = pointerColor;
    }

    public void setPointerColorPress(String pointerColorPress) {
        this.pointerColorPress = pointerColorPress;
    }

    public void setFollowType(int followType) {
        this.followType = followType;
    }

    public void setDoubleClick(boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public void setOnShakeListener(OnShakeListener onShakeListener) {
        this.onShakeListener = onShakeListener;
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    private void refreshView(MotionEvent event) {
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        if (calculateDistance(event.getX(),event.getY(),centerX,centerY) <= getWidth() / 6) {
            if (tempDirection != Direction.DIRECTION_CENTER) {
                tempDirection = Direction.DIRECTION_CENTER;
                center = State.PRESS;
                up = State.NORMAL;
                down = State.NORMAL;
                left = State.NORMAL;
                right = State.NORMAL;
                upLeft = State.HIDE;
                downLeft = State.HIDE;
                upRight = State.HIDE;
                downRight = State.HIDE;
                if (onShakeListener != null) {
                    onShakeListener.onShake(this,tempDirection);
                }
            }
        }
        else {
            setDirection(event);
        }
    }

    private double calculateDistance (float xPri,float yPri,float xSec,float ySec) {
        float d = ((xPri - xSec) * (xPri - xSec)) + ((yPri - ySec) * (yPri - ySec));
        return Math.sqrt(d);
    }

    private double radian2Angle(double radian) {
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : 360 + tmp;
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    private void setDirection(MotionEvent event) {
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float lenX = (float) (event.getX() - centerX);
        float lenY = (float) (event.getY() - centerY);
        float lenXY = (float) Math.sqrt((double) (lenX * lenX + lenY * lenY));
        double radian = Math.acos(lenX / lenXY) * (event.getY() < centerY ? -1 : 1);
        double angle = radian2Angle(radian);
        if ((ANGLE_0 <= angle && ANGLE_8D_OF_0P > angle || ANGLE_8D_OF_7P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
            // 右
            tempDirection = Direction.DIRECTION_RIGHT;
            center = State.NORMAL;
            up = State.NORMAL;
            down = State.NORMAL;
            left = State.NORMAL;
            right = State.PRESS;
            upLeft = State.HIDE;
            downLeft = State.HIDE;
            upRight = State.NORMAL;
            downRight = State.NORMAL;
        } else if (ANGLE_8D_OF_0P <= angle && ANGLE_8D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN_RIGHT) {
            // 右下
            tempDirection = Direction.DIRECTION_DOWN_RIGHT;
            center = State.NORMAL;
            up = State.NORMAL;
            down = State.NORMAL;
            left = State.NORMAL;
            right = State.NORMAL;
            downRight = State.PRESS;
        } else if (ANGLE_8D_OF_1P <= angle && ANGLE_8D_OF_2P > angle && tempDirection != Direction.DIRECTION_DOWN) {
            // 下
            tempDirection = Direction.DIRECTION_DOWN;
            center = State.NORMAL;
            up = State.NORMAL;
            down = State.PRESS;
            left = State.NORMAL;
            right = State.NORMAL;
            upLeft = State.HIDE;
            downLeft = State.NORMAL;
            upRight = State.HIDE;
            downRight = State.NORMAL;
        } else if (ANGLE_8D_OF_2P <= angle && ANGLE_8D_OF_3P > angle && tempDirection != Direction.DIRECTION_DOWN_LEFT) {
            // 左下
            tempDirection = Direction.DIRECTION_DOWN_LEFT;
            center = State.NORMAL;
            up = State.NORMAL;
            down = State.NORMAL;
            left = State.NORMAL;
            right = State.NORMAL;
            downLeft = State.PRESS;
        } else if (ANGLE_8D_OF_3P <= angle && ANGLE_8D_OF_4P > angle && tempDirection != Direction.DIRECTION_LEFT) {
            // 左
            tempDirection = Direction.DIRECTION_LEFT;
            center = State.NORMAL;
            up = State.NORMAL;
            down = State.NORMAL;
            left = State.PRESS;
            right = State.NORMAL;
            upLeft = State.NORMAL;
            downLeft = State.NORMAL;
            upRight = State.HIDE;
            downRight = State.HIDE;
        } else if (ANGLE_8D_OF_4P <= angle && ANGLE_8D_OF_5P > angle && tempDirection != Direction.DIRECTION_UP_LEFT) {
            // 左上
            tempDirection = Direction.DIRECTION_UP_LEFT;
            center = State.NORMAL;
            up = State.NORMAL;
            down = State.NORMAL;
            left = State.NORMAL;
            right = State.NORMAL;
            upLeft = State.PRESS;
        } else if (ANGLE_8D_OF_5P <= angle && ANGLE_8D_OF_6P > angle && tempDirection != Direction.DIRECTION_UP) {
            // 上
            tempDirection = Direction.DIRECTION_UP;
            center = State.NORMAL;
            up = State.PRESS;
            down = State.NORMAL;
            left = State.NORMAL;
            right = State.NORMAL;
            upLeft = State.NORMAL;
            downLeft = State.HIDE;
            upRight = State.NORMAL;
            downRight = State.HIDE;
        } else if (ANGLE_8D_OF_6P <= angle && ANGLE_8D_OF_7P > angle && tempDirection != Direction.DIRECTION_UP_RIGHT) {
            // 右上
            tempDirection = Direction.DIRECTION_UP_RIGHT;
            center = State.NORMAL;
            up = State.NORMAL;
            down = State.NORMAL;
            left = State.NORMAL;
            right = State.NORMAL;
            upRight = State.PRESS;
        }
        if (onShakeListener != null) {
            onShakeListener.onShake(this,tempDirection);
        }
    }

    public enum State {
        NORMAL,
        PRESS,
        HIDE
    }

    public enum Direction {
        DIRECTION_LEFT, // 左
        DIRECTION_RIGHT, // 右
        DIRECTION_UP, // 上
        DIRECTION_DOWN, // 下
        DIRECTION_UP_LEFT, // 左上
        DIRECTION_UP_RIGHT, // 右上
        DIRECTION_DOWN_LEFT, // 左下
        DIRECTION_DOWN_RIGHT, // 右下
        DIRECTION_CENTER // 中间
    }

    public interface OnShakeListener{
        void onTouch(RockerView view);
        void onShake(RockerView view,Direction direction);
        void onCenterDoubleClick(RockerView view);
        void onFinish(RockerView view);
    }
}
