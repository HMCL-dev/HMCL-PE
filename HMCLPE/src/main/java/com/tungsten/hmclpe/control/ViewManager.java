package com.tungsten.hmclpe.control;

import static android.content.Context.SENSOR_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import androidx.core.view.GravityCompat;

import com.google.gson.Gson;
import com.tungsten.hmclpe.control.bean.BaseButtonInfo;
import com.tungsten.hmclpe.control.bean.BaseRockerViewInfo;
import com.tungsten.hmclpe.control.view.BaseButton;
import com.tungsten.hmclpe.control.view.BaseRockerView;
import com.tungsten.hmclpe.control.view.LayoutPanel;
import com.tungsten.hmclpe.control.view.MenuFloat;
import com.tungsten.hmclpe.control.view.MenuView;
import com.tungsten.hmclpe.control.view.TouchPad;
import com.tungsten.hmclpe.launcher.list.local.controller.ChildLayout;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.launcher.setting.game.GameMenuSetting;
import com.tungsten.hmclpe.utils.file.FileStringUtils;

import java.util.ArrayList;

public class ViewManager implements SensorEventListener {

    public Context context;
    public Activity activity;
    public MenuHelper menuHelper;
    public LayoutPanel layoutPanel;
    public int launcher;
    public int screenWidth;
    public int screenHeight;

    public TouchPad touchPad;

    public MenuFloat menuFloat;
    public MenuView menuView;

    public SensorManager sensorManager;
    public Sensor sensor;
    public static final float NS2S = 1.0f / 1000000000.0f;
    public float timestamp;
    public float[] angle = new float[3];

    public String viewMovingType = "0";

    public ViewManager (Context context, Activity activity, MenuHelper menuHelper, LayoutPanel layoutPanel,int launcher) {
        this.context = context;
        this.activity = activity;
        this.menuHelper = menuHelper;
        this.layoutPanel = layoutPanel;
        this.launcher = launcher;

        screenWidth = layoutPanel.getWidth();
        screenHeight = layoutPanel.getHeight();
        init();
    }

    private void init(){
        /*
         *初始化触控板和陀螺仪
         */
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        setSensorEnable(menuHelper.gameMenuSetting.enableSensor);
        touchPad = new TouchPad(context,launcher,layoutPanel.getWidth(),layoutPanel.getHeight(),menuHelper);
        layoutPanel.addView(touchPad);
        /*
         *初始化菜单键
         */
        menuFloat = new MenuFloat(context,menuHelper,layoutPanel.getWidth(),layoutPanel.getHeight(),menuHelper.gameMenuSetting.menuFloatSetting.positionX,menuHelper.gameMenuSetting.menuFloatSetting.positionY);
        menuFloat.addCallback(new MenuFloat.MenuFloatCallback() {
            @Override
            public void onClick() {
                menuHelper.drawerLayout.openDrawer(GravityCompat.END,true);
                menuHelper.drawerLayout.openDrawer(GravityCompat.START,true);
            }

            @Override
            public void onMove(float xPosition, float yPosition) {
                menuHelper.gameMenuSetting.menuFloatSetting.positionX = xPosition;
                menuHelper.gameMenuSetting.menuFloatSetting.positionY = yPosition;
                GameMenuSetting.saveGameMenuSetting(menuHelper.gameMenuSetting);
            }
        });
        menuView = new MenuView(context,menuHelper,layoutPanel.getWidth(),layoutPanel.getHeight(),menuHelper.gameMenuSetting.menuViewSetting.mode,menuHelper.gameMenuSetting.menuViewSetting.yPercent);
        menuView.addCallback(new MenuView.MenuCallback() {
            @Override
            public void onRelease() {
                menuHelper.drawerLayout.openDrawer(GravityCompat.END,true);
                menuHelper.drawerLayout.openDrawer(GravityCompat.START,true);
            }

            @Override
            public void onMoveModeStart() {

            }

            @Override
            public void onMove(int mode, float yPercent) {
                menuHelper.gameMenuSetting.menuViewSetting.mode = mode;
                menuHelper.gameMenuSetting.menuViewSetting.yPercent = yPercent;
                GameMenuSetting.saveGameMenuSetting(menuHelper.gameMenuSetting);
            }

            @Override
            public void onMoveModeStop() {

            }
        });
        if (menuHelper.gameMenuSetting.menuFloatSetting.enable){
            layoutPanel.addView(menuFloat);
        }
        if (menuHelper.gameMenuSetting.menuViewSetting.enable){
            layoutPanel.addView(menuView);
        }

        refreshLayout(menuHelper.currentPattern.name,menuHelper.currentChild,menuHelper.editMode);
    }

