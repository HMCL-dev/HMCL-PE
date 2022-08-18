package com.tungsten.hmclpe.control.bean;

import androidx.annotation.NonNull;

public class ViewPosition implements Cloneable {

    public int absolutePosition;
    public float percentPosition;

    public ViewPosition(int absolutePosition, float percentPosition) {
        this.absolutePosition = absolutePosition;
        this.percentPosition = percentPosition;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
