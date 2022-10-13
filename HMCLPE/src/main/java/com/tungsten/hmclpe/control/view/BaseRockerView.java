package com.tungsten.hmclpe.control.view;

import static com.tungsten.hmclpe.control.bean.BaseRockerViewInfo.POSITION_TYPE_PERCENT;
import static com.tungsten.hmclpe.control.bean.BaseRockerViewInfo.SIZE_OBJECT_WIDTH;
import static com.tungsten.hmclpe.control.bean.BaseRockerViewInfo.SIZE_TYPE_PERCENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.InputBridge;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.control.bean.BaseRockerViewInfo;
import com.tungsten.hmclpe.launcher.dialogs.control.EditRockerDialog;
import com.tungsten.hmclpe.launcher.list.local.controller.ChildLayout;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import net.kdt.pojavlaunch.keyboard.LwjglGlfwKeycode;

@SuppressLint("ViewConstructor")
public class BaseRockerView extends RockerView{

    public int screenWidth;
    public int screenHeight;
    public BaseRockerViewInfo info;
    public MenuHelper menuHelper;

    public GradientDrawable drawableNormal;
    public GradientDrawable drawablePress;

    private long downTime;
    private float initialX;
    private float initialY;
    private float initialPositionX;
    private float initialPositionY;

    private boolean shiftMode = false;

    private boolean isShowing = true;

    private final Paint outlinePaint;

