package com.tungsten.hmclpe.control.bean.button;

import androidx.annotation.NonNull;

public class ButtonSize implements Cloneable {

    public int absoluteSize;
    public float percentSize;
    public int object;

    public ButtonSize (int absoluteSize,float percentSize,int object) {
        this.absoluteSize = absoluteSize;
        this.percentSize = percentSize;
        this.object = object;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
