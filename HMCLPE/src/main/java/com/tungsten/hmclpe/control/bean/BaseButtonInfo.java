package com.tungsten.hmclpe.control.bean;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.control.bean.button.ButtonSize;
import com.tungsten.hmclpe.control.bean.button.ButtonStyle;

import java.util.ArrayList;

public class BaseButtonInfo implements Cloneable {

    public static final int SHOW_TYPE_ALWAYS = 0;
    public static final int SHOW_TYPE_IN_GAME = 1;
    public static final int SHOW_TYPE_OUT_GAME = 2;

    public static final int SIZE_TYPE_PERCENT = 0;
    public static final int SIZE_TYPE_ABSOLUTE = 1;

    public static final int SIZE_OBJECT_WIDTH = 0;
    public static final int SIZE_OBJECT_HEIGHT = 1;

    public static final int POSITION_TYPE_PERCENT = 0;
    public static final int POSITION_TYPE_ABSOLUTE = 1;

    public static final int FUNCTION_TYPE_TOUCH = 0;
    public static final int FUNCTION_TYPE_DOUBLE_CLICK = 1;

    public String uuid;
    public String pattern;
    public String child;
    public String text;
    public int showType;
    public int sizeType;
    public ButtonSize width;
    public ButtonSize height;
    public int positionType;
    public ViewPosition xPosition;
    public ViewPosition yPosition;
    public int functionType;
    public boolean viewMove;
    public boolean autoKeep;
    public boolean autoClick;
    public boolean openMenu;
    public boolean movable;
    public boolean switchTouchMode;
    public boolean switchSensor;
    public boolean switchLeftPad;
    public boolean showInputDialog;
    public ArrayList<String> visibilityControl;
    public String outputText;
    public ArrayList<Integer> outputKeycode;
    public boolean usingExist;
    public ButtonStyle buttonStyle;

    public BaseButtonInfo(String uuid, String pattern, String child, String text, int showType, int sizeType, ButtonSize width, ButtonSize height, int positionType, ViewPosition xPosition, ViewPosition yPosition, int functionType, boolean viewMove, boolean autoKeep, boolean autoClick, boolean openMenu, boolean movable, boolean switchTouchMode, boolean switchSensor, boolean switchLeftPad, boolean showInputDialog, ArrayList<String> visibilityControl, String outputText, ArrayList<Integer> outputKeycode, boolean usingExist, ButtonStyle buttonStyle) {
        this.uuid = uuid;
        this.pattern = pattern;
        this.child = child;
        this.text = text;
        this.showType = showType;
        this.sizeType = sizeType;
        this.width = width;
        this.height = height;
        this.positionType = positionType;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.functionType = functionType;
        this.viewMove = viewMove;
        this.autoKeep = autoKeep;
        this.autoClick = autoClick;
        this.openMenu = openMenu;
        this.movable = movable;
        this.switchTouchMode = switchTouchMode;
        this.switchSensor = switchSensor;
        this.switchLeftPad = switchLeftPad;
        this.showInputDialog = showInputDialog;
        this.visibilityControl = visibilityControl;
        this.outputText = outputText;
        this.outputKeycode = outputKeycode;
        this.usingExist = usingExist;
        this.buttonStyle = buttonStyle;
    }

    public void refresh(BaseButtonInfo info) {
        this.uuid = info.uuid;
        this.pattern = info.pattern;
        this.child = info.child;
        this.text = info.text;
        this.showType = info.showType;
        this.sizeType = info.sizeType;
        this.width = info.width;
        this.height = info.height;
        this.positionType = info.positionType;
        this.xPosition = info.xPosition;
        this.yPosition = info.yPosition;
        this.functionType = info.functionType;
        this.viewMove = info.viewMove;
        this.autoKeep = info.autoKeep;
        this.autoClick = info.autoClick;
        this.openMenu = info.openMenu;
        this.movable = info.movable;
        this.switchTouchMode = info.switchTouchMode;
        this.switchSensor = info.switchSensor;
        this.switchLeftPad = info.switchLeftPad;
        this.showInputDialog = info.showInputDialog;
        this.visibilityControl = info.visibilityControl;
        this.outputText = info.outputText;
        this.outputKeycode = info.outputKeycode;
        this.usingExist = info.usingExist;
        this.buttonStyle = info.buttonStyle;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        BaseButtonInfo baseButtonInfo;
        baseButtonInfo = (BaseButtonInfo) super.clone();
        baseButtonInfo.width = (ButtonSize) width.clone();
        baseButtonInfo.height = (ButtonSize) height.clone();
        baseButtonInfo.xPosition = (ViewPosition) xPosition.clone();
        baseButtonInfo.yPosition = (ViewPosition) yPosition.clone();
        baseButtonInfo.visibilityControl = (ArrayList<String>) visibilityControl.clone();
        baseButtonInfo.outputKeycode = (ArrayList<Integer>) outputKeycode.clone();
        baseButtonInfo.buttonStyle = (ButtonStyle) buttonStyle.clone();
        return baseButtonInfo;
    }
}