    private final Handler deleteHandler = new Handler();
    private final Runnable deleteRunnable = () -> {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.dialog_delete_rocker_title));
        builder.setMessage(getContext().getString(R.string.dialog_delete_rocker_content));
        builder.setPositiveButton(getContext().getString(R.string.dialog_delete_rocker_positive), (dialogInterface, i) -> {
            deleteRocker();
        });
        builder.setNegativeButton(getContext().getString(R.string.dialog_delete_rocker_negative), (dialogInterface, i) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    };

    public BaseRockerView(Context context,int screenWidth, int screenHeight, BaseRockerViewInfo info, MenuHelper menuHelper) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.menuHelper = menuHelper;

        outlinePaint = new Paint();
        outlinePaint.setAntiAlias(true);
        outlinePaint.setColor(getContext().getColor(R.color.colorRed));
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(3);

        refreshInfo(info);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (menuHelper.editMode) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downTime = System.currentTimeMillis();
                    initialX = event.getX();
                    initialY = event.getY();
                    initialPositionX = getX();
                    initialPositionY = getY();
                    deleteHandler.postDelayed(deleteRunnable,600);
                    menuHelper.viewManager.layoutPanel.showReference(info.positionType,getX(),getY(),this.getWidth(),this.getHeight());
                    break;
                case MotionEvent.ACTION_MOVE:
                    float targetX;
                    float targetY;
                    if (getX() + event.getX() - initialX >= 0 && getX() + event.getX() - initialX <= screenWidth - getWidth()){
                        targetX = getX() + event.getX() - initialX;
                    }
                    else if (getX() + event.getX() - initialX < 0){
                        targetX = 0;
                    }
                    else {
                        targetX = screenWidth - getWidth();
                    }
                    if (getY() + event.getY() - initialY >= 0 && getY() + event.getY() - initialY <= screenHeight - getHeight()){
                        targetY = getY() + event.getY() - initialY;
                    }
                    else if (getY() + event.getY() - initialY < 0){
                        targetY = 0;
                    }
                    else {
                        targetY = screenHeight - getHeight();
                    }
                    setX(targetX);
                    setY(targetY);
                    info.xPosition.absolutePosition = ConvertUtils.px2dip(getContext(),targetX);
                    info.yPosition.absolutePosition = ConvertUtils.px2dip(getContext(),targetY);
                    info.xPosition.percentPosition = targetX / (screenWidth - getWidth());
                    info.yPosition.percentPosition = targetY / (screenHeight - getHeight());
                    saveRockerInfo();
                    menuHelper.viewManager.layoutPanel.showReference(info.positionType,getX(),getY(),this.getWidth(),this.getHeight());
                    if (Math.abs(event.getX() - initialX) > 1 || Math.abs(event.getY() - initialY) > 1){
                        deleteHandler.removeCallbacks(deleteRunnable);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    deleteHandler.removeCallbacks(deleteRunnable);
                    if (System.currentTimeMillis() - downTime <= 200 && Math.abs(event.getX() - initialX) <= 10 && Math.abs(event.getY() - initialY) <= 10){
                        setX(initialPositionX);
                        setY(initialPositionY);
                        info.xPosition.absolutePosition = ConvertUtils.px2dip(getContext(),initialPositionX);
                        info.yPosition.absolutePosition = ConvertUtils.px2dip(getContext(),initialPositionY);
                        info.xPosition.percentPosition = initialPositionX / (screenWidth - getWidth());
                        info.yPosition.percentPosition = initialPositionY / (screenHeight - getHeight());
                        saveRockerInfo();
                        EditRockerDialog dialog = new EditRockerDialog(getContext(),menuHelper.viewManager, info.pattern, info.child,screenWidth,screenHeight,this,menuHelper.fullscreen);
                        dialog.show();
                    }
                    menuHelper.viewManager.layoutPanel.hideReference();
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setIsShowing(boolean show) {
        isShowing = show;
        refreshVisibility();
    }

    public boolean getIsShowing(){
        return isShowing;
    }

    public void refreshVisibility(){
        int mode = menuHelper.viewManager == null ? 0 : menuHelper.gameCursorMode;
        if (menuHelper.editMode || (isShowing && (info.showType == 0 || (mode == 1 && info.showType == 1) || (mode == 0 && info.showType == 2)))) {
            setVisibility(VISIBLE);
        }
        else {
            setVisibility(INVISIBLE);
        }
    }

    public void refreshInfo (BaseRockerViewInfo info) {
        this.info = info;
        drawableNormal = new GradientDrawable();
        drawablePress = new GradientDrawable();
        drawableNormal.setCornerRadius(ConvertUtils.dip2px(getContext(),info.rockerStyle.cornerRadius));
        drawableNormal.setStroke(ConvertUtils.dip2px(getContext(),info.rockerStyle.strokeWidth), Color.parseColor(info.rockerStyle.strokeColor));
        drawableNormal.setColor(Color.parseColor(info.rockerStyle.fillColor));
        drawablePress.setCornerRadius(ConvertUtils.dip2px(getContext(),info.rockerStyle.cornerRadiusPress));
        drawablePress.setStroke(ConvertUtils.dip2px(getContext(),info.rockerStyle.strokeWidthPress), Color.parseColor(info.rockerStyle.strokeColorPress));
        drawablePress.setColor(Color.parseColor(info.rockerStyle.fillColorPress));
        this.setPointerColor(info.rockerStyle.pointerColor);
        this.setPointerColorPress(info.rockerStyle.pointerColorPress);
        this.setFollowType(info.followType);
        this.setDoubleClick(info.shift);
        this.setOnShakeListener(new OnShakeListener() {
            @Override
            public void onTouch(RockerView view) {
                if (menuHelper.editMode) {
                    setFollowType(0);
                    setDoubleClick(false);
                }
                else {
                    setFollowType(info.followType);
                    setDoubleClick(info.shift);
                }
                setPressDrawable();
            }

            @Override
            public void onShake(RockerView view, Direction direction) {
                if (!menuHelper.editMode) {
                    getDirectionEvent(direction);
                }
            }

            @Override
            public void onCenterDoubleClick(RockerView view) {
                if (!menuHelper.editMode) {
                    if (shiftMode) {
                        InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_LEFT_SHIFT,false);
                    }
                    else {
                        InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_LEFT_SHIFT,true);
                    }
                    shiftMode = !shiftMode;
                }
            }

            @Override
            public void onFinish(RockerView view) {
                setNormalDrawable();
                setFollowType(info.followType);
                setDoubleClick(info.shift);
            }
        });
        setNormalDrawable();
    }

    public void setNormalDrawable(){
        setBackground(drawableNormal);
    }

    public void setPressDrawable(){
        setBackground(drawablePress);
    }

    public void getDirectionEvent(Direction direction) {
        switch (direction) {
            case DIRECTION_CENTER:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,false);
                break;
            case DIRECTION_UP:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,false);
                break;
            case DIRECTION_DOWN:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,false);
                break;
            case DIRECTION_LEFT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,false);
                break;
            case DIRECTION_RIGHT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,true);
                break;
            case DIRECTION_UP_LEFT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,false);
                break;
            case DIRECTION_UP_RIGHT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,true);
                break;
            case DIRECTION_DOWN_LEFT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,false);
                break;
            case DIRECTION_DOWN_RIGHT:
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_W,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_A,false);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_S,true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_D,true);
                break;
        }
    }

    public void updateSizeAndPosition (BaseRockerViewInfo info) {
        this.info = info;
        int size;
        if (info.sizeType == SIZE_TYPE_PERCENT){
            if (info.size.object == SIZE_OBJECT_WIDTH){
                size = (int) (screenWidth * info.size.percentSize);
            }
            else {
                size = (int) (screenHeight * info.size.percentSize);
            }
        }
        else {
            size = ConvertUtils.dip2px(getContext(),info.size.absoluteSize);
        }
        setSize(size);
        if (info.positionType == POSITION_TYPE_PERCENT){
            setX((screenWidth - size) * info.xPosition.percentPosition);
            setY((screenHeight - size) * info.yPosition.percentPosition);
        }
        else {
            setX(ConvertUtils.dip2px(getContext(),info.xPosition.absolutePosition));
            setY(ConvertUtils.dip2px(getContext(),info.yPosition.absolutePosition));
        }
    }

    public void saveRockerInfo() {
        if (menuHelper.editMode) {
            ChildLayout childLayout = null;
            for (ChildLayout child : SettingUtils.getChildList(info.pattern)) {
                if (child.name.equals(menuHelper.currentChild)) {
                    childLayout = child;
                }
            }
            assert childLayout != null;
            boolean exist = false;
            for (int i = 0;i < childLayout.baseRockerViewList.size();i++) {
                if (childLayout.baseRockerViewList.get(i).uuid.equals(info.uuid)) {
                    childLayout.baseRockerViewList.get(i).refresh(info);
                    exist = true;
                }
            }
            if (!exist) {
                childLayout.baseRockerViewList.add(info);
            }
            ChildLayout.saveChildLayout(info.pattern,childLayout);
        }
    }

    public void deleteRocker () {
        if (menuHelper.editMode) {
            ChildLayout childLayout = null;
            for (ChildLayout child : SettingUtils.getChildList(info.pattern)) {
                if (child.name.equals(menuHelper.currentChild)) {
                    childLayout = child;
                }
            }
            assert childLayout != null;
            for (int i = 0;i < childLayout.baseRockerViewList.size();i++) {
                if (childLayout.baseRockerViewList.get(i).uuid.equals(info.uuid)) {
                    childLayout.baseRockerViewList.remove(i);
                    break;
                }
            }
            ChildLayout.saveChildLayout(info.pattern,childLayout);
            menuHelper.viewManager.layoutPanel.removeView(this);
        }
    }

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {

            }
            if (msg.what == 1) {

            }
        }
    };

}
