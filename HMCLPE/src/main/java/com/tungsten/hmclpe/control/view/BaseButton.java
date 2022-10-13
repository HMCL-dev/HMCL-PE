package com.tungsten.hmclpe.control.view;

import static com.tungsten.hmclpe.control.bean.BaseButtonInfo.POSITION_TYPE_PERCENT;
import static com.tungsten.hmclpe.control.bean.BaseButtonInfo.SIZE_OBJECT_WIDTH;
import static com.tungsten.hmclpe.control.bean.BaseButtonInfo.SIZE_TYPE_PERCENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.InputBridge;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.control.bean.BaseButtonInfo;
import com.tungsten.hmclpe.launcher.dialogs.control.EditButtonDialog;
import com.tungsten.hmclpe.launcher.dialogs.control.InputDialog;
import com.tungsten.hmclpe.launcher.list.local.controller.ChildLayout;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import net.kdt.pojavlaunch.keyboard.LwjglGlfwKeycode;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class BaseButton extends androidx.appcompat.widget.AppCompatButton {

    public int screenWidth;
    public int screenHeight;
    public BaseButtonInfo info;
    public MenuHelper menuHelper;

    public GradientDrawable drawableNormal;
    public GradientDrawable drawablePress;

    private long downTime;
    private float initialX;
    private float initialY;
    private float initialPositionX;
    private float initialPositionY;
    private int clickCount;
    private long firstClickTime;

    private final Paint outlinePaint;

    private boolean isKeeping = false;

    private boolean isShowing = true;

    private final Handler deleteHandler = new Handler();
    private final Runnable deleteRunnable = () -> {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.dialog_delete_button_title));
        builder.setMessage(getContext().getString(R.string.dialog_delete_button_content));
        builder.setPositiveButton(getContext().getString(R.string.dialog_delete_button_positive), (dialogInterface, i) -> {
            deleteButton();
        });
        builder.setNegativeButton(getContext().getString(R.string.dialog_delete_button_negative), (dialogInterface, i) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    };

    private final Handler clickHandler = new Handler();
    private final Runnable clickRunnable = new Runnable() {
        @Override
        public void run() {
            for (int code : info.outputKeycode) {
                InputBridge.sendEvent(menuHelper.launcher,code,true);
            }
            clickHandler.post(clickRunnable);
        }
    };

    public BaseButton(Context context, int screenWidth, int screenHeight, BaseButtonInfo info, MenuHelper menuHelper) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.menuHelper = menuHelper;

        outlinePaint = new Paint();
        outlinePaint.setAntiAlias(true);
        outlinePaint.setColor(getContext().getColor(R.color.colorRed));
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(3);

        refreshStyle(info);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (menuHelper.showOutline) {
            Path outlinePath = new Path();
            outlinePath.moveTo(0,0);
            outlinePath.lineTo(getWidth(),0);
            outlinePath.lineTo(getWidth(),getHeight());
            outlinePath.lineTo(0,getHeight());
            outlinePath.lineTo(0,0);
            canvas.drawPath(outlinePath,outlinePaint);
        }
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
                    setPressDrawable();
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
                    saveButtonInfo();
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
                        saveButtonInfo();
                        EditButtonDialog dialog = new EditButtonDialog(getContext(),menuHelper.viewManager, info.pattern, info.child,screenWidth,screenHeight,this,menuHelper.fullscreen);
                        dialog.show();
                    }
                    setNormalDrawable();
                    menuHelper.viewManager.layoutPanel.hideReference();
                    break;
            }
        }
        else {
            if (info.functionType == 0) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!info.autoKeep) {
                            setPressDrawable();
                        }
                        initialX = event.getRawX();
                        initialY = event.getRawY();
                        initialPositionX = getX();
                        initialPositionY = getY();
                        if (info.openMenu) {
                            menuHelper.drawerLayout.openDrawer(GravityCompat.END,true);
                            menuHelper.drawerLayout.openDrawer(GravityCompat.START,true);
                        }
                        if (info.switchTouchMode) {
                            if (menuHelper.gameMenuSetting.touchMode == 0) {
                                menuHelper.spinnerTouchMode.setSelection(1);
                                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_touch_mode_attack_alert),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                menuHelper.spinnerTouchMode.setSelection(0);
                                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_touch_mode_create_alert),Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (info.switchSensor) {
                            if (menuHelper.switchSensor.isChecked()) {
                                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_sensor_close_alert),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_sensor_open_alert),Toast.LENGTH_SHORT).show();
                            }
                            menuHelper.switchSensor.setChecked(!menuHelper.switchSensor.isChecked());
                        }
                        if (info.switchLeftPad) {
                            if (menuHelper.switchHalfScreen.isChecked()) {
                                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_half_screen_disable),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_half_screen_enable),Toast.LENGTH_SHORT).show();
                            }
                            menuHelper.switchHalfScreen.setChecked(!menuHelper.switchHalfScreen.isChecked());
                        }
                        if (!info.autoKeep && !info.autoClick) {
                            for (int code : info.outputKeycode) {
                                InputBridge.sendEvent(menuHelper.launcher,code,true);
                            }
                        }
                        if (!info.autoKeep && info.autoClick) {
                            clickHandler.post(clickRunnable);
                        }
                        if (info.autoKeep) {
                            if (isKeeping) {
                                setNormalDrawable();
                            }
                            else {
                                setPressDrawable();
                            }
                            if (info.autoClick) {
                                if (isKeeping) {
                                    clickHandler.removeCallbacks(clickRunnable);
                                    for (int code : info.outputKeycode) {
                                        InputBridge.sendEvent(menuHelper.launcher,code,false);
                                    }
                                }
                                else {
                                    clickHandler.post(clickRunnable);
                                }
                            }
                            else {
                                if (isKeeping) {
                                    for (int code : info.outputKeycode) {
                                        InputBridge.sendEvent(menuHelper.launcher,code,false);
                                    }
                                }
                                else {
                                    for (int code : info.outputKeycode) {
                                        InputBridge.sendEvent(menuHelper.launcher,code,true);
                                    }
                                }
                            }
                            isKeeping = !isKeeping;
                        }
                        ArrayList<String> childNames = new ArrayList<>();
                        for (ChildLayout childLayout : SettingUtils.getChildList(info.pattern)){
                            childNames.add(childLayout.name);
                        }
                        for (String child : info.visibilityControl) {
                            if (childNames.contains(child)) {
                                menuHelper.viewManager.setChildVisibility(child);
                            }
                            else {
                                info.visibilityControl.remove(child);
                                saveButtonInfo();
                            }
                        }
                        if (info.outputText != null && !info.outputText.equals("")) {
                            if (menuHelper.gameCursorMode == 0) {
                                for(int i = 0; i < info.outputText.length(); i++){
                                    InputBridge.sendKeyChar(menuHelper.launcher,info.outputText.charAt(i));
                                }
                            }
                            else {
                                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_T, true);
                                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_T, false);
                                new Handler().postDelayed(() -> {
                                    for(int i = 0; i < info.outputText.length(); i++){
                                        InputBridge.sendKeyChar(menuHelper.launcher,info.outputText.charAt(i));
                                    }
                                    InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_ENTER, true);
                                    InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_ENTER, false);
                                },50);
                            }
                        }
                        if (info.showInputDialog) {
                            if (!menuHelper.gameMenuSetting.advanceInput) {
                                menuHelper.touchCharInput.switchKeyboardState();
                            }
                            else {
                                InputDialog dialog = new InputDialog(getContext(),menuHelper);
                                dialog.show();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (info.viewMove && menuHelper.gameCursorMode == 1) {
                            menuHelper.viewManager.setGamePointer(info.uuid,true,event.getRawX() - initialX,event.getRawY() - initialY);
                        }
                        if (info.movable) {
                            float targetX;
                            float targetY;
                            if (initialPositionX + event.getRawX() - initialX >= 0 && initialPositionX + event.getRawX() - initialX <= screenWidth - getWidth()){
                                targetX = initialPositionX + event.getRawX() - initialX;
                            }
                            else if (initialPositionX + event.getRawX() - initialX < 0){
                                targetX = 0;
                            }
                            else {
                                targetX = screenWidth - getWidth();
                            }
                            if (initialPositionY + event.getRawY() - initialY >= 0 && initialPositionY + event.getRawY() - initialY <= screenHeight - getHeight()){
                                targetY = initialPositionY + event.getRawY() - initialY;
                            }
                            else if (initialPositionY + event.getRawY() - initialY < 0){
                                targetY = 0;
                            }
                            else {
                                targetY = screenHeight - getHeight();
                            }
                            setX(targetX);
                            setY(targetY);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (info.viewMove && menuHelper.gameCursorMode == 1) {
                            menuHelper.viewManager.setGamePointer(info.uuid,false,event.getRawX() - initialX,event.getRawY() - initialY);
                        }
                        if (!info.autoKeep && !info.autoClick) {
                            for (int code : info.outputKeycode) {
                                InputBridge.sendEvent(menuHelper.launcher,code,false);
                            }
                        }
                        if (!info.autoKeep && info.autoClick) {
                            clickHandler.removeCallbacks(clickRunnable);
                            for (int code : info.outputKeycode) {
                                InputBridge.sendEvent(menuHelper.launcher,code,false);
                            }
                        }
                        if (!info.autoKeep) {
                            setNormalDrawable();
                        }
                        break;
                }
            }
            if (info.functionType == 1) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!info.autoKeep) {
                            setPressDrawable();
                        }
                        initialX = event.getRawX();
                        initialY = event.getRawY();
                        initialPositionX = getX();
                        initialPositionY = getY();
                        clickCount++;
                        if (clickCount == 1) {
                            firstClickTime = System.currentTimeMillis();
                        }
                        if (clickCount == 2) {
                            if (System.currentTimeMillis() - firstClickTime <= 500) {
                                handleDoubleClick();
                                clickCount = 0;
                            }
                            else {
                                firstClickTime = System.currentTimeMillis();
                                clickCount = 1;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (info.viewMove && menuHelper.gameCursorMode == 1) {
                            menuHelper.viewManager.setGamePointer(info.uuid,true,event.getRawX() - initialX,event.getRawY() - initialY);
                        }
                        if (info.movable) {
                            float targetX;
                            float targetY;
                            if (initialPositionX + event.getRawX() - initialX >= 0 && initialPositionX + event.getRawX() - initialX <= screenWidth - getWidth()){
                                targetX = initialPositionX + event.getRawX() - initialX;
                            }
                            else if (initialPositionX + event.getRawX() - initialX < 0){
                                targetX = 0;
                            }
                            else {
                                targetX = screenWidth - getWidth();
                            }
                            if (initialPositionY + event.getRawY() - initialY >= 0 && initialPositionY + event.getRawY() - initialY <= screenHeight - getHeight()){
                                targetY = initialPositionY + event.getRawY() - initialY;
                            }
                            else if (initialPositionY + event.getRawY() - initialY < 0){
                                targetY = 0;
                            }
                            else {
                                targetY = screenHeight - getHeight();
                            }
                            setX(targetX);
                            setY(targetY);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (info.viewMove && menuHelper.gameCursorMode == 1) {
                            menuHelper.viewManager.setGamePointer(info.uuid,false,event.getRawX() - initialX,event.getRawY() - initialY);
                        }
                        if (!info.autoKeep) {
                            setNormalDrawable();
                        }
                        break;
                }
            }
        }
        return true;
    }

    private void handleDoubleClick() {
        if (info.openMenu) {
            menuHelper.drawerLayout.openDrawer(GravityCompat.END,true);
            menuHelper.drawerLayout.openDrawer(GravityCompat.START,true);
        }
        if (info.switchTouchMode) {
            if (menuHelper.gameMenuSetting.touchMode == 0) {
                menuHelper.spinnerTouchMode.setSelection(1);
                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_touch_mode_attack_alert),Toast.LENGTH_SHORT).show();
            }
            else {
                menuHelper.spinnerTouchMode.setSelection(0);
                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_touch_mode_create_alert),Toast.LENGTH_SHORT).show();
            }
        }
        if (info.switchSensor) {
            if (menuHelper.switchSensor.isChecked()) {
                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_sensor_close_alert),Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_sensor_open_alert),Toast.LENGTH_SHORT).show();
            }
            menuHelper.switchSensor.setChecked(!menuHelper.switchSensor.isChecked());
        }
        if (info.switchLeftPad) {
            if (menuHelper.switchHalfScreen.isChecked()) {
                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_half_screen_disable),Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(),getContext().getString(R.string.drawer_game_menu_control_half_screen_enable),Toast.LENGTH_SHORT).show();
            }
            menuHelper.switchHalfScreen.setChecked(!menuHelper.switchHalfScreen.isChecked());
        }
        if (!info.autoKeep) {
            for (int code : info.outputKeycode) {
                InputBridge.sendEvent(menuHelper.launcher,code,true);
                InputBridge.sendEvent(menuHelper.launcher,code,false);
            }
        }
        if (info.autoKeep) {
            if (isKeeping) {
                setNormalDrawable();
            }
            else {
                setPressDrawable();
            }
            if (info.autoClick) {
                if (isKeeping) {
                    clickHandler.removeCallbacks(clickRunnable);
                    for (int code : info.outputKeycode) {
                        InputBridge.sendEvent(menuHelper.launcher,code,false);
                    }
                }
                else {
                    clickHandler.post(clickRunnable);
                }
            }
            else {
                if (isKeeping) {
                    for (int code : info.outputKeycode) {
                        InputBridge.sendEvent(menuHelper.launcher,code,false);
                    }
                }
                else {
                    for (int code : info.outputKeycode) {
                        InputBridge.sendEvent(menuHelper.launcher,code,true);
                    }
                }
            }
            isKeeping = !isKeeping;
        }
        ArrayList<String> childNames = new ArrayList<>();
        for (ChildLayout childLayout : SettingUtils.getChildList(info.pattern)){
            childNames.add(childLayout.name);
        }
        for (String child : info.visibilityControl) {
            if (childNames.contains(child)) {
                menuHelper.viewManager.setChildVisibility(child);
            }
            else {
                info.visibilityControl.remove(child);
                saveButtonInfo();
            }
        }
        if (info.outputText != null && !info.outputText.equals("")) {
            if (menuHelper.gameCursorMode == 0) {
                for(int i = 0; i < info.outputText.length(); i++){
                    InputBridge.sendKeyChar(menuHelper.launcher,info.outputText.charAt(i));
                }
            }
            else {
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_T, true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_T, false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0; i < info.outputText.length(); i++){
                            InputBridge.sendKeyChar(menuHelper.launcher,info.outputText.charAt(i));
                        }
                        InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_ENTER, true);
                        InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_ENTER, false);
                    }
                },50);
            }
        }
        if (info.showInputDialog) {
            if (!menuHelper.gameMenuSetting.advanceInput) {
                menuHelper.touchCharInput.switchKeyboardState();
            }
            else {
                InputDialog dialog = new InputDialog(getContext(),menuHelper);
                dialog.show();
            }
        }
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

    public void refresh() {
        invalidate();
    }

    public void refreshStyle (BaseButtonInfo info) {
        this.info = info;
        drawableNormal = new GradientDrawable();
        drawablePress = new GradientDrawable();
        drawableNormal.setCornerRadius(ConvertUtils.dip2px(getContext(),info.buttonStyle.cornerRadius));
        drawableNormal.setStroke(ConvertUtils.dip2px(getContext(),info.buttonStyle.strokeWidth), Color.parseColor(info.buttonStyle.strokeColor));
        drawableNormal.setColor(Color.parseColor(info.buttonStyle.fillColor));
        drawablePress.setCornerRadius(ConvertUtils.dip2px(getContext(),info.buttonStyle.cornerRadiusPress));
        drawablePress.setStroke(ConvertUtils.dip2px(getContext(),info.buttonStyle.strokeWidthPress), Color.parseColor(info.buttonStyle.strokeColorPress));
        drawablePress.setColor(Color.parseColor(info.buttonStyle.fillColorPress));
        this.setText(info.text);
        this.setGravity(Gravity.CENTER);
        this.setPadding(0,0,0,0);
        this.setAllCaps(false);
        setNormalDrawable();
    }

    public void setNormalDrawable(){
        setTextSize(info.buttonStyle.textSize);
        setTextColor(Color.parseColor(info.buttonStyle.textColor));
        setBackground(drawableNormal);
    }

    public void setPressDrawable(){
        setTextSize(info.buttonStyle.textSizePress);
        setTextColor(Color.parseColor(info.buttonStyle.textColorPress));
        setBackground(drawablePress);
    }

    public void updateSizeAndPosition (BaseButtonInfo info) {
        this.info = info;
        int width;
        int height;
        if (info.sizeType == SIZE_TYPE_PERCENT){
            if (info.width.object == SIZE_OBJECT_WIDTH){
                width = (int) (screenWidth * info.width.percentSize);
            }
            else {
                width = (int) (screenHeight * info.width.percentSize);
            }
            if (info.height.object == SIZE_OBJECT_WIDTH){
                height = (int) (screenWidth * info.height.percentSize);
            }
            else {
                height = (int) (screenHeight * info.height.percentSize);
            }
        }
        else {
            width = ConvertUtils.dip2px(getContext(),info.width.absoluteSize);
            height = ConvertUtils.dip2px(getContext(),info.height.absoluteSize);
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        if (info.positionType == POSITION_TYPE_PERCENT){
            setX((screenWidth - width) * info.xPosition.percentPosition);
            setY((screenHeight - height) * info.yPosition.percentPosition);
        }
        else {
            setX(ConvertUtils.dip2px(getContext(),info.xPosition.absolutePosition));
            setY(ConvertUtils.dip2px(getContext(),info.yPosition.absolutePosition));
        }
    }

    public void saveButtonInfo() {
        if (menuHelper.editMode) {
            ChildLayout childLayout = null;
            for (ChildLayout child : SettingUtils.getChildList(info.pattern)) {
                if (child.name.equals(menuHelper.currentChild)) {
                    childLayout = child;
                }
            }
            assert childLayout != null;
            boolean exist = false;
            for (int i = 0;i < childLayout.baseButtonList.size();i++) {
                if (childLayout.baseButtonList.get(i).uuid.equals(info.uuid)) {
                    childLayout.baseButtonList.get(i).refresh(info);
                    exist = true;
                }
            }
            if (!exist) {
                childLayout.baseButtonList.add(info);
            }
            ChildLayout.saveChildLayout(info.pattern,childLayout);
        }
    }

    public void deleteButton () {
        if (menuHelper.editMode) {
            ChildLayout childLayout = null;
            for (ChildLayout child : SettingUtils.getChildList(info.pattern)) {
                if (child.name.equals(menuHelper.currentChild)) {
                    childLayout = child;
                }
            }
            assert childLayout != null;
            for (int i = 0;i < childLayout.baseButtonList.size();i++) {
                if (childLayout.baseButtonList.get(i).uuid.equals(info.uuid)) {
                    childLayout.baseButtonList.remove(i);
                    break;
                }
            }
            ChildLayout.saveChildLayout(info.pattern,childLayout);
            menuHelper.viewManager.layoutPanel.removeView(this);
        }
    }
}
