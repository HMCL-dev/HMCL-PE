package com.tungsten.hmclpe.control.bean.rocker;

import androidx.annotation.NonNull;

public class RockerSize implements Cloneable {

    public int absoluteSize;
    public float percentSize;
    public int object;

    public RockerSize (int absoluteSize,float percentSize,int object) {
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
