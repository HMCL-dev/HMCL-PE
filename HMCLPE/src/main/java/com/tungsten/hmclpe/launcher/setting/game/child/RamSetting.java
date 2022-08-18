package com.tungsten.hmclpe.launcher.setting.game.child;

import androidx.annotation.NonNull;

public class RamSetting implements Cloneable {

    public int minRam;
    public int maxRam;
    public boolean autoRam;

    public RamSetting(int minRam,int maxRam,boolean autoRam){
        this.minRam = minRam;
        this.maxRam = maxRam;
        this.autoRam = autoRam;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
