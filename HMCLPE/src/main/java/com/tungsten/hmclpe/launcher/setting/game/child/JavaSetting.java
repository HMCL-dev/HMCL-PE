package com.tungsten.hmclpe.launcher.setting.game.child;

import androidx.annotation.NonNull;

public class JavaSetting implements Cloneable{

    public boolean autoSelect;
    public String name;

    public JavaSetting(boolean autoSelect,String name){
        this.autoSelect = autoSelect;
        this.name = name;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
