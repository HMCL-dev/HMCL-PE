package com.tungsten.hmclpe.control.bean;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.control.bean.rocker.RockerSize;
import com.tungsten.hmclpe.control.bean.rocker.RockerStyle;

public class BaseRockerViewInfo implements Cloneable{

    public static final int SHOW_TYPE_ALWAYS = 0;
    public static final int SHOW_TYPE_IN_GAME = 1;
    public static final int SHOW_TYPE_OUT_GAME = 2;

    public static final int SIZE_TYPE_PERCENT = 0;
    public static final int SIZE_TYPE_ABSOLUTE = 1;

    public static final int SIZE_OBJECT_WIDTH = 0;
    public static final int SIZE_OBJECT_HEIGHT = 1;

    public static final int POSITION_TYPE_PERCENT = 0;
    public static final int POSITION_TYPE_ABSOLUTE = 1;

    public static final int FUNCTION_FOLLOW_NONE = 0;
    public static final int FUNCTION_FOLLOW_CENTER = 1;
    public static final int FUNCTION_FOLLOW_ALL = 2;

    public String uuid;
    public String pattern;
    public String child;
    public int showType;
    public int sizeType;
    public RockerSize size;
    public int positionType;
    public ViewPosition xPosition;
    public ViewPosition yPosition;
    public int followType;
    public boolean shift;
    public boolean usingExist;
    public RockerStyle rockerStyle;

    public BaseRockerViewInfo (String uuid,String pattern,String child,int showType,int sizeType,RockerSize size,int positionType,ViewPosition xPosition,ViewPosition yPosition,int followType,boolean shift,boolean usingExist,RockerStyle rockerStyle) {
        this.uuid = uuid;
        this.pattern = pattern;
        this.child = child;
        this.showType = showType;
        this.sizeType = sizeType;
        this.size = size;
        this.positionType = positionType;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.followType = followType;
        this.shift = shift;
        this.usingExist = usingExist;
        this.rockerStyle = rockerStyle;
    }

    public void refresh(BaseRockerViewInfo info) {
        this.uuid = info.uuid;
        this.pattern = info.pattern;
        this.child = info.child;
        this.showType = info.showType;
        this.sizeType = info.sizeType;
        this.size = info.size;
        this.positionType = info.positionType;
        this.xPosition = info.xPosition;
        this.yPosition = info.yPosition;
        this.followType = info.followType;
        this.shift = info.shift;
        this.usingExist = info.usingExist;
        this.rockerStyle = info.rockerStyle;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        BaseRockerViewInfo baseRockerViewInfo;
        baseRockerViewInfo = (BaseRockerViewInfo) super.clone();
        baseRockerViewInfo.size = (RockerSize) size.clone();
        baseRockerViewInfo.xPosition = (ViewPosition) xPosition.clone();
        baseRockerViewInfo.yPosition = (ViewPosition) yPosition.clone();
        baseRockerViewInfo.rockerStyle = (RockerStyle) rockerStyle.clone();
        return baseRockerViewInfo;
    }

}