    public void addButton (BaseButtonInfo baseButtonInfo,int visibility) {
        BaseButton baseButton = new BaseButton(context,screenWidth,screenHeight,baseButtonInfo,menuHelper);
        baseButton.setIsShowing(visibility == View.VISIBLE);
        layoutPanel.addView(baseButton);
        baseButton.updateSizeAndPosition(baseButtonInfo);
        if (menuHelper.editMode) {
            baseButton.saveButtonInfo();
        }
    }

    public void loadButton (BaseButtonInfo baseButtonInfo,int visibility) {
        BaseButton baseButton = new BaseButton(context,screenWidth,screenHeight,baseButtonInfo,menuHelper);
        baseButton.setIsShowing(visibility == View.VISIBLE);
        layoutPanel.addView(baseButton);
        baseButton.updateSizeAndPosition(baseButtonInfo);
    }

    public void addRocker (BaseRockerViewInfo baseRockerViewInfo,int visibility) {
        BaseRockerView baseRockerView = new BaseRockerView(context,screenWidth,screenHeight,baseRockerViewInfo,menuHelper);
        baseRockerView.setIsShowing(visibility == View.VISIBLE);
        layoutPanel.addView(baseRockerView);
        baseRockerView.updateSizeAndPosition(baseRockerViewInfo);
        if (menuHelper.editMode) {
            baseRockerView.saveRockerInfo();
        }
    }

    public void loadRocker (BaseRockerViewInfo baseRockerViewInfo,int visibility) {
        BaseRockerView baseRockerView = new BaseRockerView(context,screenWidth,screenHeight,baseRockerViewInfo,menuHelper);
        baseRockerView.setIsShowing(visibility == View.VISIBLE);
        layoutPanel.addView(baseRockerView);
        baseRockerView.updateSizeAndPosition(baseRockerViewInfo);
    }

    public void refreshViews() {
        for (int i = 0;i < layoutPanel.getChildCount();i++) {
            if (layoutPanel.getChildAt(i) instanceof BaseButton){
                ((BaseButton) layoutPanel.getChildAt(i)).refresh();
            }
        }
    }

