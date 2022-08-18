package com.tungsten.hmclpe.control.bean.rocker;

import androidx.annotation.NonNull;

public class RockerStyle implements Cloneable {

    public String name;
    public int cornerRadius;
    public float strokeWidth;
    public String strokeColor;
    public String fillColor;
    public String pointerColor;
    public int cornerRadiusPress;
    public float strokeWidthPress;
    public String strokeColorPress;
    public String fillColorPress;
    public String pointerColorPress;

    public RockerStyle () {
        this ("",80,2.0f,"#1a000000","#00ffffff","#f6f6f6",80,2.0f,"#1a000000","#00ffffff","#40ffffff");
    }

    public RockerStyle (String name,int cornerRadius,float strokeWidth,String strokeColor,String fillColor,String pointerColor,int cornerRadiusPress,float strokeWidthPress,String strokeColorPress,String fillColorPress,String pointerColorPress) {
        this.name = name;
        this.cornerRadius = cornerRadius;
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.pointerColor = pointerColor;
        this.cornerRadiusPress = cornerRadiusPress;
        this.strokeWidthPress = strokeWidthPress;
        this.strokeColorPress = strokeColorPress;
        this.fillColorPress = fillColorPress;
        this.pointerColorPress = pointerColorPress;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