    public void refreshLayout (String pattern,String child,boolean editMode) {
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0;i < layoutPanel.getChildCount();i++) {
            if (layoutPanel.getChildAt(i) instanceof BaseButton || layoutPanel.getChildAt(i) instanceof BaseRockerView){
                views.add(layoutPanel.getChildAt(i));
            }
        }
        for (View v : views) {
            layoutPanel.removeView(v);
        }
        if (SettingUtils.getChildList(pattern).size() > 0) {
            if (editMode) {
                String string = FileStringUtils.getStringFromFile(AppManifest.CONTROLLER_DIR + "/" + pattern + "/" + child + ".json");
                Gson gson = new Gson();
                ChildLayout childLayout = gson.fromJson(string, ChildLayout.class);
                for (BaseButtonInfo buttonInfo : childLayout.baseButtonList) {
                    loadButton(buttonInfo, View.VISIBLE);
                }
                for (BaseRockerViewInfo rockerViewInfo : childLayout.baseRockerViewList) {
                    loadRocker(rockerViewInfo, View.VISIBLE);
                }
            }
            else {
                ArrayList<ChildLayout> childLayouts = SettingUtils.getChildList(pattern);
                for (ChildLayout layout : childLayouts) {
                    for (BaseButtonInfo buttonInfo : layout.baseButtonList) {
                        loadButton(buttonInfo,layout.visibility);
                    }
                    for (BaseRockerViewInfo rockerViewInfo : layout.baseRockerViewList) {
                        loadRocker(rockerViewInfo,layout.visibility);
                    }
                }
            }
        }
        hideUI(menuHelper.gameMenuSetting.hideUI);
    }

    public void setChildVisibility(String child) {
        for (int i = 0;i < layoutPanel.getChildCount();i++) {
            if (layoutPanel.getChildAt(i) instanceof BaseButton){
                if (((BaseButton) layoutPanel.getChildAt(i)).info.child.equals(child)) {
                    ((BaseButton) layoutPanel.getChildAt(i)).setIsShowing(!((BaseButton) layoutPanel.getChildAt(i)).getIsShowing());
                }
            }
            if (layoutPanel.getChildAt(i) instanceof BaseRockerView){
                if (((BaseRockerView) layoutPanel.getChildAt(i)).info.child.equals(child)) {
                    ((BaseRockerView) layoutPanel.getChildAt(i)).setIsShowing(!((BaseRockerView) layoutPanel.getChildAt(i)).getIsShowing());
                }
            }
        }
    }

    public void hideUI(boolean b) {
        for (int i = 0;i < layoutPanel.getChildCount();i++) {
            if (!(layoutPanel.getChildAt(i) instanceof TouchPad)) {
                layoutPanel.getChildAt(i).setAlpha(b ? 0 : 1);
            }
        }
    }

    public void enableCursor() {
        if (touchPad != null){
            InputBridge.setPointer(launcher,(int) (menuHelper.cursorX * menuHelper.scaleFactor),(int) (menuHelper.cursorY * menuHelper.scaleFactor));
        }
        for (int i = 0;i < layoutPanel.getChildCount();i++) {
            if (layoutPanel.getChildAt(i) instanceof BaseButton){
                ((BaseButton) layoutPanel.getChildAt(i)).refreshVisibility();
            }
            if (layoutPanel.getChildAt(i) instanceof BaseRockerView){
                ((BaseRockerView) layoutPanel.getChildAt(i)).refreshVisibility();
            }
        }
    }

    public void disableCursor() {
        viewMovingType = "0";
        for (int i = 0;i < layoutPanel.getChildCount();i++) {
            if (layoutPanel.getChildAt(i) instanceof BaseButton){
                ((BaseButton) layoutPanel.getChildAt(i)).refreshVisibility();
            }
            if (layoutPanel.getChildAt(i) instanceof BaseRockerView){
                ((BaseRockerView) layoutPanel.getChildAt(i)).refreshVisibility();
            }
        }
    }

    public void setSensorEnable(boolean enable) {
        if (enable) {
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            sensorManager.unregisterListener(this);
            if (menuHelper.gameCursorMode == 1){
                InputBridge.setPointer(launcher,(int) menuHelper.currentX,(int) menuHelper.currentY);
            }
        }
    }

    public void setGamePointer(String uuid,boolean isMoving,float deltaX,float deltaY) {
        if (viewMovingType.equals("0") || viewMovingType.equals(uuid)){
            if (!menuHelper.gameMenuSetting.enableSensor){
                InputBridge.setPointer(launcher,(int) (menuHelper.pointerX + deltaX * menuHelper.gameMenuSetting.mouseSpeed),(int) (menuHelper.pointerY + deltaY * menuHelper.gameMenuSetting.mouseSpeed));
            }
            menuHelper.currentX = menuHelper.pointerX + deltaX * menuHelper.gameMenuSetting.mouseSpeed;
            menuHelper.currentY = menuHelper.pointerY + deltaY * menuHelper.gameMenuSetting.mouseSpeed;
            viewMovingType = uuid;
            if (!isMoving){
                menuHelper.pointerX = menuHelper.pointerX + deltaX * menuHelper.gameMenuSetting.mouseSpeed;
                menuHelper.pointerY = menuHelper.pointerY + deltaY * menuHelper.gameMenuSetting.mouseSpeed;
                viewMovingType = "0";
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (menuHelper.gameCursorMode == 1){
            if (timestamp != 0){
                final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
                angle[0] += sensorEvent.values[0] * dT;
                angle[1] += sensorEvent.values[1] * dT;
                float angleX = (float) Math.toDegrees(angle[0]);
                float angleY = (float) Math.toDegrees(angle[1]);
                InputBridge.setPointer(launcher,(int) (menuHelper.currentX - angleX * menuHelper.gameMenuSetting.sensitivity),(int) (menuHelper.currentY + angleY * menuHelper.gameMenuSetting.sensitivity));
            }
            timestamp = sensorEvent.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
